package com.example.gan_image

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View

import android.widget.TextView
import android.widget.Toast


import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.gan_image.databinding.NextsentenceActivityBinding
import com.example.gan_image.model.APIClient
import com.example.gan_image.model.APIService
import com.example.gan_image.model.RecommendNext
import com.example.gan_image.model.RecommendNextResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class NextSentenceActivity : AppCompatActivity() {
    val binding by lazy {NextsentenceActivityBinding.inflate(layoutInflater)}
    val api= APIClient.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        val page = intent.getIntExtra("page", 1)

        //현재 문장
        var text:String = ""


        //전 문장 가져오기
        val previous_text= getIntent().getStringExtra("previous_text")


        //이야기 문장 입력받는 화면에서 뒤로가기 누를경우 이전까지의 내용이 저장된다. ->제목팝업 띄움
        binding.backBtn.setOnClickListener {
            /*
                이전 이미지 생성까지 저장
            */
            val intent = Intent(applicationContext, TitlePopupActivity::class.java)
            startActivity(intent)
        }

        //입력 버튼누르면 이미지 생성
        binding.sendBtn.setOnClickListener {

            //입력받은 문장
            text=binding.sentence.text.toString()

            val intent = Intent(applicationContext, MakingImageActivity::class.java)
            intent.putExtra("previous_text",text) // String 형태로 text전달
            intent.putExtra("page",page) //페이지 정보 넘겨줌

            startActivity(intent)
        }


        //추천 문장 선택
        binding.recommend.setOnClickListener {
            binding.loading.setAnimation("dino.json")
            binding.loading.visibility = View.VISIBLE
            binding.loading.playAnimation()
            var data = RecommendNext(previous_text)

            api.recommend_next(data).enqueue(object: Callback<RecommendNextResponse> {
                override fun onFailure(call: Call<RecommendNextResponse>, t: Throwable) {
                    Log.e("recommend", t.cause.toString())
                    var dialog = AlertDialog.Builder(this@NextSentenceActivity)
                    dialog.setTitle("에러")
                    dialog.setMessage("호출실패했습니다.")
                    dialog.show()
                }

                override fun onResponse(call: Call<RecommendNextResponse>, response: Response<RecommendNextResponse>) {
                    var signup = response.body()
                    binding.loading.visibility = View.INVISIBLE


                    binding.sentence.hint = signup?.msg.toString()
                    text = signup?.msg.toString()
                    binding.sentence.setText(text)

                }

            })
        }




    }
}