package com.cookandroid.s2_archiving

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cookandroid.s2_archiving.fragment.HomeFragment
import com.example.instaprac.FriendAdapter
import com.example.instaprac.FriendModel
import kotlinx.android.synthetic.main.activity_friend_page.*

class FriendPage : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend_page)

        val homefragment = HomeFragment()

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

        //플러스 버튼 클릭 시 게시글 쓰기 페이지로 이동
        btnGoWrite.setOnClickListener{
            val intent = Intent(this,WriteActivity::class.java)
            startActivity(intent)
        }



    }
}