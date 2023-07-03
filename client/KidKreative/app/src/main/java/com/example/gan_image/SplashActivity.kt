package com.example.gan_image

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.example.gan_image.databinding.SplashActivityBinding

class SplashActivity : AppCompatActivity() {
    val binding by lazy{ SplashActivityBinding.inflate(layoutInflater)}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        moveLogin(2) //2초후에 로그인 화면으로 넘어감
    }

    private fun moveLogin(sec: Int) {
        Handler().postDelayed({
            //로그인 처음 화면으로 이동할 액티비티 생성
            val intent = Intent(applicationContext, StartActivity::class.java)
            startActivity(intent)
            finish() //현재 액티비티 종료
        }, (1000 * sec).toLong())
    }
}