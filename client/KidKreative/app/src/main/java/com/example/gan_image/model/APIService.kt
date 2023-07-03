package com.example.gan_image.model

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface APIService {
    // ------------- Image 생성 ------------------
    // text 보내고, 사진 받기, db에 저장X
    @GET("storyimage/")
    fun getImage(
        // url에 ?를 이용 -> @Path
        // 함수의 매개변수 값 서버 전달 -> @Query
        @Query("text") text:String
    ):Call<ImageResponse>

    // 다음버튼 누르면 text, image post하고 db 저장
    @POST("storyimage/")
    fun registerImage(
        @Body imagePost: ImagePost
    )

    // ------------- FairyTale -------------------
    @GET("fairytale/list/")
    fun getFairytaleList(
        @Header("Authorization") accessToken:String
    ):Call<List<storylistResponse>>

    @POST("fairytale/list/")
    fun makeStory(
        @Header("Authorization") accessToken:String,
        @Body makestory: makestoryPost
    ):Call<storylistResponse>

    @POST("fairytale/listelement/")
    fun makeStoryEle(
        @Header("Authorization") accessToken:String,
        @Body makestoryele:makestoryElePost
    ):Call<storylistResponse>

    //동화 삭제
    @DELETE("fairytale/list/{pk}/delete/")
    fun deleteStory(
        @Header("Authorization") accessToken:String,
        @Path("pk") pk:Int,
    ):Call<deletestoryResponse>

    //내용 수정
    @PUT("fairytale/listelement/")
    fun changeStoryEle(
        @Header("Authorization") accessToken:String,
        @Body changestoryele:changestoryElePost
    ):Call<storylistResponse>

    //동화내용정보수정하기
    @PUT("fairytale/changelistelement/{pk}/")
    fun putFairytaleEle(
        @Header("Authorization") accessToken:String,
        @Path("pk") pk:Int,
        @Body changestoryele:changestoryElePost
    ):Call<storylistResponse>

    @GET("fairytale/list/{id}/")
    fun getFairytaleDetail(
        @Header("Authorization") accessToken:String,
        @Path("id") id:Int
    ):Call<storylistResponse>

    @PUT("fairytale/list/{id}/") // title 변경
    fun putFairytaleTitle(
        @Header("Authorization") accessToken:String,
        @Path("id") id:Int,
        @Body makestory: makestoryPost
    ):Call<storylistResponse>

    //표지 변경
    @PUT("fairytale/list/{id}/")
    fun putFairytaleCover(
        @Header("Authorization") accessToken:String,
        @Path("id") id:Int,
        @Body makestory: makestoryPost
    ):Call<storylistResponse>

    //영어 추천
    @POST("gpt/pick_word/")
    fun pickWord(
        @Header("Authorization") accessToken:String,
        @Body pickWord:EnglishWord
    ):Call<EnglishWordResponse>




    @POST("/gpt/compatibility/")
    fun check(
        @Body jsonparams: Compatibility
    ): Call<CompatibilityResponse>

    @POST("gpt/recommend_next/")
    fun recommend_next(
        @Body jsonparams: RecommendNext
    ): Call<RecommendNextResponse>

    @POST("gpt/first_recommend_next/")
    fun first_recommend_next(
        @Body jsonparams: RecommendNext
    ): Call<RecommendNextResponse>


    //ai image base 64 불러오기
    @POST("getimage/")
    fun imageapi(
        @Body jsonparams: AI_image

    ): Call<AI_image_response>

    // ------------- Login,signup, id_check -------------------
    @POST("user/login/")
    fun login_user(@Body jsonparams: Login): Call<LoginResponse>

    @POST("user/signup/")
    fun signup_user(@Body jsonparams: Signup): Call<SignupResponse>

    @POST("user/check/")
    fun check_user(@Body jsonparams: CheckUser): Call<CheckUserResponse>

    @POST("user/update/")
    fun update_user(
        @Body jsonparams: UpdateUser
    ): Call<UpdateUserResponse>
}



