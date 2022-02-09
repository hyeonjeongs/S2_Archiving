package com.cookandroid.s2_archiving

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.io.File

class PostActivity_1 : AppCompatActivity() {

    private lateinit var mFirebaseAuth: FirebaseAuth // 파이어베이스 인증 처리
    private lateinit var mDatabaseRef: DatabaseReference // 실시간 데이터 베이스

    private lateinit var mEtDate: EditText // 날짜
    private lateinit var mEtDateName: EditText // 기념일 이름
    private lateinit var mEtPost: EditText // 글 내용
    private lateinit var mBtnPostRegister: Button // 게시글 업로드 버튼

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)

        //파이어베이스 계정, 리얼타임 데이터베이스
        mFirebaseAuth = FirebaseAuth.getInstance()
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Firebase")

        mEtDate = findViewById<EditText>(R.id.tvDate)
        mEtDateName = findViewById<EditText>(R.id.tvDateName)
        mEtPost = findViewById<EditText>(R.id.tvWriteText)
        mBtnPostRegister = findViewById<Button>(R.id.btnPostRegister)

        mBtnPostRegister.setOnClickListener{
            val hashMap : HashMap<String, String> = HashMap()

            var strDate: String = mEtDate.text.toString()
            var strDateName: String = mEtDateName.text.toString()
            var strPost: String = mEtPost.text.toString()


            hashMap.put("uid", mFirebaseAuth!!.currentUser!!.uid)
            hashMap.put("postDate", strDate)
            hashMap.put("postDateName", strDateName)
            hashMap.put("post", strPost)

            mDatabaseRef.ref.child("UserPosts").child("${mFirebaseAuth!!.currentUser!!.uid}").push().setValue(hashMap)
                    .addOnCompleteListener {
                        if(it.isSuccessful){
                            Toast.makeText(this, "등록완료", Toast.LENGTH_SHORT).show()
                        }
                    }

            Toast.makeText(this, "게시글 추가 완료", Toast.LENGTH_SHORT).show()
            var intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()

        }
    }
}