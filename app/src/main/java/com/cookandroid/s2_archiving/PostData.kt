package com.cookandroid.s2_archiving

//게시글 정보 내용을 담는 모델
data class PostData(
        var postPhotoUri:String="",
        var postId: String ="",
        var post : String = "",
        var postDate : String = "",
        var postDateName : String = "",
        var heart:Int = 0
)