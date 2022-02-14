package com.cookandroid.s2_archiving

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Gallery
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.add_friend.*


class PostActivity : AppCompatActivity() {

    // 이미지 선택시 상수 값값
    var PICK_IMAGE_FROM_ALBUM = 0
    var photoUri: Uri? = null

    lateinit var cameraPermission: ActivityResultLauncher<String>
    lateinit var storagePermission: ActivityResultLauncher<String>

    lateinit var cameraLauncher: ActivityResultLauncher<Uri>
    lateinit var galleryLauncher: ActivityResultLauncher<String>

    var imgUrl: String = ""
    private var postId: String = ""

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

        mEtDate = findViewById<EditText>(R.id.etPostDate)
        mEtDateName = findViewById<EditText>(R.id.etPostName)
        mEtPost = findViewById<EditText>(R.id.etWritePost)
        mBtnPostRegister = findViewById<Button>(R.id.btnPostRegister)


        var friendId = getIntent().getStringExtra("fPostId")
        postId = mDatabaseRef.ref.child("UserPosts").child("${mFirebaseAuth!!.currentUser!!.uid}").child("${friendId!!}").push().key.toString()

        mBtnPostRegister.setOnClickListener {
            val hashMap: HashMap<String, String> = HashMap()
            var strDate: String = mEtDate.text.toString()
            var strDateName: String = mEtDateName.text.toString()
            var strPost: String = mEtPost.text.toString()
            var strPostId: String = postId

            hashMap.put("postDate", strDate)
            hashMap.put("postDateName", strDateName)
            hashMap.put("postId", strPostId)
            hashMap.put("post", strPost)

            mDatabaseRef.ref.child("UserPosts").child("${mFirebaseAuth!!.currentUser!!.uid}").child("$friendId").push().setValue(hashMap)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            Toast.makeText(this, "등록완료", Toast.LENGTH_SHORT).show()
                        }
                    }

            Toast.makeText(this, "게시글 추가 완료", Toast.LENGTH_SHORT).show()
            var intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()

        }

        // onActivityResult
        fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)
            if (requestCode == PICK_IMAGE_FROM_ALBUM) {
                if (resultCode == Activity.RESULT_OK) {
                    // This is path to the selected image
                    photoUri = data?.data
                } else {
                    // Exit the addPhotoActivity if you leave the album without selecting it
                    finish()
                }
            }
        }

    }
}





