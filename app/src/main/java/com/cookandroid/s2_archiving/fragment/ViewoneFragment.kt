package com.cookandroid.s2_archiving.fragment

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.cookandroid.s2_archiving.*
import com.cookandroid.s2_archiving.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_view_one.*

class ViewoneFragment: Fragment(), onBackPressedListener {

    lateinit var friendId:String
    lateinit var postId:String
    lateinit var id:String
    private var mFirebaseAuth: FirebaseAuth? = FirebaseAuth.getInstance() //파이어베이스 인증
    private var mDatabaseRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("Firebase")//실시간 데이터베이스
    private lateinit var activitys: Activity
    private lateinit var viewOneImage: ImageView

    // 프레그먼트를 안고 있는 액티비티에 붙었을 때(프래그먼트가 엑티비티에 올라온 순간)
    override fun onAttach(context: Context) {
        super.onAttach(context)
        activitys = context as Activity
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_view_one,container, false)
        friendId = requireArguments().getString("friend_id").toString()
        postId = requireArguments().getString("post_id").toString()
        id = requireArguments().getString("id").toString()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        viewOneImage=view.findViewById(R.id.view_one_img)
        mDatabaseRef.child("UserPosts").child("${mFirebaseAuth?.currentUser!!.uid}")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (data: DataSnapshot in snapshot.children) {
                        var post: PostData? = data.getValue(PostData::class.java)
                        if(post!!.postId == postId) {
                            if (post!!.postPhotoUri == "") {
                                view_one_img?.setImageResource(R.drawable.man)
                            } else { // Uri가 있으면 그 사진 로드하기
                                Glide.with(activitys)
                                    .load(post!!.postPhotoUri)
                                    .into(viewOneImage)
                            }

                            view_one_date?.text=post!!.postDate
                            view_one_name?.text=post!!.postDateName
                            view_one_story?.text=post!!.post
                            if (post!!.heart == 1) {
                                view_one_heart?.setImageResource(R.drawable.heart_empty)
                            } else if (post!!.heart == 0) {
                                view_one_heart?.setImageResource(R.drawable.heart_full)
                            }
                            view_one_heart?.setOnClickListener {
                                heartEvent(post!!)
                            }
                        }
                    }

                }

                override fun onCancelled(error: DatabaseError) {

                }
            })

        btnViewOneBack.setOnClickListener{
            onBackPressed()
        }


    }

    override fun onBackPressed() {  //휴대폰의 뒤로가기 버튼 클릭 시
        if(this is ViewoneFragment){
            if(id == "post_adapter"){
                var fragment: Fragment = FriendpageFragment()
                var activityH = this.activity as MainActivity?
                var bundle: Bundle = Bundle()
                fragment.arguments = bundle
                bundle.putString("friend_id",friendId)
                activityH?.fragmentChange_for_adapter(fragment)
            } else if(id == "favorite_adapter"){
                var fragment: Fragment = LikeFragment()
                var activityH = this.activity as MainActivity?
                var bundle: Bundle = Bundle()
                fragment.arguments = bundle
                bundle.putString("friend_id",friendId)
                activityH?.fragmentChange_for_adapter(fragment)
            }
        }
    }

    private fun heartEvent(postData: PostData){
        var heart:Int?

        if(postData.heart==1){//하트가 비어있는데 클릭된경우
            heart=0

        }else{
            heart=1
        }
        val hashMap: HashMap<String, Any> = HashMap()
        hashMap.put("heart", heart!!)
        mDatabaseRef.child("UserPosts").child("${mFirebaseAuth?.currentUser!!.uid}").child(postData.postId).updateChildren(hashMap)
    }

}