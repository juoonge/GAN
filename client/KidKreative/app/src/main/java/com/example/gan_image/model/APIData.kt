package com.example.gan_image.model

import com.google.gson.annotations.SerializedName

/***********Image API Class**************/
data class ImageResponse(
    @SerializedName("text")
    var text:String,
    @SerializedName("image")
    var image:String
)

data class ImagePost(
    @SerializedName("text")
    var text:String,
    @SerializedName("image")
    var image:String
)


/***********FairyTale API Class**************/
// 동화책 보관함 data
data class storylistResponse(
    @SerializedName("author")
    val author: String,
    @SerializedName("contents")
    val contents: List<storydetailResponse>,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("title_num")
    val title_num: String,

    )

// storage에 보여줄 data 저장
data class Items(val id:Int, val title:String, val image:String)

data class storydetailResponse(
    @SerializedName("element_id")
    val elementId: Int,
    val image: String,
    val text: String
)

data class makestoryPost(
    // 제목은 공백으로 넘겨주고,
    @SerializedName("title")
    var title:String,
    @SerializedName("author")
    val author:String,
    @SerializedName("title_num")
    val title_num:String,
)

data class makestoryElePost(
    @SerializedName("story_id")
    val storyID: Int,
    @SerializedName("text")
    val text:String,
    @SerializedName("image")
    val image:String
)

data class changestoryElePost(
    @SerializedName("story_id")
    val storyID: Int,
    @SerializedName("text")
    val text:String,
    @SerializedName("image")
    val image:String,
    @SerializedName("element_id")
    val element_id:String
)
data class deletestoryResponse(
    @SerializedName("msg")
    var msg:String
)


/*****************GPT Class**************/
data class AI_image(
    var prompt : String? =null,
    var style :String?="fantasy-art"
)

data class AI_image_response(
    @SerializedName("text") var text_response : String,
    @SerializedName("image") var image_response : String,
)

data class RecommendNext(
    var previous : String? =null
)

data class RecommendNextResponse(
    @SerializedName("code") var code : String,
    @SerializedName("msg") var msg : String,
)

data class Compatibility(
    var check : String? =null
)

data class CompatibilityResponse(
    @SerializedName("code") var code : String,
    @SerializedName("msg") var msg : String,
)

data class EnglishWord(
    var check : String,
)

data class EnglishWordResponse(
    @SerializedName("eng1") var eng1 : String,
    @SerializedName("eng2") var eng2 : String,
    @SerializedName("kor1")var kor1 : String,
    @SerializedName("kor2")var kor2 : String
)

/***********Login, Signup Class**************/

data class Signup(
    var id : String? =null ,
    var password : String?=null,
    var password2: String?=null,
    var nickname : String?=null
)

data class SignupResponse(
    @SerializedName("msg") var msg : String,
)

data class LoginResponse(
    @SerializedName("id") var id : String,
    @SerializedName("tokens") var token : String,
)

data class Login(
    var id : String? =null ,
    var password : String?=null,
)

data class CheckUserResponse(
    @SerializedName("msg") var msg : String
)

data class CheckUser(
    var id : String? =null
)
data class UpdateUserResponse(
    @SerializedName("msg")
    var msg : String
)

data class UpdateUser(
    var id : String,
    var nickname : String,
    var token:String
)