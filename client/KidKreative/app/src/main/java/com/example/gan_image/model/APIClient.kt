package com.example.gan_image.model

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object APIClient {

    private const val BASE_URL= "https://88a3-58-237-126-254.ngrok-free.app/"

    // 상동 집 : private const val BASE_URL="http://192.168.0.5:8080/"
    // 상동 회사 : private const val BASE_URL="http://10.10.10.108:8080/"
    // 주원 : private const val BASE_URL = "https://88a3-58-237-126-254.ngrok-free.app/"
    // 정윤 집 : "http://192.168.55.209:8000/"
    // 정아: "http://192.168.56.1:8000/"
    // 다금 : "http://192.168.200.101:8000/"
    // 다금 핫스팟 : "http://172.20.10.3:8000/"
    fun create(): APIService {
        val clientBuilder = OkHttpClient.Builder()
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        clientBuilder.addInterceptor(loggingInterceptor)

        val gson: Gson = GsonBuilder().setLenient().create()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(clientBuilder.build())
            .build()//
            .create(APIService::class.java)
    }
}