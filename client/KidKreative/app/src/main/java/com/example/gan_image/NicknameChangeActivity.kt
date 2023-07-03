package com.example.gan_image

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.gan_image.databinding.NicknameChangeActivityBinding
import com.example.gan_image.model.APIClient
import com.example.gan_image.model.UpdateUser
import com.example.gan_image.model.UpdateUserResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NicknameChangeActivity : AppCompatActivity() {
    private lateinit var binding: NicknameChangeActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = NicknameChangeActivityBinding.inflate(layoutInflater)

        val view = binding.root
        setContentView(view)

        val api = APIClient.create()

        val pref = getSharedPreferences("token",0)
        val edit = pref.edit()
        var id = pref.getString("id", "")
        var token=pref.getString("token","")
        var before = pref.getString("nickname", "") //기존 닉네임
        binding.text.setText(before)

        binding.okBtn.setOnClickListener {//확인 버튼 눌렀을 때

            if (binding.text.toString() == "") { //공백인 경우
                Toast.makeText(getApplicationContext(), "빈칸을 확인해 주세요.", Toast.LENGTH_SHORT).show();
            }
            else {
                val data = UpdateUser(id!!, binding.text.text.toString(), token!!)
                api.update_user(data).enqueue(object : Callback<UpdateUserResponse> {
                    override fun onFailure(call: Call<UpdateUserResponse>, t: Throwable) {
                        Log.e("change", t.cause.toString())
                        var dialog = AlertDialog.Builder(this@NicknameChangeActivity)
                        dialog.setTitle("error")
                        dialog.setMessage("fail.")
                        dialog.show()
                    }

                    override fun onResponse(
                        call: Call<UpdateUserResponse>,
                        response: Response<UpdateUserResponse>
                    ) {
                        var res = response.body()
                        Log.d("log","res: "+res)

                        if (res?.msg == "SUCCESS") { //닉네임 변경 성공

                            edit.putString("nickname", binding.text.text.toString()) //닉네임 변경 정보 반영
                            edit.apply()
                            Toast.makeText(getApplicationContext(), "닉네임이 변경되었습니다.", Toast.LENGTH_SHORT).show();

                            val intent = Intent(applicationContext, MainActivity::class.java)
                            startActivity(intent)

                        } else { //닉네임 변경 실패
                            Toast.makeText(getApplicationContext(), "닉네임 변경 실패.", Toast.LENGTH_SHORT).show();
                        }

                    }
                })
            }
        }
        binding.cancelBtn.setOnClickListener {//취소 버튼 클릭

            val intent = Intent(applicationContext, SettingActivity::class.java)
            startActivity(intent)
        }
    }
}