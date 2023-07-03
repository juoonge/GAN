package com.example.gan_image

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.gan_image.databinding.LoginActivityBinding
import com.example.gan_image.model.APIClient
import com.example.gan_image.model.Login
import com.example.gan_image.model.LoginResponse
import io.github.muddz.styleabletoast.StyleableToast
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: LoginActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.login_activity)
        binding = LoginActivityBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val api= APIClient.create()

        //로그인 정보 저장
        val pref = getSharedPreferences("token",0)
        val edit = pref.edit()

        //start 버튼
        binding.loginBtn.setOnClickListener {
            //DB에 회원정보 일치하는지 확인
            //로그인 정보 저장
            var id = binding.id.text.toString()
            var pw = binding.password.text.toString()


            if (id == "" || pw == "") { //공백 있을 경우
                //Toast.makeText(getApplicationContext(), "빈칸을 확인해 주세요.", Toast.LENGTH_SHORT).show();
                this@LoginActivity.let { StyleableToast.makeText(it, "아이디를 확인해주세요", R.style.warningToast).show() }
            }
            else {
                val data = Login(id, pw)
                api.login_user(data).enqueue(object: Callback<LoginResponse> {
                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        Log.e("LOGIN", t.cause.toString())
                        var dialog = AlertDialog.Builder(this@LoginActivity)
                        dialog.setTitle("error")
                        dialog.setMessage("fail.")
                        dialog.show()
                    }

                    override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                        var login = response.body()
                        var dialog = AlertDialog.Builder(this@LoginActivity)

                        if (login?.id != null) { //로그인 성공시

                            // 로그인 후 받은 token을 sharedpreferences에 저장
                            val temp = JSONObject(login.token)
                            val id = login?.id
                            val token = temp.getString("access")
                            val nickname = temp.getString("nickname")

                            edit.putString("id", id) //id를 key 값으로 저장
                            edit.putString("token", token) //토큰을 key 값으로 저장
                            edit.putString("nickname", nickname) //닉네임을 key 값으로 저장
                            edit.apply()

                            //메인 화면으로 전환
                            val intent = Intent(applicationContext, MainActivity::class.java)
                            intent.putExtra("token", pref.getString("token", "")) //아이디 정보 같이 넘겨줌
                            startActivity(intent)

                        }
                        else{
                            Toast.makeText(getApplicationContext(), "로그인 실패", Toast.LENGTH_SHORT).show();
                        }
                    }
                })

            }
        }
        //뒤로가기 버튼
        binding.backBtn.setOnClickListener {
            val intent = Intent(applicationContext, StartActivity::class.java)
            startActivity(intent)
        }
    }
}