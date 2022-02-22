package com.cookandroid.s2_archiving.fragment

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cookandroid.s2_archiving.*
import com.cookandroid.s2_archiving.R
import com.cookandroid.s2_archiving.model.MyFavorite
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_like.*
import kotlinx.android.synthetic.main.fragment_like.view.*

class LikeFragment : Fragment() {
    var fragmentView : View? = null

    lateinit var adapterL : RecyclerView.Adapter<FavoriteAdapter.CustomViewHolder>
    lateinit var postDataList: ArrayList<PostData>

    //파이어베이스
    private lateinit var database : FirebaseDatabase
    private lateinit var mDatabaseRef : DatabaseReference //실시간 데이터베이스 연결
    private var mFirebaseAuth: FirebaseAuth? = null
    var uid : String? = null
    var auth : FirebaseAuth? = null
    lateinit var friendId:String

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
        fragmentView = LayoutInflater.from(this.getActivity()).inflate(R.layout.fragment_like,container, false)
        //파이어베이스 계정, 리얼타임 데이터베이스
        mFirebaseAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance() //파이어베이스 데이터베이스 연동
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Firebase")
        auth = FirebaseAuth.getInstance()
        uid = arguments?.getString("favoriteUid")
        postDataList = ArrayList()
        fragmentView?.fragmentlike_rv?.layoutManager = GridLayoutManager(this.requireContext(),2)

        return fragmentView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mDatabaseRef.child("UserPosts").child("${mFirebaseAuth?.currentUser!!.uid}")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    postDataList.clear() //없으면 비어주기
                    for (data: DataSnapshot in snapshot.children) {
                        var post: PostData? = data.getValue(PostData::class.java)
                        if(post!!.heart==0){
                            postDataList.add(post!!)
                        }
                    }
                    adapterL.notifyDataSetChanged() //리스트 저장 및 새로고침
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
        adapterL = FavoriteAdapter(postDataList,this.requireContext(),this)
        fragmentlike_rv.adapter=adapterL
    }

}