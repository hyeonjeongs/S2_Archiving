package com.cookandroid.s2_archiving

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

class ModifyAccount : AppCompatActivity() {

    //파이어베이스에서 인스턴스 가져오기
    private var mFirebaseAuth: FirebaseAuth? = FirebaseAuth.getInstance()
    private var mDatabaseRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("Firebase")

    private lateinit var mTvEmail: TextView
    private lateinit var mEtNickName: EditText
    private lateinit var mEtBeforePwd: EditText
    private lateinit var mEtAfterPwd: EditText
    private lateinit var btnModify: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modify_account)

        //위젯 연결
        mEtNickName = findViewById(R.id.etModifyNickName)
        mEtBeforePwd = findViewById(R.id.etModifyBeforePwd)
        mEtAfterPwd = findViewById(R.id.etModifyAfterPwd)
        mTvEmail = findViewById(R.id.etModifyEmail)
        btnModify = findViewById(R.id.btnModify)

        // 사용자의 닉네임, 이메일 출력(이메일은 수정 불가능)
        mDatabaseRef.child("UserAccount").child("${mFirebaseAuth?.currentUser!!.uid}").addValueEventListener(object : ValueEventListener {

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
            var strAfterPwd: String = mEtAfterPwd.text.toString()

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
                                Toast.makeText(this@ModifyAccount, "현재 비밀번호를 확인해주세요", Toast.LENGTH_SHORT)
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
                        hashMap.put("userPhone", "")
                        hashMap.put("userPwd", strAfterPwd)

                        mDatabaseRef.child("UserAccount")
                            .child("${mFirebaseAuth?.currentUser!!.uid}").setValue(hashMap)

                    }
                })

        }
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