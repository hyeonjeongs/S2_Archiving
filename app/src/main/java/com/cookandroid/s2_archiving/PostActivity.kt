package com.cookandroid.s2_archiving

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class PostActivity : AppCompatActivity() {

    private lateinit var mFirebaseAuth: FirebaseAuth // 파이어베이스 인증 처리
    private lateinit var mDatabaseRef: DatabaseReference // 실시간 데이터 베이스
    private lateinit var mEtDate: EditText // 날짜
    private lateinit var mEtDateName: EditText // 기념일 이름
    private lateinit var mEtPost: EditText // 글 내용
    private lateinit var mBtnPostRegister: Button // 회원 가입 버튼

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)

        mFirebaseAuth = FirebaseAuth.getInstance()
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Firebase")
        mEtDate = findViewById<EditText>(R.id.tvDate)
        mEtDateName = findViewById<EditText>(R.id.tvDateName)
        mEtPost = findViewById<EditText>(R.id.tvWriteText)
        mBtnPostRegister = findViewById<Button>(R.id.btnPostRegister)




    }
}