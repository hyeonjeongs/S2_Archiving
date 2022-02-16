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
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_view.*
import kotlinx.android.synthetic.main.activity_view.view.*

class ViewpageFragment: Fragment() {

    lateinit var adapter : RecyclerView.Adapter<ViewAdapter.CustomViewHolder>
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

        viewDataList = ArrayList<PostData>()

        val view = inflater.inflate(R.layout.activity_view,container, false)
        view?.rv_view?.layoutManager = LinearLayoutManager(this.requireContext(), LinearLayoutManager.VERTICAL, false)


        return view


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        listener = mDatabaseRef.child("UserPosts").child("${mFirebaseAuth!!.currentUser!!.uid}").child("${friendId!!}")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.e("태그", "여기까지는 됨")
                    viewDataList.clear()

                    for (data : DataSnapshot in snapshot.children) {
                        Log.e("태그bbbb", "여기부터가 안된다ㅜㅜㅜㅜㅜㅜㅜㅜㅜㅜㅜㅜㅜ")
                        var viewData : PostData? = data.getValue(PostData::class.java)

                        viewDataList.add(viewData!!) //담은 데이터들을 배열리스트에 넣고 리사이클러뷰로 보낼 준비

                        Log.d("태그", "$viewDataList")
                    }
                    adapter.notifyDataSetChanged() //리스트 저장 및 새로고침

                }

                override fun onCancelled(error: DatabaseError) {

                }
            })



//        adapter = ViewAdapter(viewDataList)
//        rv_view.adapter=adapter

//        val viewList = arrayListOf(
//            PostData("이미지","ㅈ대ㅑ랴","아아아","하...","이름",0,0),
//            PostData("이미지","ㅈ대ㅑ랴","아아아","하...","이름",0,0),
//            PostData("이미지","ㅈ대ㅑ랴","아아아","하...","이름",0,0),
//            PostData("이미지","ㅈ대ㅑ랴","아아아","하...","이름",0,0),
//            )

        adapter = ViewAdapter(viewDataList,this.requireContext(),this)
        rv_view.adapter = adapter
    }


    //삭제, 수정 다이얼로그
//    fun onDialogBtnClicked(view: View){
//        val myCustomDialog = MyCustomDialog(this.requireContext())
//        myCustomDialog.show()
//    }

}