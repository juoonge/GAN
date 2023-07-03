package com.example.gan_image

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.example.gan_image.databinding.MainActivityBinding
import com.example.gan_image.model.APIClient
import com.example.gan_image.model.ImageResponse
import com.example.gan_image.model.makestoryPost
import com.example.gan_image.model.storylistResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    val binding by lazy { MainActivityBinding.inflate(layoutInflater)}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        val api= APIClient.create()

        //보관함 버튼 선택
        val storageButton = findViewById<View>(R.id.storage_btn) as ImageButton
        storageButton.setOnClickListener {
            val intent = Intent(applicationContext, StorageBoxActivity::class.java)
            startActivity(intent)
        }


        //이야기 만들기 버튼 선택
        val makeStoryBtn = findViewById<View>(R.id.makeBtn)as ImageButton
        makeStoryBtn.setOnClickListener{
            val intent = Intent(applicationContext, FirstSentenceActivity::class.java)
            startActivity(intent)
        }

        //세팅 버튼 선택
        val settingButton = findViewById<View>(R.id.setting_btn) as ImageButton
        settingButton.setOnClickListener {
            val intent = Intent(applicationContext, SettingActivity::class.java)
            startActivity(intent)
        }
    }
}