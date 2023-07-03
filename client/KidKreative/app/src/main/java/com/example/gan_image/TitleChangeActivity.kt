package com.example.gan_image

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.text.TextUtils.isEmpty
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.gan_image.databinding.TitleChangeActivityBinding
import com.example.gan_image.model.APIClient
import com.example.gan_image.model.Items
import com.example.gan_image.model.makestoryPost
import com.example.gan_image.model.storydetailResponse
import com.example.gan_image.model.storylistResponse
import io.github.muddz.styleabletoast.StyleableToast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.util.Base64

class TitleChangeActivity : AppCompatActivity() {
    var text: String? = null
    val binding by lazy{TitleChangeActivityBinding.inflate(layoutInflater)}

    //내용 저장돼 있는 list
    val ItemList = arrayListOf<storydetailResponse>()
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

            //loading중 체크 안보이게하기
            binding.check1.setVisibility(View.INVISIBLE)
            binding.check2.setVisibility(View.INVISIBLE)
            binding.check3.setVisibility(View.INVISIBLE)
            binding.check4.setVisibility(View.INVISIBLE)
            binding.check5.setVisibility(View.INVISIBLE)
            binding.check6.setVisibility(View.INVISIBLE)



        val api= APIClient.create()

        //token
        val pref = getSharedPreferences("token",0)
        var token = pref.getString("token", "")
        var id=pref.getString("id","").toString()

        //책 id 가져오기
        var storyID= getIntent().getIntExtra("storyID",1)
        var storytitle_string= getIntent().getStringExtra("storytitle").toString()


        //책 제목 저장
        var storytitle:String = ""

        //표지 정하기
        var title_num:String = "0"

