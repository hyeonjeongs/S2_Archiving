package com.cookandroid.s2_archiving.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.cookandroid.s2_archiving.*
import com.cookandroid.s2_archiving.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

class UserFragment : Fragment() {

    //파이어베이스에서 인스턴스 가져오기
    private var mFirebaseAuth: FirebaseAuth? = FirebaseAuth.getInstance()
    private var mDatabaseRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("Firebase")



    // xml 요소들
    private lateinit var btnChangeinfo : Button
    private lateinit var btnLogout : Button
    private lateinit var btnDrop : Button
    private lateinit var mTvEmail:TextView
    private lateinit var mTvNickName:TextView
    private lateinit var ivInfoimg : ImageView
    private lateinit var muserfragLogobtn :Button


    // context
    private lateinit var activitys: Activity

    companion object {
        const val TAG : String = "로그"

        fun newInstance() : UserFragment{
            return UserFragment()
        }

    }

    // 메모리에 올라갔을때
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    // 프레그먼트를 안고 있는 액티비티에 붙었을 때(프래그먼트가 엑티비티에 올라온 순간)
    override fun onAttach(context: Context) {
        super.onAttach(context)
        activitys = context as Activity
    }

    // 뷰가 생성되었을 때
    // 프레그먼트와 레이아웃을 연결시켜주는 부분이다.
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        Log.d(TAG, "UserFragement - onCreateView() called")

        //fragment내 findViewById 사용
        val view = inflater.inflate(R.layout.fragment_user, container, false)
        btnChangeinfo  = view.findViewById(R.id.btnChangeInfo)
        btnLogout = view.findViewById(R.id.btnLogout)
        btnDrop = view.findViewById(R.id.btnDrop)
        mTvEmail = view.findViewById(R.id.tvNickName)
        mTvNickName = view.findViewById(R.id.tvEmail)
        ivInfoimg  = view.findViewById(R.id.ivProf)

        //닉네임과 이메일 받아오기
        mTvNickName = view.findViewById(R.id.tvNickName)
        mTvEmail = view.findViewById(R.id.tvEmail)
        muserfragLogobtn = view.findViewById(R.id.userfragLogobtn)

        val mFirebaseUser : FirebaseUser? = mFirebaseAuth?.currentUser
        val userId:String = mFirebaseUser!!.uid

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Firebase")
            .child("UserAccount").child(userId)

        mFirebaseAuth = FirebaseAuth.getInstance()

        //정보 수정 액티비티로 넘어가는 버튼
        btnChangeinfo.setOnClickListener {

            val intent = Intent(activity, ModifyAccount::class.java)
            startActivity(intent)
        }

        //로그아웃 버튼
        btnLogout.setOnClickListener {

            activity?.finish()
            startActivity(Intent(activity,LoginActivity::class.java))
            mFirebaseAuth!!.signOut()

        }

        //탈퇴 버튼
        btnDrop.setOnClickListener {

            mFirebaseUser.delete().addOnCompleteListener { task ->
                if(task.isSuccessful){
                    mDatabaseRef.removeValue()
                    mFirebaseAuth!!.signOut()
                    startActivity(Intent(activity,LoginActivity::class.java))
                }
            }


        }

        //Logo 버튼
        muserfragLogobtn.setOnClickListener {//버튼 클릭시 HomeFragment로 이동시켜줌
            var fragment: Fragment = HomeFragment()
            var activityH = this.activity as MainActivity?

            activityH?.fragementChange_view(fragment)

        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //화면에 사용자 프로필 이미지, 닉네임, 이메일 출력
        mDatabaseRef.addValueEventListener(object : ValueEventListener {

            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.value ==null){ // 널이면 아무것도하지마

                }
                else {
                    var user: UserAccount? = snapshot.getValue(UserAccount::class.java)

                    mTvNickName.text = "${user?.userNickname}"

                    mTvEmail.text = "${user?.userEmail}"

                    // 사진 url 추가 후 load하는 코드 넣을 자리
                    if (user!!.userPhotoUri == "") {
                        ivInfoimg.setImageResource(R.drawable.man)
                    } else { // userPhotoUri가 있으면 그 사진 로드하기
                        Glide.with(activitys)
                            .load(user!!.userPhotoUri)
                            .into(ivInfoimg)
                    }
                }
            }
        })

    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.e("UserFragment", "유저프래그먼트 사라짐")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("UserFragment", "유저프래그먼트 파괴됨")
    }

    override fun onDetach() {
        super.onDetach()
        Log.e("UserFragment", "유저프래그먼트 분리됨")
    }

}