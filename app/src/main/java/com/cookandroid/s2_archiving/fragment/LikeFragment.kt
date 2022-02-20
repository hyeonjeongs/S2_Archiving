package com.cookandroid.s2_archiving.fragment

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cookandroid.s2_archiving.FavoriteAdapter
import com.cookandroid.s2_archiving.FriendDataAdapter
import com.cookandroid.s2_archiving.R
import com.cookandroid.s2_archiving.model.MyFavorite
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_like.*
import kotlinx.android.synthetic.main.fragment_like.view.*

class LikeFragment : Fragment() {
    var fragmentView : View? = null

    //파이어베이스
    private lateinit var database : FirebaseDatabase
    private lateinit var mDatabaseRef : DatabaseReference //실시간 데이터베이스 연결
    private var mFirebaseAuth: FirebaseAuth? = null
    var uid : String? = null
    var auth : FirebaseAuth? = null

    // context
    private lateinit var activity: Activity

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
        val fragmentView = LayoutInflater.from(activity).inflate(R.layout.fragment_like,container, false)
        //파이어베이스 계정, 리얼타임 데이터베이스
        mFirebaseAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance() //파이어베이스 데이터베이스 연동
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Firebase")
        auth = FirebaseAuth.getInstance()
        uid = arguments?.getString("favoriteUid")


        fragmentView?.fragmentlike_rv?.adapter = FavoriteAdapter(activity,this)
        fragmentView?.fragmentlike_rv?.layoutManager = GridLayoutManager(activity,2)

        return fragmentView
    }

}