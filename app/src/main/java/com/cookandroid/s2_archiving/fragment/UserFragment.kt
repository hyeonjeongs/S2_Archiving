package com.cookandroid.s2_archiving.fragment

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
import com.cookandroid.s2_archiving.*
import com.cookandroid.s2_archiving.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_user.*


class UserFragment : Fragment() {

    lateinit var activitys : MainActivity

    lateinit var mDatabaseRef : DatabaseReference
    lateinit var mFirebaseAuth: FirebaseAuth

    companion object {
        const val TAG : String = "로그"

        fun newInstance() : UserFragment{
            return UserFragment()
        }

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

<<<<<<< Updated upstream
        //
//        btnChangeInfo.setOnClickListener {
//            activity?.let{
//                val intent = Intent(context, MydataEdit::class.java)
//                startActivity(intent)
//            }
//        }
=======
>>>>>>> Stashed changes
    }
    // 메모리에 올라갔을때
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "UserFragement - onCreate() called")

    }

    // 프레그먼트를 안고 있는 액티비티에 붙었을 때(프래그먼트가 엑티비티에 올라온 순간)
    override fun onAttach(context: Context) {
        super.onAttach(context)
        val d = Log.d(TAG, "UserFragement - onAttach() called")
        activitys = activity as MainActivity
    }

    override fun onDetach() {
        super.onDetach()
        //activitys= null


    }
    // 뷰가 생성되었을 때
    // 프레그먼트와 레이아웃을 연결시켜주는 부분이다.
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        Log.d(TAG, "UserFragement - onCreateView() called")

        //fragment내 findViewById 사용
        val view = inflater.inflate(R.layout.fragment_user, container, false)
        var btnChangeinfo : Button = view.findViewById(R.id.btnChangeInfo)
        var btnLogout : Button = view.findViewById(R.id.btnLogout)
        var btnDrop : Button = view.findViewById(R.id.btnDrop)
        var ivInfoimg : ImageView = view.findViewById(R.id.ivProf)
        //닉네임 받아오기
        var tvNickname : TextView = view.findViewById(R.id.tvNickName)
        var tvEmail : TextView = view.findViewById(R.id.tvEmail)

        mFirebaseAuth = FirebaseAuth.getInstance()
        val mFirebaseUser : FirebaseUser? = mFirebaseAuth?.currentUser

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Firebase")
                .child("UserAccount").child(mFirebaseUser!!.uid)

        //화면에 사용자 프로필 이미지, 닉네임, 이메일 출력
        mDatabaseRef.addValueEventListener(object : ValueEventListener {

            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                var user = snapshot.getValue(UserAccount::class.java)

                tvNickname.setText("${user!!.userNickname}")

                tvEmail.setText("${user!!.userEmail}")

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

            val intent = Intent(requireContext(), ModifyAccount::class.java)
            startActivity(intent)
        }

        //로그아웃 버튼
        btnLogout.setOnClickListener {

            mFirebaseAuth!!.signOut()
            val intent = Intent(getActivity(), LoginActivity::class.java)
            startActivity(intent)
        }

        //탈퇴 버튼
        btnDrop.setOnClickListener {

            mFirebaseAuth!!.currentUser!!.delete()
            mDatabaseRef.removeValue()
            val intent = Intent(getActivity(), LoginActivity::class.java)
            startActivity(intent)
            activity?.supportFragmentManager
                ?.beginTransaction()
                ?.remove(this)
                ?.commit()
        }

<<<<<<< Updated upstream
        val btnChangeInfo: Button = view.findViewById(R.id.btnChangeInfo)

        //내정보수정 버튼 눌렀을때
        btnChangeInfo.setOnClickListener(View.OnClickListener {
            activitys.OnFragmentChange(1) })

            return view
        }

=======
        return view
    }
>>>>>>> Stashed changes


}