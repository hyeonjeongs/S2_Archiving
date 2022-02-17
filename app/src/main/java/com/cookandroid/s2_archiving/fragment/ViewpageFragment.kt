package com.cookandroid.s2_archiving.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cookandroid.s2_archiving.*
import com.cookandroid.s2_archiving.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_view.*
import kotlinx.android.synthetic.main.activity_view.view.*

class ViewpageFragment: Fragment() {

    lateinit var adapterV : RecyclerView.Adapter<ViewAdapter.CustomViewHolder>
    lateinit var viewDataList: ArrayList<PostData>
    lateinit var friendId:String


    private var mFirebaseAuth: FirebaseAuth? = FirebaseAuth.getInstance() //파이어베이스 인증
    private var mDatabaseRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("Firebase")//실시간 데이터베이스
    private lateinit var fbStorage: FirebaseStorage

    private lateinit var listener: ValueEventListener

    companion object {
        const val TAG : String = "로그"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        friendId = requireArguments().getString("friend_id").toString()
        viewDataList = ArrayList()
        Log.e("친구 아이디 ", friendId)

        val view = inflater.inflate(R.layout.activity_view,container, false)
        view?.rv_view?.layoutManager = LinearLayoutManager(this.requireContext(), LinearLayoutManager.VERTICAL, false)


        return view


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        friendId = requireArguments().getString("friend_id").toString()

        mDatabaseRef.child("UserPosts").child("${mFirebaseAuth!!.currentUser!!.uid}").child("${friendId!!}")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.e("태그", "여기까지는 됨")
                    viewDataList.clear()

                    for (data in snapshot.children) {
                        val item = data.getValue<PostData>()
                        viewDataList.add(item!!)
                    }
                    adapterV.notifyDataSetChanged() //리스트 저장 및 새로고침

                }

                override fun onCancelled(error: DatabaseError) {

                }
            })



        adapterV = ViewAdapter(viewDataList,this.requireContext(),this)
        rv_view.adapter = adapterV



//        adapter = ViewAdapter(viewDataList)
//        rv_view.adapter=adapter

//        val viewList = arrayListOf(
//            PostData("이미지","ㅈ대ㅑ랴","아아아","하...","이름",0,0),
//            PostData("이미지","ㅈ대ㅑ랴","아아아","하...","이름",0,0),
//            PostData("이미지","ㅈ대ㅑ랴","아아아","하...","이름",0,0),
//            PostData("이미지","ㅈ대ㅑ랴","아아아","하...","이름",0,0),
//            )


    }


    //삭제, 수정 다이얼로그
//    fun onDialogBtnClicked(view: View){
//        val myCustomDialog = MyCustomDialog(this.requireContext())
//        myCustomDialog.show()
//    }

}