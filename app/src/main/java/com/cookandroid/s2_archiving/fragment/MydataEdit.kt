package com.cookandroid.s2_archiving.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import android.widget.Toast.makeText
import androidx.fragment.app.Fragment
import com.cookandroid.s2_archiving.MainActivity
import com.cookandroid.s2_archiving.R
import com.cookandroid.s2_archiving.UserAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

class MydataEdit : Fragment() {

    // 프래그먼트 붙일 액티비티
    lateinit var activitys : MainActivity

    //파이어베이스에서 인스턴스 가져오기
    private var mFirebaseAuth: FirebaseAuth? = FirebaseAuth.getInstance()
    private var mDatabaseRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("Firebase")

    private lateinit var mTvEmail: TextView
    private lateinit var mEtNickName: EditText
    private lateinit var mEtBeforePwd: EditText
    private lateinit var mEtAfterPwd: EditText
    private lateinit var btnModify: Button

    companion object{
        const val TAG : String = "로그"

        fun newInstance() : MydataEdit{
            return MydataEdit()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    // 메모리에 올라갔을 때
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(MydataEdit.TAG, "MydataEdit - onCreate() called")

    }

    // 프레그먼트를 안고 있는 액티비티에 붙었을 때(프래그먼트가 엑티비티에 올라온 순간)
    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(MydataEdit.TAG, "UserFragement - onAttach() called")
        activitys = activity as MainActivity
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? { //edit_mydata xml파일이랑 연결
        val view = inflater.inflate(R.layout.edit_mydata,container, false)

        //위젯 연결
        mEtNickName = view.findViewById(R.id.etEditNameMyData)
        mEtBeforePwd = view.findViewById(R.id.etCurrentPwdMyData)
        mEtAfterPwd = view.findViewById(R.id.etNewPwdMyData)
        mTvEmail = view.findViewById(R.id.etEditEmailMyData)
        btnModify = view.findViewById(R.id.btnEditEditMyData)

        // 사용자의 닉네임, 이메일 출력(이메일은 수정 불가능)
        mDatabaseRef.child("UserAccount").child("${mFirebaseAuth?.currentUser!!.uid}").addValueEventListener(object :
            ValueEventListener {

            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                var user: UserAccount? = snapshot.getValue(UserAccount::class.java)
                mTvEmail.text = "${user!!.userEmail}"
                mEtNickName.setText("${user!!.userNickname}")

            }
        })

        //정보 수정 버튼
        btnModify.setOnClickListener {

            var strNickName: String = mEtNickName.text.toString()
            var strAfterPwd: String = ""

            //파이어베이스에 정보 변경 내용 업데이트
            mDatabaseRef.child("UserAccount").child("${mFirebaseAuth?.currentUser!!.uid}")
                .addValueEventListener(object : ValueEventListener {

                    override fun onCancelled(error: DatabaseError) {

                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        var user: UserAccount? = snapshot.getValue(UserAccount::class.java)

                        var strEmail:String = user!!.userEmail // 이메일은 원래 저장되어있던 이메일을 사용해야함
                        var comparePassword:String = user!!.userPwd// 현재 비밀번호
                        if(mEtBeforePwd.text.isNotEmpty()&&mEtAfterPwd.text.isNotEmpty()){ // 비밀번호를 변경하고자 한다면
                            if(comparePassword.equals(mEtBeforePwd.text.toString())) {
                                changePassword()
                                strAfterPwd = mEtAfterPwd.text.toString()
                            }
                            else{ // 현재 비밀번호 입력 오류 시
                                Toast.makeText(activity, "현재 비밀번호를 확인해주세요", Toast.LENGTH_SHORT)
                                strAfterPwd = user!!.userPwd // 원래 비밀번호로 update
                            }
                        }
                        else{ // 비밀번호를 변경하고자 하지 않는다면
                            strAfterPwd = user!!.userPwd
                        }


                        val hashMap: HashMap<String, String> = HashMap()
                        hashMap.put("userBirth", "")
                        hashMap.put("userEmail", strEmail)
                        hashMap.put("userId", user.userId)
                        hashMap.put("userNickname", strNickName)
                        hashMap.put("userPwd", strAfterPwd)

                        mDatabaseRef.child("UserAccount")
                            .child("${mFirebaseAuth?.currentUser!!.uid}").updateChildren(hashMap as Map<String, Any>)
                            .addOnSuccessListener { Log.e("changeinfo", "정보 변경 완료") }
                            .addOnFailureListener{ Log.e("changepw", "정보 변경 실패") }


                    }
                })
            // 프래그먼트 종료 후 UserFragment로 이동해야 함 (activity에서의 finish()기능..) --> 구글링해서 나오는 코드는 빈 화면이 나오게 됨

        }

        return view
    }

    // 비밀번호 변경 메소드
    private fun changePassword() {
        val user: FirebaseUser? = mFirebaseAuth!!.currentUser


        user?.updatePassword(mEtAfterPwd.text.toString())?.addOnCompleteListener{ task->
            if(task.isSuccessful){
                Log.e("changepw", "비밀번호 변경 완료")
            }
            else{
                Log.e("changepw", "비밀번호 변경 실패")
            }
        }
    }
}