        //책 정보 가져오기
        api.getFairytaleDetail("Bearer $token",storyID).enqueue(object: Callback<storylistResponse> {
            override fun onResponse(
                call: Call<storylistResponse>,
                response: Response<storylistResponse>
            ) {
                val contentlist=response.body()?.contents



                storytitle_string = response.body()?.title.toString()

                //표지 번호 가져오기
                title_num = response.body()?.title_num.toString()

                if (contentlist!=null){
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
                //배경 설정
                binding.background.setImageBitmap(stringToBitmap(ItemList[title_num.toInt()].image))
                //제목 띄우기
                binding.title.setText(response.body()?.title)


                //check 아이콘 INVISIBLE 초기화
                if(title_num.toInt()==0) {
                    binding.check1.setVisibility(View.VISIBLE)
                    binding.check2.setVisibility(View.INVISIBLE)
                    binding.check3.setVisibility(View.INVISIBLE)
                    binding.check4.setVisibility(View.INVISIBLE)
                    binding.check5.setVisibility(View.INVISIBLE)
                    binding.check6.setVisibility(View.INVISIBLE)
                }
                else if(title_num.toInt()==1) {
                    binding.check1.setVisibility(View.INVISIBLE)
                    binding.check2.setVisibility(View.VISIBLE)
                    binding.check3.setVisibility(View.INVISIBLE)
                    binding.check4.setVisibility(View.INVISIBLE)
                    binding.check5.setVisibility(View.INVISIBLE)
                    binding.check6.setVisibility(View.INVISIBLE)
                }
                else if(title_num.toInt()==2) {
                    binding.check1.setVisibility(View.INVISIBLE)
                    binding.check2.setVisibility(View.INVISIBLE)
                    binding.check3.setVisibility(View.VISIBLE)
                    binding.check4.setVisibility(View.INVISIBLE)
                    binding.check5.setVisibility(View.INVISIBLE)
                    binding.check6.setVisibility(View.INVISIBLE)
                }
                else if(title_num.toInt()==3) {
                    binding.check1.setVisibility(View.INVISIBLE)
                    binding.check2.setVisibility(View.INVISIBLE)
                    binding.check3.setVisibility(View.INVISIBLE)
                    binding.check4.setVisibility(View.VISIBLE)
                    binding.check5.setVisibility(View.INVISIBLE)
                    binding.check6.setVisibility(View.INVISIBLE)
                }
                else if(title_num.toInt()==4) {
                    binding.check1.setVisibility(View.INVISIBLE)
                    binding.check2.setVisibility(View.INVISIBLE)
                    binding.check3.setVisibility(View.INVISIBLE)
                    binding.check4.setVisibility(View.INVISIBLE)
                    binding.check5.setVisibility(View.VISIBLE)
                    binding.check6.setVisibility(View.INVISIBLE)
                }
                else if(title_num.toInt()==5) {
                    binding.check1.setVisibility(View.INVISIBLE)
                    binding.check2.setVisibility(View.INVISIBLE)
                    binding.check3.setVisibility(View.INVISIBLE)
                    binding.check4.setVisibility(View.INVISIBLE)
                    binding.check5.setVisibility(View.INVISIBLE)
                    binding.check6.setVisibility(View.VISIBLE)
                }

                //책 그림 띄우기
                try {
                    binding.story1.setImageBitmap(stringToBitmap(ItemList[0].image)) //이미지
                    binding.story2.setImageBitmap(stringToBitmap(ItemList[1].image)) //이미지
                    binding.story3.setImageBitmap(stringToBitmap(ItemList[2].image)) //이미지
                    binding.story4.setImageBitmap(stringToBitmap(ItemList[3].image)) //이미지
                    binding.story5.setImageBitmap(stringToBitmap(ItemList[4].image)) //이미지
                    binding.story6.setImageBitmap(stringToBitmap(ItemList[5].image)) //이미지
                } catch (e:Exception){
                }

            }
            override fun onFailure(call: Call<storylistResponse>, t: Throwable) {
                Log.d("log","fail")
            }
        })


            //책 표지 변경
            binding.story1.setOnClickListener {
                title_num = "0"
                //check1만 VISIBLE
                try {
                if(ItemList[0] != null) {
                binding.check1.setVisibility(View.VISIBLE)
                binding.check2.setVisibility(View.INVISIBLE)
                binding.check3.setVisibility(View.INVISIBLE)
                binding.check4.setVisibility(View.INVISIBLE)
                binding.check5.setVisibility(View.INVISIBLE)
                binding.check6.setVisibility(View.INVISIBLE)
                }

                    binding.story2.setImageBitmap(stringToBitmap(ItemList[1].image))
                    binding.story3.setImageBitmap(stringToBitmap(ItemList[2].image))
                    binding.story4.setImageBitmap(stringToBitmap(ItemList[3].image))
                    binding.story5.setImageBitmap(stringToBitmap(ItemList[4].image))
                    binding.story6.setImageBitmap(stringToBitmap(ItemList[5].image))
                } catch (e: Exception) {
                }
            }



            binding.story2.setOnClickListener {
                title_num = "1"
                //check2만 VISIBLE
                try {
                if(ItemList[1] != null){
                binding.check1.setVisibility(View.INVISIBLE)
                binding.check2.setVisibility(View.VISIBLE)
                binding.check3.setVisibility(View.INVISIBLE)
                binding.check4.setVisibility(View.INVISIBLE)
                binding.check5.setVisibility(View.INVISIBLE)
                binding.check6.setVisibility(View.INVISIBLE)
                }

                    binding.story1.setImageBitmap(stringToBitmap(ItemList[0].image))
                    binding.story3.setImageBitmap(stringToBitmap(ItemList[2].image))
                    binding.story4.setImageBitmap(stringToBitmap(ItemList[3].image))
                    binding.story5.setImageBitmap(stringToBitmap(ItemList[4].image))
                    binding.story6.setImageBitmap(stringToBitmap(ItemList[5].image))
                } catch (e:Exception){
                }
            }



            binding.story3.setOnClickListener {
                title_num = "2"
                //check3만 VISIBLE
                try {
                if(ItemList[2] != null) {
                binding.check1.setVisibility(View.INVISIBLE)
                binding.check2.setVisibility(View.INVISIBLE)
                binding.check3.setVisibility(View.VISIBLE)
                binding.check4.setVisibility(View.INVISIBLE)
                binding.check5.setVisibility(View.INVISIBLE)
                binding.check6.setVisibility(View.INVISIBLE)
                }

                    binding.story1.setImageBitmap(stringToBitmap(ItemList[0].image))
                    binding.story2.setImageBitmap(stringToBitmap(ItemList[1].image))
                    binding.story4.setImageBitmap(stringToBitmap(ItemList[3].image))
                    binding.story5.setImageBitmap(stringToBitmap(ItemList[4].image))
                    binding.story6.setImageBitmap(stringToBitmap(ItemList[5].image))
                } catch (e: Exception) {
                }
            }



            binding.story4.setOnClickListener {
                title_num = "3"
                //check4만 VISIBLE
                try {
                if(ItemList[3] != null) {
                binding.check1.setVisibility(View.INVISIBLE)
                binding.check2.setVisibility(View.INVISIBLE)
                binding.check3.setVisibility(View.INVISIBLE)
                binding.check4.setVisibility(View.VISIBLE)
                binding.check5.setVisibility(View.INVISIBLE)
                binding.check6.setVisibility(View.INVISIBLE)
                }

                    binding.story1.setImageBitmap(stringToBitmap(ItemList[0].image))
                    binding.story2.setImageBitmap(stringToBitmap(ItemList[1].image))
                    binding.story3.setImageBitmap(stringToBitmap(ItemList[2].image))
                    binding.story5.setImageBitmap(stringToBitmap(ItemList[4].image))
                    binding.story6.setImageBitmap(stringToBitmap(ItemList[5].image))
                } catch (e: Exception) {
                }
            }



            binding.story5.setOnClickListener {
                title_num = "4"
                //check5만 VISIBLE
                try {
                if(ItemList[4] != null) {
                binding.check1.setVisibility(View.INVISIBLE)
                binding.check2.setVisibility(View.INVISIBLE)
                binding.check3.setVisibility(View.INVISIBLE)
                binding.check4.setVisibility(View.INVISIBLE)
                binding.check5.setVisibility(View.VISIBLE)
                binding.check6.setVisibility(View.INVISIBLE)
                }

                    binding.story1.setImageBitmap(stringToBitmap(ItemList[0].image))
                    binding.story2.setImageBitmap(stringToBitmap(ItemList[1].image))
                    binding.story3.setImageBitmap(stringToBitmap(ItemList[2].image))
                    binding.story4.setImageBitmap(stringToBitmap(ItemList[3].image))
                    binding.story6.setImageBitmap(stringToBitmap(ItemList[5].image))
                } catch (e: Exception) {
                }
            }



            binding.story6.setOnClickListener {
                title_num = "5"
                //check6만 VISIBLE
                try {
                if(ItemList[5] != null) {
                binding.check1.setVisibility(View.INVISIBLE)
                binding.check2.setVisibility(View.INVISIBLE)
                binding.check3.setVisibility(View.INVISIBLE)
                binding.check4.setVisibility(View.INVISIBLE)
                binding.check5.setVisibility(View.INVISIBLE)
                binding.check6.setVisibility(View.VISIBLE)
                }

                    binding.story1.setImageBitmap(stringToBitmap(ItemList[0].image))
                    binding.story2.setImageBitmap(stringToBitmap(ItemList[1].image))
                    binding.story3.setImageBitmap(stringToBitmap(ItemList[2].image))
                    binding.story4.setImageBitmap(stringToBitmap(ItemList[3].image))
                    binding.story5.setImageBitmap(stringToBitmap(ItemList[4].image))
                } catch (e: Exception) {
                }
            }


        //뒤로가기 --> ClickBookActivity 화면
       binding.backBtn.setOnClickListener {
            val intent = Intent(applicationContext, ClickBookActivity::class.java)
            intent.putExtra("storyID",storyID)
            intent.putExtra("storytitle",storytitle_string)
            startActivity(intent)
        }

        //수정 버튼 --> 표지와 제목 수정한거 저장
        binding.changeBtn.setOnClickListener {
            //title값으로 제목을 변경
            val changetitle = binding.title.text.toString()
            api.putFairytaleTitle("Bearer $token",storyID,makestoryPost(changetitle,id,title_num)).enqueue(object: Callback<storylistResponse> {
                override fun onResponse(
                    call: Call<storylistResponse>,
                    response: Response<storylistResponse>,
                ) {
                    if (response.isSuccessful) {
                        //Toast.makeText(this@TitleChangeActivity, changetitle + "으로 제목이 수정되었습니다.", Toast.LENGTH_SHORT).show()
                        //Toast.makeText(this@TitleChangeActivity, "수정 완료", Toast.LENGTH_SHORT).show()
                        this@TitleChangeActivity.let { StyleableToast.makeText(it, "수정 완료되었습니다!", R.style.changeToast).show() }
                        storytitle_string = response.body()?.title.toString()
                        //배경 설정
                        binding.background.setImageBitmap(stringToBitmap(ItemList[title_num.toInt()].image))
                        Log.d("log", "제목바꾸기 성공")
                    } else {
                        Log.d("log", "제목바꾸기 실패")
                    }
                }
                override fun onFailure(call: Call<storylistResponse>, t: Throwable) {
                    Log.d("log", "fail")
                }
            })
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun stringToBitmap(encodedString: String): Bitmap {

        //val encodeByte = Base64.decode()
        val encodeByte = Base64.getDecoder().decode(encodedString)
        return BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.size)
    }
}
//