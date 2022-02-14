package com.cookandroid.s2_archiving.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cookandroid.s2_archiving.*
import com.cookandroid.s2_archiving.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_friendpage.*
import kotlinx.android.synthetic.main.fragment_friendpage.view.*

class FriendpageFragment : Fragment() {

    lateinit var adapter : RecyclerView.Adapter<PostAdapter.CustomViewHolder>
    lateinit var postDataList: ArrayList<PostData>
    lateinit var friendId:String

    private var mFirebaseAuth: FirebaseAuth? = FirebaseAuth.getInstance() //파이어베이스 인증
    private var mDatabaseRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("Firebase")//실시간 데이터베이스
    private lateinit var fbStorage: FirebaseStorage

    private lateinit var listener: ValueEventListener


    companion object {
        const val TAG : String = "로그"

        fun newInstance() : FriendpageFragment{
            return FriendpageFragment()
        }

    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val friendName = requireArguments().getString("friend_name")
        friendId = requireArguments().getString("friend_id").toString()

        postDataList = ArrayList<PostData>() //PostData 객체를 담을 ArrayList

        val view = inflater.inflate(R.layout.fragment_friendpage,container, false)
        view?.rv_post?.layoutManager = GridLayoutManager(getActivity(),2)
        val friendpageName = view.findViewById<TextView>(R.id.friendpage_name)
        friendpageName.text = friendName

        var btnGoWrite = view.findViewById<Button>(R.id.btnGoWrite)
        var btnEditFriend = view.findViewById<Button>(R.id.friendpage_edit_btn)

        // 버튼 클릭 시 친구 정보 수정 페이지로 이동
        btnEditFriend.setOnClickListener{
            val intent = Intent(requireContext(), EditFriendActivity::class.java)
            intent.putExtra("fId",friendId)
            startActivity(intent)
        }

        //플러스 버튼 클릭 시 게시글 쓰기 페이지로 이동
        btnGoWrite.setOnClickListener{
            val intent = Intent(requireContext(), PostActivity::class.java)
            intent.putExtra("fPostId",friendId)
            startActivity(intent)
        }

        return view


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listener = mDatabaseRef.child("UserPosts").child("${mFirebaseAuth!!.currentUser!!.uid}").child("${friendId!!}")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    postDataList.clear()

                    for (data: DataSnapshot in snapshot.getChildren()) {
                        var postData: PostData? = data.getValue(PostData::class.java)
                        Log.e("aaa", "${data}")
                        postDataList.add(postData!!) // 리스트에 넣기
                        Log.e("태그", "$postDataList")
                    }
                    adapter.notifyDataSetChanged() // 리스트 저장 및 새로 고침
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
        adapter = PostAdapter(postDataList,this.requireContext(),this)
        rv_post.adapter = adapter

    }
}