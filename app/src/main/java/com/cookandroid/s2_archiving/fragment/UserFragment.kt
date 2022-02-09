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
import com.cookandroid.s2_archiving.LoginActivity
import com.cookandroid.s2_archiving.ModifyAccount
import com.cookandroid.s2_archiving.R
import com.cookandroid.s2_archiving.UserAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

class UserFragment : Fragment() {

    //파이어베이스에서 인스턴스 가져오기
    private var mFirebaseAuth: FirebaseAuth? = FirebaseAuth.getInstance()
    private var mDatabaseRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("Firebase")
    private lateinit var listener: ValueEventListener


    // xml 요소들
    private lateinit var btnChangeinfo : Button
    private lateinit var btnLogout : Button
    private lateinit var btnDrop : Button
    private lateinit var mTvEmail:TextView
    private lateinit var mTvNickName:TextView
    private lateinit var ivInfoimg : ImageView

    // context
    private lateinit var activity: Activity

    companion object {
        const val TAG : String = "로그"

        fun newInstance() : UserFragment{
            return UserFragment()
        }

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

    // 메모리에 올라갔을때
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "UserFragement - onCreate() called")

    }

    // 프레그먼트를 안고 있는 액티비티에 붙었을 때(프래그먼트가 엑티비티에 올라온 순간)
    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(TAG, "UserFragement - onAttach() called")
        activity = context as Activity
    }

    // 뷰가 생성되었을 때
    // 프레그먼트와 레이아웃을 연결시켜주는 부분이다.
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        Log.d(TAG, "UserFragement - onCreateView() called")

        //fragment내 findViewById 사용
        val view = inflater.inflate(R.layout.fragment_user, container, false)
        btnChangeinfo  = view.findViewById(R.id.btnChangeInfo)
        btnLogout = view.findViewById(R.id.btnLogout)
        btnDrop = view.findViewById(R.id.btnDrop)
        mTvEmail = view.findViewById(R.id.tvNickName)
        mTvNickName = view.findViewById(R.id.tvEmail)

        //ivInfoimg : ImageView = view.findViewById(R.id.ivProf)
        //닉네임 받아오기
        mTvNickName = view.findViewById(R.id.tvNickName)
        mTvEmail = view.findViewById(R.id.tvEmail)

        val mFirebaseUser : FirebaseUser? = mFirebaseAuth?.currentUser

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Firebase")
            .child("UserAccount").child(mFirebaseUser!!.uid)

        //화면에 사용자 프로필 이미지, 닉네임, 이메일 출력
        listener=mDatabaseRef.addValueEventListener(object : ValueEventListener {

            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                var user: UserAccount? = snapshot.getValue(UserAccount::class.java)

                mTvNickName.text = ("${user?.userNickname}")

                mTvEmail.text = ("${user?.userEmail}")

//                if(user!!.userProfileImage.equals("")){
//                    ivInfoimg.setImageResource(R.drawable.user)
//                }else{
//                    var cropOptions : RequestOptions = RequestOptions()
//                    Glide.with(this@ProfileFragment)
//                            .load(user!!.user_profileImage)
//                            .apply(cropOptions.optionalCircleCrop())
//                            .into(ivInfoimg)
//                }
            }
        })


        var mFirebaseAuth : FirebaseAuth? = null //파이어베이스 인증

        mFirebaseAuth = FirebaseAuth.getInstance()

        //정보 수정 액티비티로 넘어가는 버튼
        btnChangeinfo.setOnClickListener {

            val intent = Intent(getActivity(), ModifyAccount::class.java)
            startActivity(intent)
        }

        //로그아웃 버튼
        btnLogout.setOnClickListener {

            mFirebaseAuth!!.signOut()
            getActivity()?.finishAffinity()
            val packageManager = requireContext().packageManager
            val intent = packageManager.getLaunchIntentForPackage(requireContext().packageName)
            val componentName = intent!!.component
            val mainIntent = Intent.makeRestartActivityTask(componentName)
            requireContext().startActivity(mainIntent)
            Runtime.getRuntime().exit(0)
        }

        //탈퇴 버튼
        btnDrop.setOnClickListener {
            mDatabaseRef.removeEventListener(listener)
            Log.e("UserFragment", "listner remove")
            mDatabaseRef.removeValue()
            mFirebaseAuth!!.currentUser!!.delete()
            getActivity()?.finishAffinity()
            Log.e("UserFragment", "MainActivity Destory")
            val packageManager = requireContext().packageManager
            val intent = packageManager.getLaunchIntentForPackage(requireContext().packageName)
            val componentName = intent!!.component
            val mainIntent = Intent.makeRestartActivityTask(componentName)
            requireContext().startActivity(mainIntent)
            Runtime.getRuntime().exit(0)

        }


        return view
    }



    override fun onDestroyView() {
        super.onDestroyView()
        Log.e("UserFragment", "유저프래그먼트 사라짐")
    }

    override fun onDestroy() {
        super.onDestroy()
        mDatabaseRef.removeEventListener(listener)
        Log.e("UserFragment", "유저프래그먼트 파괴됨")
    }

    override fun onDetach() {
        super.onDetach()
        Log.e("UserFragment", "유저프래그먼트 분리됨")
    }

}