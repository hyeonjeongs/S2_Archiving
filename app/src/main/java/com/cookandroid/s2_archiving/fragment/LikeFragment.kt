package com.cookandroid.s2_archiving.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cookandroid.s2_archiving.FavoriteAdapter
import com.cookandroid.s2_archiving.R
import com.cookandroid.s2_archiving.model.MyFavorite
import kotlinx.android.synthetic.main.fragment_like.*
import kotlinx.android.synthetic.main.fragment_like.view.*

class LikeFragment : Fragment() {
    var fragmentView : View? = null

    companion object{
        fun newInstance() : LikeFragment{
            return LikeFragment()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? { //like_fragment xml파일이랑 연결
        val friendlist = arrayListOf( //리스트형태의 객체들을 넣어줌
            MyFavorite(R.drawable.food3,"박씨",20220201,"#재밌는날",R.drawable.ic_baseline_favorite_24),
            MyFavorite(R.drawable.food2,"김씨",20220501,"#깜짝놀란날",R.drawable.ic_baseline_favorite_24)
        )

        val fragmentView = LayoutInflater.from(activity).inflate(R.layout.fragment_like,container, false)
        fragmentView?.fragmentlike_rv?.adapter = FavoriteAdapter(friendlist)
        fragmentView?.fragmentlike_rv?.layoutManager = GridLayoutManager(activity,2)

        return fragmentView
    }

}