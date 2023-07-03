package com.example.gan_image

import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.gan_image.databinding.PlayingEnglishActivityBinding
import com.example.gan_image.model.APIClient
import com.example.gan_image.model.EnglishWord
import com.example.gan_image.model.EnglishWordResponse
import com.example.gan_image.model.storydetailResponse
import com.example.gan_image.model.storylistResponse
import io.github.muddz.styleabletoast.StyleableToast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Base64

class PlayingEnglishActivity : AppCompatActivity()  {
    val binding by lazy{ PlayingEnglishActivityBinding.inflate(layoutInflater)}
    //내용 저장돼 있는 list
    val ItemList = arrayListOf<storydetailResponse>()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        val api= APIClient.create()

        // sharedpreferences에 저장된 token을 불러옴
        val pref = getSharedPreferences("token",0)
        var token = pref.getString("token", "")

        val id= getIntent().getIntExtra("storyID",1)
        var cnt = 0

        //책 제목 저장
        var storytitle:String = ""


        //동화책 가져옴
        api.getFairytaleDetail("Bearer $token",id).enqueue(object: Callback<storylistResponse> {
            override fun onResponse(
                call: Call<storylistResponse>,
                response: Response<storylistResponse>,
            ) {
                val contentlist=response.body()?.contents
                storytitle = response.body()?.title.toString()

                if (contentlist!=null){
                    //전체 페이지
                    binding.allPage.text = (contentlist.size).toString()

                    for(i in 0..contentlist.size-1){

                        var elementId=contentlist.get(i).elementId
                        var image=contentlist.get(i).image
                        var text=contentlist.get(i).text
                        Log.d("log","elementid: "+elementId)
                        Log.d("log","image: "+image)
                        Log.d("log","text: "+text)

                        ItemList.add(storydetailResponse(elementId,image,text))
                    }
                }
                //화면 초기 내용
                binding.currentPage.text = "1"
                binding.sentence.text = ItemList[cnt].text //1페이지 내용
                binding.image.setImageBitmap(stringToBitmap(ItemList[cnt].image)) //이미지

                //영어단어 가져오기
                var text = EnglishWord(ItemList[cnt].text)
                //this@PlayingEnglishActivity.let { StyleableToast.makeText(it, text.toString(), R.style.firstpageToast).show() }
                api.pickWord("Bearer $token",text).enqueue(object: Callback<EnglishWordResponse> {
                    override fun onResponse(
                        call: Call<EnglishWordResponse>,
                        response: Response<EnglishWordResponse>,
                    ) {
                        val eng1=response.body()?.eng1
                        val eng2=response.body()?.eng2
                        val kor1=response.body()?.kor1
                        val kor2=response.body()?.kor2

                        binding.engWord1.text = eng1
                        binding.engWord2.text = eng2
                        binding.word1.text = kor1
                        binding.word2.text = kor2

                    }
                    override fun onFailure(call: Call<EnglishWordResponse>, t: Throwable) {
                        Log.d("log","fail")
                    }
                })

            }
            override fun onFailure(call: Call<storylistResponse>, t: Throwable) {
                Log.d("log","fail")
            }
        })



