package com.example.gan_image

import android.animation.ValueAnimator
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.gan_image.databinding.MakingImageActivityBinding
import com.example.gan_image.model.AI_image
import com.example.gan_image.model.AI_image_response
import com.example.gan_image.model.APIClient
import com.example.gan_image.model.makestoryElePost
import com.example.gan_image.model.storylistResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MakingImageActivity : AppCompatActivity() {
    val binding by lazy { MakingImageActivityBinding.inflate(layoutInflater)}
    val api=APIClient.create()
    // 받아온 text, image 문자열을 화면에 보여주고, 다음페이지를 누르면 서버로 post
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val pref = getSharedPreferences("token",0)
        var style=pref.getString("animation_style","")
        if (style==""){
            val edit = pref.edit()
            val style= "fantasy-art"

            edit.putString("animation_style", style)
            edit.apply()
        }
        style=pref.getString("animation_style","")
        val page = intent.getIntExtra("page", 1)
        //loading animation
        binding.loading.setAnimation("dino.json")
        binding.loading.playAnimation()

        //페이지 설정
        binding.currentPage.setText(page.toString())
        binding.allPage.setText(page.toString())

        //전 문장 가져오기
        var previous_text= getIntent().getStringExtra("previous_text")
        Log.d("previous_text", "전 문장 : " + previous_text)

        //var text= getIntent().getStringExtra("textResult")
        val ItemList=arrayListOf<String>()


        api.imageapi(AI_image(previous_text,style)).enqueue(object: Callback<AI_image_response> {
            override fun onFailure(call: Call<AI_image_response>, t: Throwable) {
                Log.d("log", t.message.toString())
                Log.d("log", "fail")
            }

            override fun onResponse(call: Call<AI_image_response>, response: Response<AI_image_response>) {
                if(response.isSuccessful){
                    var imagestring= response.body()!!
                    //base64(String) -> bitmap(bitmap)
                    var encodeByte= Base64.decode(imagestring.image_response,Base64.DEFAULT)
                    var bitmapDecode= BitmapFactory.decodeByteArray(encodeByte,0,encodeByte.size)

                    binding.image.setImageBitmap(bitmapDecode)
                    //Log.d("log","onResponse 성공: "+bitmapDecode)
                    binding.sentence.setText(imagestring?.text_response)

                    var completeText=imagestring.text_response
                    var completeImage=imagestring.image_response
                    ItemList.add(completeText)
                    ItemList.add(completeImage)

                    binding.loading.visibility = View.INVISIBLE
                }else{
                    Log.d("log","onResponse 실패")
                    Toast.makeText(this@MakingImageActivity, "이미지 실패", Toast.LENGTH_SHORT).show()
                }
            }

        })

        //이미지 생성에서 뒤로가기 누르면 해당 이미지 생성까지 저장 -> 제목 팝업
        binding.backBtn.setOnClickListener {
            val pref = getSharedPreferences("token",0)
            var token = pref.getString("token", "")
            // 앞의 화면에서 넘겨받은 id
            val pref2 = getSharedPreferences("storyID",0)
            var storyID = pref2.getInt("storyID",0)

            val style=pref.getString("animation-style", "")
            // 이미지, text 서버 전송
            var text=ItemList[0]
            var image=ItemList[1]
            api.makeStoryEle("Bearer $token", makestoryElePost(storyID,text, image)).enqueue(object: Callback<storylistResponse> {
                override fun onResponse(call: Call<storylistResponse>, response: Response<storylistResponse>) {
                    if(response.isSuccessful){
                        Log.d("log","response: "+response.body())
                    }
                }
                override fun onFailure(call: Call<storylistResponse>, t: Throwable) {
                    Log.d("log", "fail")
                }
            })
            // 저장돼있던 이미지 스타일 초기화
            val editor=pref.edit()
            editor.remove("animation_style")
            editor.apply()

            val intent = Intent(applicationContext, TitlePopupActivity::class.java)
            startActivity(intent)
        }


        //다음 페이지 버튼
        binding.nextBtn.setOnClickListener {
            val pref = getSharedPreferences("token",0)
            var token = pref.getString("token", "")
            // 앞의 화면에서 넘겨받은 id
            val pref2 = getSharedPreferences("storyID",0)
            var storyID = pref2.getInt("storyID",0)


            // 이미지, text 서버 전송
            var text=ItemList[0]
            var image=ItemList[1]
            api.makeStoryEle("Bearer $token", makestoryElePost(storyID,text, image)).enqueue(object: Callback<storylistResponse> {
                override fun onResponse(call: Call<storylistResponse>, response: Response<storylistResponse>) {
                    if(response.isSuccessful){
                        Log.d("log","response: "+response.body())
                    }
                }
                override fun onFailure(call: Call<storylistResponse>, t: Throwable) {
                    Log.d("log", "fail")
                }

            })
            val intent = Intent(applicationContext, NextSentenceActivity::class.java)
            intent.putExtra("previous_text",text)
            intent.putExtra("page",page+1) //페이지 정보 넘겨줌
            startActivity(intent)
        }

        //끝내기 버튼
        binding.endBtn.setOnClickListener{
            val pref = getSharedPreferences("token",0)
            var token = pref.getString("token", "")
            // 앞의 화면에서 넘겨받은 id
            val pref2 = getSharedPreferences("storyID",0)
            var storyID = pref2.getInt("storyID",0)

            // 이미지, text 서버 전송
            var text=ItemList[0]
            var image=ItemList[1]
            api.makeStoryEle("Bearer $token", makestoryElePost(storyID,text, image)).enqueue(object: Callback<storylistResponse> {
                override fun onResponse(call: Call<storylistResponse>, response: Response<storylistResponse>) {
                    if(response.isSuccessful){
                        Log.d("log","response: "+response.body())
                    }
                }
                override fun onFailure(call: Call<storylistResponse>, t: Throwable) {
                    Log.d("log", "fail")
                }
            })
            // 저장돼있던 이미지 스타일 초기화
            val editor=pref.edit()
            editor.remove("animation_style")
            editor.apply()

            val intent = Intent(applicationContext, TitlePopupActivity::class.java)
            startActivity(intent)
        }
    }
}
