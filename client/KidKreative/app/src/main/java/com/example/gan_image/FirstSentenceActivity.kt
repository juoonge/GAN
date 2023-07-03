package com.example.gan_image

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.gan_image.databinding.FirstsectenceActivityBinding
import com.example.gan_image.model.AI_image
import com.example.gan_image.model.AI_image_response
import com.example.gan_image.model.APIClient
import com.example.gan_image.model.Compatibility
import com.example.gan_image.model.CompatibilityResponse
import com.example.gan_image.model.ImageResponse
import com.example.gan_image.model.RecommendNext
import com.example.gan_image.model.RecommendNextResponse
import com.example.gan_image.model.makestoryElePost
import com.example.gan_image.model.makestoryPost
import com.example.gan_image.model.storylistResponse
import io.github.muddz.styleabletoast.StyleableToast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FirstSentenceActivity : AppCompatActivity() {
    val binding by lazy { FirstsectenceActivityBinding.inflate(layoutInflater)}
    val api= APIClient.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val pref = getSharedPreferences("token",0)

        //입력버튼 눌렀을 때
        binding.sendBtn.setOnClickListener {
            var text=binding.firstSentence.text.toString()

            // 동화책 만들기 시작, 제목은 일단 공백으로 보냄

            var token = pref.getString("token", "")
            var id=pref.getString("id","").toString()

            val intent = Intent(this, MakingImageActivity::class.java)
            intent.putExtra("previous_text",text)
            intent.putExtra("page",1) //페이지 정보 넘겨줌

            var text_check = Compatibility(text)

            //내용 적합성 판단
            api.check(text_check).enqueue(object: Callback<CompatibilityResponse> {
                override fun onFailure(call: Call<CompatibilityResponse>, t: Throwable) {
                    Log.e("recommend", t.cause.toString())
                    var dialog = AlertDialog.Builder(this@FirstSentenceActivity)
                    dialog.setTitle("에러")
                    dialog.setMessage("호출실패했습니다.")
                    dialog.show()
                }

                override fun onResponse(
                    call: Call<CompatibilityResponse>,
                    response: Response<CompatibilityResponse>
                ) {
                    if (response.body()?.code.equals("0000")){ //적합한 내용 코드
                        api.makeStory("Bearer $token",makestoryPost("NULL",id,"0")).enqueue(object: Callback<storylistResponse> {
                            override fun onResponse(
                                call: Call<storylistResponse>,
                                response: Response<storylistResponse>,
                            ) {
                                if (response.isSuccessful) {
                                    var storyId= response.body()?.id

                                    // 생성된 storyid를 반환받고, 해당 id를 넘겨 여기에 내용을 넣어줌
                                    var pref=getSharedPreferences("storyID",0)
                                    val edit = pref.edit()
                                    if (storyId != null) {
                                        edit.putInt("storyID", storyId)
                                        edit.apply()
                                    }
                                    startActivity(intent)
                                } else {
                                    Log.d("log", "onResponse 실패")
                                }
                            }
                            override fun onFailure(call: Call<storylistResponse>, t: Throwable) {
                                Log.d("log", "fail")
                            }
                        })

                    } else {
                        //Toast.makeText(this@FirstSentenceActivity, "동화내용으로 부적합 합니다", Toast.LENGTH_SHORT).show()
                        this@FirstSentenceActivity.let { StyleableToast.makeText(it, "동화내용으로 부적합 합니다", R.style.warningToast).show() }
                    }
                }
            })
        }

        //뒤로가기 버튼
        binding.backBtn.setOnClickListener {
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
        }

        //AI 추천받기 버튼
        binding.recommend.setOnClickListener{
            binding.loading.setAnimation("dino.json")
            binding.loading.visibility = View.VISIBLE
            binding.loading.playAnimation()

            var data = RecommendNext(binding.keyword.text.toString())

            api.first_recommend_next(data).enqueue(object: Callback<RecommendNextResponse> {
                override fun onFailure(call: Call<RecommendNextResponse>, t: Throwable) {
                    Log.e("recommend", t.cause.toString())
                    var dialog = AlertDialog.Builder(this@FirstSentenceActivity)
                    dialog.setTitle("에러")
                    dialog.setMessage("호출실패했습니다.")
                    dialog.show()
                    binding.loading.visibility = View.INVISIBLE
                }

                override fun onResponse(call: Call<RecommendNextResponse>, response: Response<RecommendNextResponse>) {
                    var signup = response.body()
                    binding.loading.visibility = View.INVISIBLE

                    binding.firstSentence.setText(signup?.msg.toString())

                }

            })
        }

        //fantasyArt - 판타지 그림체 선택
        binding.fantasyArt.setOnClickListener {
            //버튼 색상 변경
            binding.fantasyArt.setBackgroundResource(R.drawable.recommend_ai)
            binding.comicBook.setBackgroundResource(R.drawable.drawing_style)
            binding.anime.setBackgroundResource(R.drawable.drawing_style)
            binding.isometric.setBackgroundResource(R.drawable.drawing_style)
            binding.cinematic.setBackgroundResource(R.drawable.drawing_style)
            binding.origami.setBackgroundResource(R.drawable.drawing_style)
            val edit = pref.edit()
            val style= "fantasy-art"

            edit.putString("animation_style", style)
            edit.apply()
        }
        //comicBook - 만화 그림체 선택
        binding.comicBook.setOnClickListener {
            //버튼 색상 변경
            binding.fantasyArt.setBackgroundResource(R.drawable.drawing_style)
            binding.comicBook.setBackgroundResource(R.drawable.recommend_ai)
            binding.anime.setBackgroundResource(R.drawable.drawing_style)
            binding.isometric.setBackgroundResource(R.drawable.drawing_style)
            binding.cinematic.setBackgroundResource(R.drawable.drawing_style)
            binding.origami.setBackgroundResource(R.drawable.drawing_style)
            val edit = pref.edit()
            val style="comic-book"

            edit.putString("animation_style", style)
            edit.apply()
        }
        //anime - 애니 그림체 선택
        binding.anime.setOnClickListener {
            //버튼 색상 변경
            binding.fantasyArt.setBackgroundResource(R.drawable.drawing_style)
            binding.comicBook.setBackgroundResource(R.drawable.drawing_style)
            binding.anime.setBackgroundResource(R.drawable.recommend_ai)
            binding.isometric.setBackgroundResource(R.drawable.drawing_style)
            binding.cinematic.setBackgroundResource(R.drawable.drawing_style)
            binding.origami.setBackgroundResource(R.drawable.drawing_style)
            val edit = pref.edit()
            val style= "anime"

            edit.putString("animation_style", style)
            edit.apply()
        }
        //isometric - 그림 그림체 선택
        binding.isometric.setOnClickListener {
            //버튼 색상 변경
            binding.fantasyArt.setBackgroundResource(R.drawable.drawing_style)
            binding.comicBook.setBackgroundResource(R.drawable.drawing_style)
            binding.anime.setBackgroundResource(R.drawable.drawing_style)
            binding.isometric.setBackgroundResource(R.drawable.recommend_ai)
            binding.cinematic.setBackgroundResource(R.drawable.drawing_style)
            binding.origami.setBackgroundResource(R.drawable.drawing_style)
            val edit = pref.edit()
            val style= "isometric"

            edit.putString("animation_style", style)
            edit.apply()
        }
        //cinematic - 영화 그림체 선택
        binding.cinematic.setOnClickListener {
            //버튼 색상 변경
            binding.fantasyArt.setBackgroundResource(R.drawable.drawing_style)
            binding.comicBook.setBackgroundResource(R.drawable.drawing_style)
            binding.anime.setBackgroundResource(R.drawable.drawing_style)
            binding.isometric.setBackgroundResource(R.drawable.drawing_style)
            binding.cinematic.setBackgroundResource(R.drawable.recommend_ai)
            binding.origami.setBackgroundResource(R.drawable.drawing_style)
            val edit = pref.edit()
            val style= "cinematic"

            edit.putString("animation_style", style)
            edit.apply()
        }
        //origami - 종이접기 그림체 선택
        binding.origami.setOnClickListener {
            //버튼 색상 변경
            binding.fantasyArt.setBackgroundResource(R.drawable.drawing_style)
            binding.comicBook.setBackgroundResource(R.drawable.drawing_style)
            binding.anime.setBackgroundResource(R.drawable.drawing_style)
            binding.isometric.setBackgroundResource(R.drawable.drawing_style)
            binding.cinematic.setBackgroundResource(R.drawable.drawing_style)
            binding.origami.setBackgroundResource(R.drawable.recommend_ai)
            val edit = pref.edit()
            val style= "origami"

            edit.putString("animation_style", style)
            edit.apply()
        }
    }
}