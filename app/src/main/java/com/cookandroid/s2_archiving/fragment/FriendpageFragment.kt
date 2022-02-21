package com.cookandroid.s2_archiving.fragment

import android.app.Activity
import android.content.Intent
import android.media.Image
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cookandroid.s2_archiving.*
import com.cookandroid.s2_archiving.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_friendpage.*
import kotlinx.android.synthetic.main.fragment_friendpage.view.*
import org.w3c.dom.Text

class FriendpageFragment : Fragment(), onBackPressedListener {

    lateinit var adapter : RecyclerView.Adapter<PostAdapter.CustomViewHolder>
    lateinit var postDataList: ArrayList<PostData>
    lateinit var friendId:String
    lateinit var friendpageName:TextView
    lateinit var ivfriendimg:ImageView
    lateinit var starImg:ImageView
    var starCount:Int = 0

    private var mFirebaseAuth: FirebaseAuth? = FirebaseAuth.getInstance() //파이어베이스 인증
    private var mDatabaseRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("Firebase")//실시간 데이터베이스

    // context
    private lateinit var activitys: Activity


    companion object {
        const val TAG : String = "로그"

        fun newInstance() : FriendpageFragment{
            return FriendpageFragment()
        }

    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        activitys = context as Activity
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        friendId = requireArguments().getString("friend_id").toString()

        postDataList = ArrayList() //PostData 객체를 담을 ArrayList

        val view = inflater.inflate(R.layout.fragment_friendpage,container, false)
        view?.rv_post?.layoutManager = GridLayoutManager(activity,2)
        friendpageName = view.findViewById(R.id.friendpage_name)
        ivfriendimg = view.findViewById(R.id.account_profile)
        starImg = view.findViewById(R.id.account_star)



        var btnGoWrite = view.findViewById<Button>(R.id.btnGoWrite)
        var btnEditFriend = view.findViewById<Button>(R.id.friendpage_edit_btn)
        var friendbackbtn = view.findViewById<Button>(R.id.friendpage_back_btn)

        starImg.setOnClickListener {
            starAction(friendId)
        }

        // 버튼 클릭 시 친구 정보 수정 페이지로 이동
        btnEditFriend.setOnClickListener{
            val intent = Intent(activity, EditFriendActivity::class.java)
            intent.putExtra("fId",friendId)
            startActivity(intent)
        }

        //플러스 버튼 클릭 시 게시글 쓰기 페이지로 이동
        btnGoWrite.setOnClickListener{
            val intent = Intent(activity, PostActivity::class.java)
            intent.putExtra("fPostId",friendId)
            startActivity(intent)
        }

        friendbackbtn.setOnClickListener {
            onBackPressed()
        }

        return view


    }

    private fun starAction(friendId: String) {
        var star:Int?
        if(starCount==1){
            star = 0
        }
        else{
            star= 1
        }
        val hashMap: HashMap<String, Any> = HashMap()
        hashMap.put("fStar", star!!)
        mDatabaseRef.child("UserFriends").child("${mFirebaseAuth?.currentUser!!.uid}").child(friendId).updateChildren(hashMap)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mDatabaseRef.child("UserFriends").child("${mFirebaseAuth?.currentUser!!.uid}").child(friendId).addValueEventListener(object : ValueEventListener {

            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                var friend:FriendData? = snapshot.getValue(FriendData::class.java)
                friendpageName.text = friend!!.fName
                // 사진 url 추가 후 load하는 코드 넣을 자리
                if (friend!!.fImgUri == "") {
                    ivfriendimg.setImageResource(R.drawable.user)
                } else { // userPhotoUri가 있으면 그 사진 로드하기
                    Glide.with(activitys)
                        .load(friend!!.fImgUri)
                        .into(ivfriendimg)
                }
                if (friend.fStar == 1) {
                    starImg.setImageResource(R.drawable.star_empty)
                } else if (friend.fStar == 0) {
                    starImg.setImageResource(R.drawable.star_full)
                }
                starCount = friend.fStar



            }
        })

        mDatabaseRef.child("UserPosts").child("${mFirebaseAuth!!.currentUser!!.uid}")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    postDataList.clear()
                    Log.e("친구 아이디 ", friendId)

                    for (data: DataSnapshot in snapshot.children) {
                        var postData: PostData? = data.getValue(PostData::class.java)
                        if(postData!!.postFriendId==friendId){ // 해당 게시글이 현재 친구의 아이디를 포함하고 있다면
                            postDataList.add(postData!!) // 리스트에 넣기
                        }
                    }
                    adapter.notifyDataSetChanged() // 리스트 저장 및 새로 고침
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
        adapter = PostAdapter(postDataList,this.requireContext(),this)
        rv_post.adapter = adapter

    }

    override fun onBackPressed() {  //휴대폰의 뒤로가기 버튼 클릭 시
        if(this is FriendpageFragment){
            var fragment: Fragment = HomeFragment()
            var activityH = this.activity as MainActivity?
            activityH?.fragmentChange_for_adapter(fragment)
        }
    }
}