package com.example.gan_image

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.example.gan_image.databinding.SettingActivityBinding

class SettingActivity : AppCompatActivity() {
    val binding by lazy{ SettingActivityBinding.inflate(layoutInflater)}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        //뒤로가기 버튼
        binding.backBtn.setOnClickListener {
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
        }

        //닉네임 변경 버튼
        binding.nicknameChangeBtn.setOnClickListener {
            val intent = Intent(applicationContext, NicknameChangeActivity::class.java)
            startActivity(intent)
        }

        //로그아웃 버튼
        binding.logoutBtn.setOnClickListener {
            val intent = Intent(applicationContext, LogoutActivity::class.java)
            startActivity(intent)
        }
    }
}