package com.cookandroid.s2_archiving

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ModifyAccount : AppCompatActivity() {

    //파이어베이스
    private var mFirebaseAuth: FirebaseAuth? = null
    private lateinit var mDatabaseRef: DatabaseReference

    private lateinit var mEtEmail: EditText
    private lateinit var mEtNickName: EditText
    private lateinit var mEtBeforePwd: EditText
    private lateinit var mEtAfterPwd: EditText
    private lateinit var btnModify: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modify_account)

        //파이어베이스에서 인스턴스 가져오기
        mFirebaseAuth = FirebaseAuth.getInstance()
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Firebase")

        //위젯 연결
        mEtNickName = findViewById(R.id.etModifyNickName)
        mEtBeforePwd = findViewById(R.id.etModifyBeforePwd)
        mEtAfterPwd = findViewById(R.id.etModifyAfterPwd)
        mEtEmail = findViewById(R.id.etModifyEmail)
        btnModify = findViewById(R.id.btnModify)

        //정보 수정 버튼
        btnModify.setOnClickListener {


            var strNickName: String = mEtNickName.text.toString()
            var strEmail: String = mEtEmail.text.toString()
            var strAfterPwd: String = mEtAfterPwd.text.toString()

            //파이어베이스에 정보 변경 내용 업데이트
            mDatabaseRef.child("UserAccount").child("${mFirebaseAuth?.currentUser!!.uid}")
                .addValueEventListener(object : ValueEventListener {

                    override fun onCancelled(error: DatabaseError) {

                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        var user: UserAccount? = snapshot.getValue(UserAccount::class.java)

                        val hashMap: HashMap<String, String> = HashMap()

                        hashMap.put("userEmail", strEmail)
                        hashMap.put("userNickname", strNickName)
                        hashMap.put("userPwd", strAfterPwd)

                        mDatabaseRef.child("UserAccount")
                            .child("${mFirebaseAuth?.currentUser!!.uid}").setValue(hashMap)

                    }
                })
            Toast.makeText(this, "등록완료", Toast.LENGTH_SHORT).show()
            //비밀번호 변경

        }
    }
}