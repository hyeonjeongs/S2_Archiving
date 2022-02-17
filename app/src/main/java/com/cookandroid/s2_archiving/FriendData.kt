package com.cookandroid.s2_archiving

//친구 정보 내용을 담는 모델
data class FriendData(
        var fId: String ="",
        var fName : String = "",
        var fPhone : String = "",
        var fBday : String = "",
        var fRel : String = "",
        var fImgUri : String = "",
        var fStar : Int = 1,
        var fAdd : String = "",
        var timestamp: String? = null
)