package com.cookandroid.s2_archiving


import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
        mEtNickName = findViewById(R.id.etEditNameMyData)
        mEtBeforePwd = findViewById(R.id.etCurrentPwdMyData)
        mEtAfterPwd = findViewById(R.id.etNewPwdMyData)
        mTvEmail = findViewById(R.id.etEditEmailMyData)
        btnModify = findViewById(R.id.btnEditEditMyData)

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
                            hashMap.put("userPwd", strAfterPwd)

                            mDatabaseRef.child("UserAccount")
                                    .child("${mFirebaseAuth?.currentUser!!.uid}").updateChildren(hashMap as Map<String, Any>)
                                    .addOnSuccessListener { Log.e("changeinfo", "정보 변경 완료") }
                                    .addOnFailureListener{ Log.e("changepw", "정보 변경 실패") }

                            finish()


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