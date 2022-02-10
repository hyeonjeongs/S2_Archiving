package com.cookandroid.s2_archiving.fragment

import android.content.Intent
import android.os.Bundle

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cookandroid.s2_archiving.FavoriteAdapter
import com.cookandroid.s2_archiving.PostActivity
import com.cookandroid.s2_archiving.R
import com.example.instaprac.FriendAdapter
import com.example.instaprac.FriendModel
import kotlinx.android.synthetic.main.activity_friend_page.*
import kotlinx.android.synthetic.main.fragment_friendpage.view.*
import kotlinx.android.synthetic.main.fragment_like.view.*

class FriendpageFragment : Fragment() {

    companion object {
        const val TAG : String = "로그"

        fun newInstance() : FriendpageFragment{
            return FriendpageFragment()
        }

    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return super.onCreateView(inflater, container, savedInstanceState)


        val friendlist = arrayListOf( //리스트형태의 객체들을 넣어줌
                FriendModel(R.drawable.ic_account,20220202,"아카이빙", R.drawable.ic_baseline_favorite_24),
                FriendModel(R.drawable.ic_account,20220203,"화이팅", R.drawable.ic_baseline_favorite_24),
                FriendModel(R.drawable.ic_account,20221010,"♥", R.drawable.ic_baseline_favorite_border_24),
                FriendModel(R.drawable.ic_account,20221013,"안드로이드", R.drawable.ic_baseline_favorite_24),
                FriendModel(R.drawable.ic_account,20221015,"열심히", R.drawable.ic_baseline_favorite_24),
                FriendModel(R.drawable.ic_account,20221015,"하기", R.drawable.ic_baseline_favorite_border_24)
        )

        val view = inflater.inflate(R.layout.fragment_friendpage,container, false)

        view?.rv_friend?.adapter = FriendAdapter(friendlist)
        view?.rv_friend?.layoutManager = GridLayoutManager(getActivity(),2)

        var btnGoWrite = view.findViewById<Button>(R.id.btnGoWrite)


        //플러스 버튼 클릭 시 게시글 쓰기 페이지로 이동
        btnGoWrite.setOnClickListener{
            val intent = Intent(getActivity(), PostActivity::class.java)
            startActivity(intent)
        }

        return view


    }
}