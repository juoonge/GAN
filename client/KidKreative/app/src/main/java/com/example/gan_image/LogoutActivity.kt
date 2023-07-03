package com.example.gan_image

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.example.gan_image.databinding.LogoutActivityBinding

class LogoutActivity : AppCompatActivity() {
    val binding by lazy{ LogoutActivityBinding.inflate(layoutInflater)}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //아니요 버튼 --> 설정 화면
        binding.cancelBtn.setOnClickListener {
            val intent = Intent(applicationContext, SettingActivity::class.java)
            startActivity(intent)
        }

        //예 --> 로그아웃 --> 로그인 화면으로 전환
        binding.okBtn.setOnClickListener { //로그아웃
            val intent = Intent(applicationContext, StartActivity::class.java)
            startActivity(intent)
        }
    }
}