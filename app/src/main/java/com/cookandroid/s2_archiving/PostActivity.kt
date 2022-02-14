package com.cookandroid.s2_archiving

import android.Manifest
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_write.*

class PostActivity : AppCompatActivity() {


    var photoUri: Uri? = null

    lateinit var cameraPermission: ActivityResultLauncher<String>
    lateinit var storagePermission : ActivityResultLauncher<String>

    lateinit var cameraLauncher: ActivityResultLauncher<Uri>
    lateinit var galleryLauncher: ActivityResultLauncher<String>

    var imgUrl : String = ""

    private lateinit var mFirebaseAuth: FirebaseAuth // 파이어베이스 인증 처리
    private lateinit var mDatabaseRef: DatabaseReference // 실시간 데이터 베이스

    private lateinit var mEtDate: EditText // 날짜
    private lateinit var mEtDateName: EditText // 기념일 이름
    private lateinit var mEtPost: EditText // 글 내용
    private lateinit var mBtnPostRegister: Button // 게시글 업로드 버튼


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write)

        //파이어베이스 계정, 리얼타임 데이터베이스
        mFirebaseAuth = FirebaseAuth.getInstance()
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Firebase")

        mEtDate = findViewById<EditText>(R.id.etWriteDate)
        mEtDateName = findViewById<EditText>(R.id.etWriteName)
        mEtPost = findViewById<EditText>(R.id.etWritePost)
        mBtnPostRegister = findViewById<Button>(R.id.btnWriteRegister)

        var friendId = getIntent().getStringExtra("fPostId")

        storagePermission=registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ){isGranted ->
            if(isGranted){
                setViews()
            } else{
                Toast.makeText(baseContext, "외부 저장소 권한을 승인해야 앱을 사용할 수 있습니다.", Toast.LENGTH_SHORT).show()
                finish()
            }
        }


        cameraPermission=registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ){isGranted ->
            if(isGranted){
                //openCamera()
            } else{
                Toast.makeText(baseContext, "카메라 권한을 승인해야 앱을 사용할 수 있습니다.", Toast.LENGTH_SHORT).show()
                finish()
            }
        }



        galleryLauncher = registerForActivityResult(
            ActivityResultContracts.
            GetContent()){uri->
            ivWriteCamera.setImageURI(uri)
        }

        storagePermission.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)

        mBtnPostRegister.setOnClickListener{
            val hashMap : HashMap<String, String> = HashMap()
            var strDate: String = mEtDate.text.toString()
            var strDateName: String = mEtDateName.text.toString()
            var strPost: String = mEtPost.text.toString()


            hashMap.put("uid", mFirebaseAuth!!.currentUser!!.uid)
            hashMap.put("postDate", strDate)
            hashMap.put("postDateName", strDateName)
            hashMap.put("post", strPost)

            mDatabaseRef.ref.child("UserPosts").child("${mFirebaseAuth!!.currentUser!!.uid}").child("$friendId").push().setValue(hashMap)
                .addOnCompleteListener {
                    if(it.isSuccessful){
                        Toast.makeText(this, "등록완료", Toast.LENGTH_SHORT).show()
                    }
                }

            Toast.makeText(this, "게시글 추가 완료", Toast.LENGTH_SHORT).show()
//            var intent = Intent(this, MainActivity::class.java)
//            startActivity(intent)
            finish()

        }

    }

    fun setViews(){
        ivWriteCamera.setOnClickListener{
            openGallery()
        }
        tvWriteImage.setOnClickListener{
            openGallery()
        }
    }

    fun openGallery(){
        galleryLauncher.launch("image/*")
    }



}