        //창 닫기 버튼 --> ClickBookActivity 화면
        binding.backBtn.setOnClickListener {
            val intent = Intent(applicationContext, ClickBookActivity::class.java)
            intent.putExtra("storyID",id)
            intent.putExtra("storytitle",storytitle)
            startActivity(intent)
        }

//화면 오른쪽 선택 --> 다음 페이지
        binding.nextPageBtn.setOnClickListener {
            //val intent = Intent(applicationContext, PlayingStoryActivity::class.java) //
            //startActivity(intent)

            if(binding.currentPage.text.toString().toInt() < binding.allPage.text.toString().toInt()){
                contentChange(++cnt) //페이지 내용 바꾸는 함수 호출

                //영어단어 가져오기
                var text = EnglishWord(ItemList[cnt].text)
                //this@PlayingEnglishActivity.let { StyleableToast.makeText(it, text.toString(), R.style.firstpageToast).show() }
                api.pickWord("Bearer $token",text).enqueue(object: Callback<EnglishWordResponse> {
                    override fun onResponse(
                        call: Call<EnglishWordResponse>,
                        response: Response<EnglishWordResponse>,
                    ) {
                        val eng1=response.body()?.eng1
                        val eng2=response.body()?.eng2
                        val kor1=response.body()?.kor1
                        val kor2=response.body()?.kor2

                        binding.engWord1.text = eng1
                        binding.engWord2.text = eng2
                        binding.word1.text = kor1
                        binding.word2.text = kor2

                    }
                    override fun onFailure(call: Call<EnglishWordResponse>, t: Throwable) {
                        Log.d("log","fail")
                    }
                })
            }
            else{
                //Toast.makeText(this, "마지막 페이지입니다.", Toast.LENGTH_SHORT).show()
                val intent=Intent(applicationContext,EndingPlayingEnglishActivity::class.java)
                intent.putExtra("storyID",id)
                startActivity(intent)
            }

        }
        //화면 왼쪽 선택 --> 이전 페이지
        binding.beforePageBtn.setOnClickListener {
            if(binding.currentPage.text.toString().toInt() > 1){ //만약 전체 페이지보다 작다면
                contentChange(--cnt) //페이지 내용 바꾸는 함수 호출

                //영어단어 가져오기
                var text = EnglishWord(ItemList[cnt].text)
                //this@PlayingEnglishActivity.let { StyleableToast.makeText(it, text.toString(), R.style.firstpageToast).show() }
                api.pickWord("Bearer $token",text).enqueue(object: Callback<EnglishWordResponse> {
                    override fun onResponse(
                        call: Call<EnglishWordResponse>,
                        response: Response<EnglishWordResponse>,
                    ) {
                        val eng1=response.body()?.eng1
                        val eng2=response.body()?.eng2
                        val kor1=response.body()?.kor1
                        val kor2=response.body()?.kor2

                        binding.engWord1.text = eng1
                        binding.engWord2.text = eng2
                        binding.word1.text = kor1
                        binding.word2.text = kor2

                    }
                    override fun onFailure(call: Call<EnglishWordResponse>, t: Throwable) {
                        Log.d("log","fail")
                    }
                })
            }
            else{
                //Toast.makeText(this, "첫 페이지입니다.", Toast.LENGTH_SHORT).show()
                this@PlayingEnglishActivity.let { StyleableToast.makeText(it, "첫 페이지입니다!", R.style.firstpageToast).show() }

            }
        }
        // '>' 버튼 선택 --> 다음 페이지
        binding.nextPage.setOnClickListener {
            //val intent = Intent(applicationContext, PlayingStoryActivity::class.java) //
            //startActivity(intent)

            if(binding.currentPage.text.toString().toInt() < binding.allPage.text.toString().toInt()){
                contentChange(++cnt) //페이지 내용 바꾸는 함수 호출

                //영어단어 가져오기
                var text = EnglishWord(ItemList[cnt].text)
                //this@PlayingEnglishActivity.let { StyleableToast.makeText(it, text.toString(), R.style.firstpageToast).show() }
                api.pickWord("Bearer $token",text).enqueue(object: Callback<EnglishWordResponse> {
                    override fun onResponse(
                        call: Call<EnglishWordResponse>,
                        response: Response<EnglishWordResponse>,
                    ) {
                        val eng1=response.body()?.eng1
                        val eng2=response.body()?.eng2
                        val kor1=response.body()?.kor1
                        val kor2=response.body()?.kor2

                        binding.engWord1.text = eng1
                        binding.engWord2.text = eng2
                        binding.word1.text = kor1
                        binding.word2.text = kor2

                    }
                    override fun onFailure(call: Call<EnglishWordResponse>, t: Throwable) {
                        Log.d("log","fail")
                    }
                })
            }
            else{
                //Toast.makeText(this, "마지막 페이지입니다.", Toast.LENGTH_SHORT).show()
                val intent=Intent(applicationContext,EndingPlayingActivity::class.java)
                intent.putExtra("storyID",id)
                startActivity(intent)
            }

        }
        // '<' 버튼 선택 --> 이전 페이지
        binding.beforePage.setOnClickListener {
            if(binding.currentPage.text.toString().toInt() > 1){ //만약 전체 페이지보다 작다면
                contentChange(--cnt) //페이지 내용 바꾸는 함수 호출

                //영어단어 가져오기
                var text = EnglishWord(ItemList[cnt].text)
                //this@PlayingEnglishActivity.let { StyleableToast.makeText(it, text.toString(), R.style.firstpageToast).show() }
                api.pickWord("Bearer $token",text).enqueue(object: Callback<EnglishWordResponse> {
                    override fun onResponse(
                        call: Call<EnglishWordResponse>,
                        response: Response<EnglishWordResponse>,
                    ) {
                        val eng1=response.body()?.eng1
                        val eng2=response.body()?.eng2
                        val kor1=response.body()?.kor1
                        val kor2=response.body()?.kor2

                        binding.engWord1.text = eng1
                        binding.engWord2.text = eng2
                        binding.word1.text = kor1
                        binding.word2.text = kor2

                    }
                    override fun onFailure(call: Call<EnglishWordResponse>, t: Throwable) {
                        Log.d("log","fail")
                    }
                })
            }
            else{
                //Toast.makeText(this, "첫 페이지입니다.", Toast.LENGTH_SHORT).show()
                this@PlayingEnglishActivity.let { StyleableToast.makeText(it, "첫 페이지입니다!", R.style.firstpageToast).show() }

            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun stringToBitmap(encodedString: String): Bitmap {

        //val encodeByte = Base64.decode()
        val encodeByte = Base64.getDecoder().decode(encodedString)
        return BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.size)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun contentChange(cnt: Int) {
        //책 페이지 넘겨주면 값 가져오기
        binding.currentPage.text = (cnt+1).toString() //현재 페이지
        binding.sentence.text = ItemList[cnt].text //내용
        binding.image.setImageBitmap(stringToBitmap(ItemList[cnt].image))

    }
}

