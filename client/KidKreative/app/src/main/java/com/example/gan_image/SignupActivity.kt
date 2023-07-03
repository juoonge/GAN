package com.example.gan_image

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.gan_image.databinding.SignupActivityBinding
import com.example.gan_image.model.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: SignupActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main)
        binding = SignupActivityBinding.inflate(layoutInflater)
        val view = binding.root

        setContentView(view)

        val api= APIClient.create()

        //아이디 확인 버튼 클릭 시
        binding.idCheckBtn.setOnClickListener {
            //이미 존재하는 아이디 인지 확인
            var id = binding.id.text.toString()

            val data = CheckUser(id)
            api.check_user(data).enqueue(object: Callback<CheckUserResponse>{
                override fun onFailure(call: Call<CheckUserResponse>, t: Throwable) {
                    Log.d("CheckUser", t.cause.toString())
                    var dialog = AlertDialog.Builder(this@SignupActivity)
                    t.printStackTrace();
                    dialog.setTitle("error")
                    dialog.setMessage("fail")
                    dialog.show()
                }

                override fun onResponse(call: Call<CheckUserResponse>, response: Response<CheckUserResponse>) {
                    var check = response.body()
                    var dialog = AlertDialog.Builder(this@SignupActivity)

                    if (check?.msg == "SUCCESS") { //사용가능한 아이디일 경우
                        Toast.makeText(getApplicationContext(), "사용 가능한 아이디입니다", Toast.LENGTH_SHORT)
                            .show();
                    } else { //이미 등록된 아이디일 경우
                        Toast.makeText(getApplicationContext(),
                            "이미 존재하는 아이디입니다", Toast.LENGTH_SHORT).show();
                    }
                }
            })
        }

        //완료 버튼 클릭 시 (회원가입)
        binding.joinBtn.setOnClickListener{
            var id = binding.id.text.toString()
            var password = binding.password.text.toString()
            var password2 = binding.checkPassword.text.toString()
            var nickname = binding.nickName.text.toString()

            if(id == "" || password == "" || password2 == "" || nickname == ""){ //공백 있을 경우
                Toast.makeText(getApplicationContext(), "빈칸을 확인해 주세요.", Toast.LENGTH_SHORT).show();
            }
            else if(password != password2){
                Toast.makeText(getApplicationContext(), "비밀번호를 확인해 주세요.", Toast.LENGTH_SHORT).show();
            }

            else{
                val data = Signup(id, password, password2, nickname)
                api.signup_user(data).enqueue(object: Callback<SignupResponse>{
                    override fun onFailure(call: Call<SignupResponse>, t: Throwable) {
                        Log.d("Signup", t.cause.toString())
                        var dialog = AlertDialog.Builder(this@SignupActivity)
                        t.printStackTrace();
                        dialog.setTitle("error")
                        dialog.setMessage("fail")
                        dialog.show()
                    }

                    override fun onResponse(call: Call<SignupResponse>, response: Response<SignupResponse>) {
                        var signup = response.body()
                        var dialog = AlertDialog.Builder(this@SignupActivity)

                        dialog.setMessage(signup?.msg)
                        dialog.show()

                        if (signup?.msg == "SUCCESS"){ //회원가입 성공시
                            //var intent = Intent(this@MainActivity, LoginActivity::class.java)
                            //intent.putExtra("id",id) //아이디 정보 같이 넘겨줌
                            //startActivity(intent)
                            Toast.makeText(getApplicationContext(), "회원가입 성공!", Toast.LENGTH_SHORT).show();

                            //로그인 화면으로 이동
                            val intent = Intent(applicationContext, StartActivity::class.java)
                            startActivity(intent)

                        }
                    }
                })
            }
        }

        //뒤로가기 버튼
        binding.backBtn.setOnClickListener{
            val intent = Intent(applicationContext, StartActivity::class.java)
            startActivity(intent)
        }
    }
}