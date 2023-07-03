package com.example.gan_image

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.gan_image.databinding.ClickBookActivityBinding
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import com.example.gan_image.model.APIClient
import com.example.gan_image.model.deletestoryResponse
import com.example.gan_image.model.storydetailResponse
import com.example.gan_image.model.storylistResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.util.Base64

class ClickBookActivity : AppCompatActivity() {
    val binding by lazy{ClickBookActivityBinding.inflate(layoutInflater)}

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val api= APIClient.create()
        val pref = getSharedPreferences("token",0)
        var token = pref.getString("token", "")
        val ItemList = arrayListOf<storydetailResponse>()
        var title_num:String = "0"

        var storyImage = getIntent().getIntExtra("storyimage",1).toString()
        //binding.background.setImageBitmap(stringToBitmap(storyImage))

        //책 id 가져오기
        var storyID= getIntent().getIntExtra("storyID",1)
        var storytitle_string= getIntent().getStringExtra("storytitle").toString()
        val storytitle = findViewById<TextView>(R.id.title)
        storytitle.text = storytitle_string


        //DB에서 제목 가져와서 setText()
        val titleView = findViewById<View>(R.id.title) as TextView

        //뒤로가기 버튼 --> 보관함
        binding.backBtn.setOnClickListener {
            val intent = Intent(applicationContext, StorageBoxActivity::class.java)
            intent.putExtra("storyID",storyID)
            intent.putExtra("storytitle",storytitle_string)
            startActivity(intent)
        }

        //플레이 버튼 --> 플레이 화면
       binding.playingBtn.setOnClickListener {
            val intent = Intent(applicationContext, PlayingStoryActivity::class.java)
            intent.putExtra("storyID",storyID)
            startActivity(intent)
        }

        //영어공부 버튼 --> 영어 공부 플레이 화면
        binding.playingEngBtn.setOnClickListener {
            val intent = Intent(applicationContext, PlayingEnglishActivity::class.java)
            intent.putExtra("storyID",storyID)
            startActivity(intent)
        }

        //삭제 버튼 --> 동화 삭제 후 보관함으로 돌아가기
        binding.deleteBtn.setOnClickListener {

            val mDialogView = LayoutInflater.from(this).inflate(R.layout.delete_book_popup_activity, null)
            val mBuilder = AlertDialog.Builder(this)
                .setView(mDialogView)
                .setCancelable(false)

            //팝업창 띄우기
            val mAlertDialog = mBuilder.show()
            mAlertDialog.window?.setLayout(1200, 650)

            //제목 수정
            val storyTitle = mDialogView.findViewById<TextView>(R.id.popupTitle)
            storyTitle.text = storytitle_string

            //예 버튼
            val okBtn = mDialogView.findViewById<ImageButton>(R.id.okBtn)

            okBtn.setOnClickListener {
                api.deleteStory("Bearer $token",storyID).enqueue(object: Callback<deletestoryResponse>{
                    override fun onResponse(
                        call: Call<deletestoryResponse>,
                        response: Response<deletestoryResponse>
                    ) {
                        var msg = response.body()

                        if(msg?.msg == "SUCCESS"){ //삭제 성공

                            //팝업창 닫기
                            mAlertDialog.dismiss()

                            val intent = Intent(applicationContext, StorageBoxActivity::class.java)
                            //intent.putExtra("storyID",storyID)
                            startActivity(intent)
                        }
                    }
                    override fun onFailure(call: Call<deletestoryResponse>, t: Throwable) { //삭제 실패
                        Log.d("log","fail")
                    }
                })

            }

            //아니요 버튼
            val cancelBtn = mDialogView.findViewById<ImageButton>(R.id.cancelBtn)
            cancelBtn.setOnClickListener {
                //팝업창 닫기
                mAlertDialog.dismiss()
            }
        }

        //수정 버튼 --> 수정 화면
        binding.changeBtn.setOnClickListener {
            val intent = Intent(applicationContext, ChangeActivity::class.java)
            intent.putExtra("storyID",storyID)
            intent.putExtra("storytitle",storytitle_string)
            startActivity(intent)
        }


        //그림 가져오기 (background 표지 그림 설정)
        api.getFairytaleDetail("Bearer $token",storyID).enqueue(object: Callback<storylistResponse> {
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