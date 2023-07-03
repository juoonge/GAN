package com.example.gan_image

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isInvisible
import com.example.gan_image.databinding.ContentChangeActivityBinding
import com.example.gan_image.model.APIClient
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.Timer
import kotlin.concurrent.timer
import kotlinx.coroutines.*
import java.time.LocalDateTime
import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import android.os.Environment
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.gan_image.model.AI_image
import com.example.gan_image.model.AI_image_response
import com.example.gan_image.model.changestoryElePost
import com.example.gan_image.model.makestoryElePost
import com.example.gan_image.model.storydetailResponse
import com.example.gan_image.model.storylistResponse
import io.github.muddz.styleabletoast.StyleableToast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.*


class ContentChangeActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    val binding by lazy { ContentChangeActivityBinding.inflate(layoutInflater)}

    val ItemList = arrayListOf<storydetailResponse>()
    //record 사용 변수
    private var output: String? = null
    private var mediaRecorder: MediaRecorder? = null
    private var state: Boolean = false

    //tts 사용 변수
    private var tts: TextToSpeech? = null

    //파일 재생 player
    private var player: MediaPlayer? = null

    private var time =0
    private var timerTask: Timer?=null
    private var i=0

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val api= APIClient.create()
        tts = TextToSpeech(this, this)


        // sharedpreferences에 저장된 token을 불러옴
        val pref = getSharedPreferences("token",0)
        var token = pref.getString("token", "")
        val storyid= getIntent().getIntExtra("storyID",1)
        var cnt = 0

        var id=pref.getString("id","")

        //내용 저장돼 있는 list

        var changeimagestring: String? = null


        //책 id 가져오기
        var storyID= getIntent().getIntExtra("storyID",1)
        var storytitle_string= getIntent().getStringExtra("storytitle").toString()

        //일단 책정보 가져오기
        api.getFairytaleDetail("Bearer $token",storyid).enqueue(object: Callback<storylistResponse> {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(
                call: Call<storylistResponse>,
                response: Response<storylistResponse>,
            ) {
                val contentlist=response.body()?.contents
                storytitle_string = response.body()?.title.toString()

                if (contentlist!=null){
                    binding.allPage.text = (contentlist.size).toString() //전체 페이지

                    for(i in 0..contentlist.size-1){

                        var elementId=contentlist.get(i).elementId
                        var image=contentlist.get(i).image
                        changeimagestring = contentlist.get(cnt).image
                        var text=contentlist.get(i).text
                        Log.d("log","elementid: "+elementId)
                        Log.d("log","image: "+image)
                        Log.d("log","text: "+text)

                        ItemList.add(storydetailResponse(elementId,image,text))
                    }
                }
                //화면 초기 내용
                binding.currentPage.text = "1"
                binding.sentence.setText(ItemList[cnt].text) //1페이지 내용
                binding.image.setImageBitmap(stringToBitmap(ItemList[cnt].image)) //이미지

                //현재 페이지 elementid 확인
                //Toast.makeText(this@ContentChangeActivity, ItemList.get(cnt).elementId.toString(), Toast.LENGTH_SHORT).show()


            }
            override fun onFailure(call: Call<storylistResponse>, t: Throwable) {
                Log.d("log","fail")
            }
        })


        // '<' 글자 선택 --> 이전 페이지
        binding.beforePage.setOnClickListener {
            if(binding.currentPage.text.toString().toInt() > 1){ //만약 전체 페이지보다 작다면
                contentChange(--cnt) //페이지 내용 바꾸는 함수 호출
                //cnt 출력
                //Toast.makeText(this@ContentChangeActivity, cnt.toString(), Toast.LENGTH_SHORT).show()
            }
            else{
                //Toast.makeText(this, "첫 페이지입니다.", Toast.LENGTH_SHORT).show()
                this@ContentChangeActivity.let { StyleableToast.makeText(it, "첫 페이지입니다!", R.style.firstpageToast).show() }
            }
        }

        // '>' 글자 선택 --> 다음 페이지
        binding.nextPage.setOnClickListener {
            if(binding.currentPage.text.toString().toInt() < binding.allPage.text.toString().toInt()){
                contentChange(++cnt) //페이지 내용 바꾸는 함수 호출
                //cnt 출력
                //Toast.makeText(this@ContentChangeActivity, cnt.toString(), Toast.LENGTH_SHORT).show()
            }
            else{
                //Toast.makeText(this, "마지막 페이지입니다.", Toast.LENGTH_SHORT).show()
                this@ContentChangeActivity.let { StyleableToast.makeText(it, "마지막 페이지입니다!", R.style.lastpageToast).show() }
            }
        }

        // '<' 버튼 선택 --> 이전 페이지
        binding.beforePageBtn.setOnClickListener {
            if(binding.currentPage.text.toString().toInt() > 1){ //만약 전체 페이지보다 작다면
                contentChange(--cnt) //페이지 내용 바꾸는 함수 호출
                //cnt 출력
                //Toast.makeText(this@ContentChangeActivity, cnt.toString(), Toast.LENGTH_SHORT).show()
            }
            else{
                this@ContentChangeActivity.let { StyleableToast.makeText(it, "첫 페이지입니다!", R.style.firstpageToast).show() }
            }
        }

        // '>' 버튼 선택 --> 다음 페이지
        binding.nextPageBtn.setOnClickListener {
            if(binding.currentPage.text.toString().toInt() < binding.allPage.text.toString().toInt()){
                contentChange(++cnt) //페이지 내용 바꾸는 함수 호출
                //cnt 출력
                //Toast.makeText(this@ContentChangeActivity, cnt.toString(), Toast.LENGTH_SHORT).show()
            }
            else{
                this@ContentChangeActivity.let { StyleableToast.makeText(it, "마지막 페이지입니다!", R.style.lastpageToast).show() }
            }
        }

        // '내용 원래대로' 버튼
        binding.returnContent.setOnClickListener {
            /*
                기존 내용으로 재수정
                  sentenceText.setText()
            */
            binding.sentence.setText(ItemList[cnt].text) //1페이지 내용

        }

        // '내용 수정' 버튼
        binding.changeContentBtn.setOnClickListener {
            /*
        바뀐 내용으로 DB에 저장
        sentenceText.getText()
            */
            val pref = getSharedPreferences("token",0)
            var token = pref.getString("token", "")

            // 이미지, text 서버 전송
            var text=binding.sentence.text.toString()
            var image = changeimagestring.toString()
            var element_id = ItemList.get(cnt).elementId
            api.putFairytaleEle("Bearer $token", element_id, changestoryElePost(storyid,text, image,element_id.toString())).enqueue(object: Callback<storylistResponse> {
                override fun onResponse(call: Call<storylistResponse>, response: Response<storylistResponse>) {
                    if(response.isSuccessful){
                        Log.d("log","response: "+response.body())
                        //Toast.makeText(this@ContentChangeActivity, "책 내용을 저장하였습니다", Toast.LENGTH_SHORT).show()
                        this@ContentChangeActivity.let { StyleableToast.makeText(it, "책 내용을 저장하였습니다!", R.style.changeToast).show() }
                    }
                }
                override fun onFailure(call: Call<storylistResponse>, t: Throwable) {
                    Log.d("log", "fail")
                    //Toast.makeText(this@ContentChangeActivity, "저장실패", Toast.LENGTH_SHORT).show()
                    this@ContentChangeActivity.let { StyleableToast.makeText(it, "저장실패!", R.style.warningToast).show() }
                }

            })
        }


        // '그림 원래대로' 버튼
        binding.returnImage.setOnClickListener {
            /*
                 기존 이미지로 재수정
                 image.setImageBitMap()??
            */
            binding.image.setImageBitmap(stringToBitmap(ItemList[cnt].image)) //이미지
            //이미지 원래대로
            changeimagestring = ItemList[cnt].image
        }

        // '이미지 재생성' 버튼
        binding.newImageBtn.setOnClickListener {
            reRoadImage()

            /*
                새로운 이미지로 재수정
                image.setImageBitMap()
            */
            val ItemList=arrayListOf<String>()

            api.imageapi(AI_image(binding.sentence.text.toString())).enqueue(object: Callback<AI_image_response> {
                override fun onFailure(call: Call<AI_image_response>, t: Throwable) {
                    Log.d("log", t.message.toString())
                    Log.d("log", "fail")
                }

                override fun onResponse(call: Call<AI_image_response>, response: Response<AI_image_response>) {
                    if(response.isSuccessful){
                        var imagestring= response.body()!!
                        changeimagestring = imagestring.image_response
                        //base64(String) -> bitmap(bitmap)
                        var encodeByte= android.util.Base64.decode(imagestring.image_response,
                            android.util.Base64.DEFAULT)
                        var bitmapDecode= BitmapFactory.decodeByteArray(encodeByte,0,encodeByte.size)

                        binding.image.setImageBitmap(bitmapDecode)
                        binding.loading.visibility = View.INVISIBLE
                        //Log.d("log","onResponse 성공: "+bitmapDecode)
                    }else{
                        Log.d("log","onResponse 실패")
                        Toast.makeText(this@ContentChangeActivity, "이미지 실패", Toast.LENGTH_SHORT).show()
                    }
                }

            })

        }
        // 문장 옆 스피커 이미지 선택 --> 기본 음성 출력
        binding.sentenceSpeakerBtn.setOnClickListener {
           //tts 출력
            startTTS()
        }


        // 녹음 버튼
        binding.record.setOnClickListener {

            Toast.makeText(this, "녹음 버튼 클릭", Toast.LENGTH_SHORT).show()
            /*
             녹음중을 표현하는 아이콘으로 변경하기
             녹음시간 count 되도록 숫자 변경 --> time
             녹음해서 DB저장
             녹음 최대 길이는 2분
               */
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                //권한 허용 코드
                val permissions = arrayOf(Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                ActivityCompat.requestPermissions(this, permissions,0)

            } else { //권한 허용하면 녹음 시작!
                //Toast.makeText(this, "권한 설정 완료", Toast.LENGTH_SHORT).show()
                if(!state){
                    startRecording()
                }
                else{
                    stopRecording()
                }
            }
        }


        // '다시 녹음' 버튼
        binding.reRecord.setOnClickListener {
            binding.reRecord.playAnimation()
            reRecording()
        }

        //뒤로가기 --> ClickBookActivity화면
        binding.backBtn.setOnClickListener {
            val intent = Intent(applicationContext, ClickBookActivity::class.java)
            intent.putExtra("storyID",storyID)
            intent.putExtra("storytitle",storytitle_string)
            startActivity(intent)

        }

    }
    //이미지 재생성 버튼 선택
    private fun reRoadImage(){
        binding.loading.visibility = View.VISIBLE
        binding.newImageBtn.playAnimation()

        binding.loading.setAnimation("dino.json")
        binding.loading.playAnimation()
        try {
            // sleep for one second
            Thread.sleep(500)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        // binding.loading.visibility=View.GONE
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun stringToBitmap(encodedString: String): Bitmap {

        val encodeByte = Base64.getDecoder().decode(encodedString)
        return BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.size)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun contentChange(cnt: Int) {
        //책 페이지 넘겨주면 값 가져오기
        binding.currentPage.text = (cnt+1).toString() //현재 페이지
        binding.sentence.setText(ItemList[cnt].text) //1페이지 내용
        binding.image.setImageBitmap(stringToBitmap(ItemList[cnt].image)) //이미지

    }

    //녹음 시작
    private fun startRecording(){
        //녹음 애니메이션 시작
        binding.record.playAnimation();

        //파일 이름 예시
        val title = "동화제목"
        val page = "페이지"

        //녹음 파일 설정
        val fileName: String = title + "_" + page + ".mp3" //파일 이름 설정
        output = Environment.getExternalStorageDirectory().absolutePath + "/Download/" + fileName //내장메모리 밑에 위치
        mediaRecorder = MediaRecorder()
        mediaRecorder?.setAudioSource((MediaRecorder.AudioSource.MIC))
        mediaRecorder?.setOutputFormat((MediaRecorder.OutputFormat.MPEG_4))
        mediaRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        mediaRecorder?.setOutputFile(output)

        try {
            mediaRecorder?.prepare()
            mediaRecorder?.start()
            state = true
            Toast.makeText(this, "녹음이 시작되었습니다.", Toast.LENGTH_SHORT).show()
        } catch (e: IllegalStateException){
            e.printStackTrace()
        } catch (e: IOException){
            e.printStackTrace()
        }

        //타이머 숫자 변경
        timerTask = timer(period =10){
            time++

            val sec = time / 100
            val milli = time % 100

            runOnUiThread{
                //60초면 정지
                if(sec==60 && milli==0){
                    binding.record.loop(false);
                    timerTask?.cancel()
                }

                if(sec<10)
                    binding.textTime?.text = "0${sec}:${milli}"
                else
                    binding.textTime?.text = "${sec}:${milli}"
            }
        }
    }

    //녹음 중지
    private fun stopRecording(){


        if(state){
            //녹음 애니메이션 실행 중지
            timerTask?.cancel()
            binding.record.loop(false);

            mediaRecorder?.stop()
            mediaRecorder?.reset()
            mediaRecorder?.release()
            state = false
            Toast.makeText(this, "녹음이 중지 되었습니다.", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "녹음 실행 중이 아닙니다.", Toast.LENGTH_SHORT).show()
        }
    }

    //녹음 초기화 및 재실행
    private fun reRecording(){
        timerTask?.cancel()
        time =0
        binding.textTime?.text = "00:00"
    }

    //녹음파일 재생
    private fun startPlaying() { //나중에 db랑 연결해서 파일경로로 저장!

        val fileName: String = "동화제목_페이지.mp3" //파일 이름 설정
        output = Environment.getExternalStorageDirectory().absolutePath + "/Download/" + fileName //내장메모리 밑에 위치

        player = MediaPlayer().apply {
            setDataSource(output)
            prepare()
        }
        player?.start()
    }

    //TTS 초기화 함수
    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {// 언어 설정
            val result = tts!!.setLanguage(Locale.ENGLISH)
            //Toast.makeText(this, "TTS 초기화 시작", Toast.LENGTH_SHORT).show()

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(this, "지원하지 않는 언어입니다", Toast.LENGTH_SHORT).show()
            }
            else{
                tts!!.setPitch(0.9f);//음성톤
                tts!!.setSpeechRate(0.9f);//속도
            }
        }
        else Toast.makeText(this, "TTS 음성전환 에러", Toast.LENGTH_SHORT).show()
    }

    //TTS 함수
    private fun startTTS(){

        if(binding.sentence.length() ==0){
            Toast.makeText(this, "내용 null.", Toast.LENGTH_SHORT).show()
        }
        else {
            tts?.speak(binding.sentence.text.toString(), TextToSpeech.QUEUE_FLUSH, null, "")
        }
    }

    //TTS 중지 함수
    override fun onDestroy() {
        if (tts != null) {
            tts!!.stop()
            tts!!.shutdown()
        }
        super.onDestroy()
    }


}

