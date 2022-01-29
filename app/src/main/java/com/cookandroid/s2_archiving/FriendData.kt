package com.cookandroid.s2_archiving

//친구 정보 내용을 담는 모델
data class FriendData(
        var fName : String? = null,
        var fPhone : String? = null,
        var fBday : String? = null,
        var fRel : String? = null,
        var fImgurl : String? = null,
        var fStar : String? = null,
        var fAdd : String? = null
)