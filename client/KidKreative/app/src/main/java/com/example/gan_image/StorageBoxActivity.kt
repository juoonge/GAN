package com.example.gan_image

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.gan_image.databinding.StorageboxActivityBinding
import com.example.gan_image.model.APIClient
import com.example.gan_image.model.Items
import com.example.gan_image.model.storylistResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StorageBoxActivity : AppCompatActivity() {
    val binding by lazy { StorageboxActivityBinding.inflate(layoutInflater)}
    // url : 동화 목록 가져옴
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val api= APIClient.create()

        val pref = getSharedPreferences("token",0)
        var nickname = pref.getString("nickname", "") //기존 닉네임
        binding.nickname.text=nickname

        // sharedpreferences에 저장된 token을 불러옴
        var token = pref.getString("token", "")
        Log.d("log","token :" +token)
        //Toast.makeText(getApplicationContext(), token, Toast.LENGTH_SHORT).show();

        api.getFairytaleList("Bearer $token").enqueue(object: Callback<List<storylistResponse>>{
            override fun onResponse(
                call: Call<List<storylistResponse>>,
                response: Response<List<storylistResponse>>,
            ) {
                val storylist=response.body()
                val ItemList=arrayListOf<Items>()


                if (storylist != null) {
                    for(i in 0..storylist.size-1){
                        val title=storylist.get(i).title
                        var title_num=storylist.get(i).title_num.toInt()
                        //Toast.makeText(this@StorageBoxActivity, title_num, Toast.LENGTH_SHORT).show()
                        var content=storylist.get(i).contents
                        var id=storylist.get(i).id
                        var image= content[title_num].image
                        ItemList.add(Items(id,title,image))
                    }
                }
                var adapter=GridListAdapter(ItemList)
                binding.recyclerGrid.adapter=adapter
                binding.recyclerGrid.layoutManager= GridLayoutManager(applicationContext,4)

            }
            override fun onFailure(call: Call<List<storylistResponse>>, t: Throwable) {
                Log.d("log","fail")
            }
        })

        binding.backBtn.setOnClickListener {
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
        }


    }

}