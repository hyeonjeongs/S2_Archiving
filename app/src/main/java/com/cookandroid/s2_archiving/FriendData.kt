package com.cookandroid.s2_archiving

//친구 정보 내용을 담는 모델
data class FriendData(
        var f_name : String? = null,
        var f_number : String? = null,
        var f_bday : String? = null,
        var f_relationship : String? = null,
        var f_imgUrl : String? = null
)