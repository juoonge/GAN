package com.example.gan_image

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.gan_image.databinding.ChangeActivityBinding
import com.example.gan_image.model.APIClient
import com.example.gan_image.model.storydetailResponse
import com.example.gan_image.model.storylistResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Base64

class ChangeActivity : AppCompatActivity() {
    val binding by lazy{ ChangeActivityBinding.inflate(layoutInflater)}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //책 id 가져오기
        var storyID= getIntent().getIntExtra("storyID",1)
        var storytitle_string= getIntent().getStringExtra("storytitle").toString()

        //뒤로가기 버튼 --> ClickBookActivity 화면
        val backButton = findViewById<View>(R.id.back_btn) as ImageButton
        backButton.setOnClickListener {
            val intent = Intent(applicationContext, ClickBookActivity::class.java)
            intent.putExtra("storyID",storyID)
            intent.putExtra("storytitle",storytitle_string)
            startActivity(intent)
        }

        //제목 & 표지 수정 버튼 --> 제목 & 표지 수정 화면
        val titleChangeButton = findViewById<View>(R.id.title_change_btn) as Button
        titleChangeButton.setOnClickListener {
            val intent = Intent(applicationContext, TitleChangeActivity::class.java)
            intent.putExtra("storyID",storyID)
            startActivity(intent)
        }

        //내용 수정 버튼 버튼 --> 내용 수정 화면
        val contentChangeButton = findViewById<View>(R.id.content_change_btn) as Button
        contentChangeButton.setOnClickListener {
            val intent = Intent(applicationContext, ContentChangeActivity::class.java)
            intent.putExtra("storyID",storyID)
            startActivity(intent)
        }
        val api= APIClient.create()
        val pref = getSharedPreferences("token",0)
        var token = pref.getString("token", "")
        val ItemList = arrayListOf<storydetailResponse>()
        var title_num:String = "0"

        //그림 가져오기 (background 표지 그림 설정)
        api.getFairytaleDetail("Bearer $token",storyID).enqueue(object:
            Callback<storylistResponse> {
            override fun onResponse(
                call: Call<storylistResponse>,
                response: Response<storylistResponse>
            ) {
                val contentlist = response.body()?.contents
                //표지 번호 가져오기
                title_num = response.body()?.title_num.toString()

                if (contentlist != null) {
                    for (i in 0..contentlist.size - 1) {
                        var elementId = contentlist.get(i).elementId
                        var image = contentlist.get(i).image
                        var text = contentlist.get(i).text

                        Log.d("log", "elementid: " + elementId)
                        Log.d("log", "image: " + image)
                        Log.d("log", "text: " + text)

                        ItemList.add(storydetailResponse(elementId, image, text))
                    }
                }
                //배경 설정
                binding.background.setImageBitmap(stringToBitmap(ItemList[title_num.toInt()].image))
            }
            override fun onFailure(call: Call<storylistResponse>, t: Throwable) {
                Log.d("log","fail")
            }
        })

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun stringToBitmap(encodedString: String): Bitmap {

        //val encodeByte = Base64.decode()
        val encodeByte = Base64.getDecoder().decode(encodedString)
        return BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.size)
    }
}