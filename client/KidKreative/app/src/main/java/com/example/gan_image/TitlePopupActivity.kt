package com.example.gan_image

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.gan_image.databinding.TitlePopupActivityBinding
import com.example.gan_image.model.APIClient
import com.example.gan_image.model.makestoryPost
import com.example.gan_image.model.storylistResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TitlePopupActivity : AppCompatActivity() {
    val binding by lazy { TitlePopupActivityBinding.inflate(layoutInflater)}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val api= APIClient.create()


        //제목 입력
        binding.sendBtn.setOnClickListener {
            val intent = Intent(applicationContext, StorageBoxActivity::class.java)
            var storytitle=binding.title.text.toString()

            val pref = getSharedPreferences("token",0)
            var token = pref.getString("token", "")
            var id=pref.getString("id","").toString()

            val pref2 = getSharedPreferences("storyID",0)
            var storyID = pref2.getInt("storyID",0)

            api.putFairytaleTitle("Bearer $token",storyID,makestoryPost(storytitle,id,"0")).enqueue(object: Callback<storylistResponse> {
                override fun onResponse(
                    call: Call<storylistResponse>,
                    response: Response<storylistResponse>,
                ) {
                    if (response.isSuccessful) {
                        Log.d("log", "onResponse 성공 : "+response.body())
                    } else {
                        Log.d("log", "onResponse 실패")
                    }
                }
                override fun onFailure(call: Call<storylistResponse>, t: Throwable) {
                    Log.d("log", "fail")
                }
            })
            startActivity(intent)
        }

    }
}