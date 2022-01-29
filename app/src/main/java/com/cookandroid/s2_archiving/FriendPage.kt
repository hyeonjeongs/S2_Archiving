package com.cookandroid.s2_archiving

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.instaprac.FriendAdapter
import com.example.instaprac.FriendModel

class FriendPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend_page)
        val friendlist = arrayListOf( //리스트형태의 객체들을 넣어줌
                FriendModel(R.drawable.ic_account,20220202,"아카이빙",R.drawable.ic_baseline_favorite_24),
                FriendModel(R.drawable.ic_account,20220203,"화이팅",R.drawable.ic_baseline_favorite_24),
                FriendModel(R.drawable.ic_account,20221010,"♥",R.drawable.ic_baseline_favorite_border_24),
                FriendModel(R.drawable.ic_account,20221013,"안드로이드",R.drawable.ic_baseline_favorite_24),
                FriendModel(R.drawable.ic_account,20221015,"열심히",R.drawable.ic_baseline_favorite_24),
                FriendModel(R.drawable.ic_account,20221015,"하기",R.drawable.ic_baseline_favorite_border_24)
        )
        val rvfriend = findViewById<RecyclerView>(R.id.rv_friend)
        rvfriend.layoutManager = GridLayoutManager(this,2) //한 행에 두 열씩
        rvfriend.setHasFixedSize(true) //리사이클러뷰 성능 개선

        rvfriend.adapter = FriendAdapter(friendlist) //위에서 선언한 리스트를 어뎁터에 변수인 profileList로 전달

    }
}