package com.cookandroid.s2_archiving

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_post.*
import kotlinx.android.synthetic.main.add_friend.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


class PostActivity : AppCompatActivity() {

    // 이미지 선택시 상수 값값
    var PICK_IMAGE_FROM_ALBUM = 0
    var photoUri: Uri? = null

    private var postId: String = ""
    private lateinit var friendId:String

    private lateinit var mFirebaseAuth: FirebaseAuth // 파이어베이스 인증 처리
    private lateinit var mDatabaseRef: DatabaseReference // 실시간 데이터 베이스
    private var storage : FirebaseStorage? = FirebaseStorage.getInstance()

    private lateinit var mEtFriendName: TextView
    private lateinit var mEtDate: EditText // 날짜
    private lateinit var mEtDateName: EditText // 기념일 이름
    private lateinit var mEtPost: EditText // 글 내용
    private lateinit var mBtnPostRegister: Button // 게시글 업로드 버튼
    private lateinit var mBtnPostClose: ImageView
    private lateinit var ivPostData:ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)

        //파이어베이스 계정, 리얼타임 데이터베이스
        mFirebaseAuth = FirebaseAuth.getInstance()
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Firebase")

        mEtFriendName = findViewById(R.id.tvPostName)
        mEtDate = findViewById(R.id.etPostDate)
        mEtDateName = findViewById(R.id.etPostName)
        mEtPost = findViewById(R.id.etWritePost)
        mBtnPostRegister = findViewById(R.id.btnPostRegister)
        mBtnPostClose = findViewById(R.id.ivPostClose)
        ivPostData = findViewById(R.id.ivPostCamera)

        friendId = intent.getStringExtra("fId").toString()
        postId = mDatabaseRef.ref.child("UserPosts").child("${mFirebaseAuth!!.currentUser!!.uid}").push().key.toString()

        mDatabaseRef.child("UserFriends").child("${mFirebaseAuth?.currentUser!!.uid}").child(friendId).addValueEventListener(object :
            ValueEventListener {

            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                var friend:FriendData? = snapshot.getValue(FriendData::class.java)
                mEtFriendName.text = friend!!.fName
                // 사진 url 추가 후 load하는 코드 넣을 자리

            }
        })

        ivPostData.setOnClickListener{
            // open the album
            val photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type="image/*"
            startActivityForResult(photoPickerIntent,PICK_IMAGE_FROM_ALBUM)
        }

        mBtnPostRegister.setOnClickListener {
            postAdd()
            finish()
        }

        mBtnPostClose.setOnClickListener {
            super.onBackPressed()
            finish()
        }
    }

    // onActivityResult
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_FROM_ALBUM) {
            if (resultCode == Activity.RESULT_OK) {
                // This is path to the selected image
                photoUri = data?.data
                ivPostData.setImageURI(photoUri)
            } else {
                // Exit the addPhotoActivity if you leave the album without selecting it
            }
        }
    }

    private fun postAdd() {
        var hashMap: HashMap<String, Any> = HashMap()
        var strDate: String = mEtDate.text.toString()
        var strDateName: String = mEtDateName.text.toString()
        var strPost: String = mEtPost.text.toString()
        var strPostId: String = postId
        var strPostUri: String= ""
        var timestamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        var imageFileName = "IMAGE_" + strPostId + "_postImage_by"+friendId+".png"
        var storageRef = storage?.reference?.child("${mFirebaseAuth?.currentUser!!.uid}")?.child(imageFileName)


        hashMap["postId"] = strPostId
        hashMap["post"] = strPost
        hashMap["postDate"] = strDate
        hashMap["postDateName"] = strDateName
        hashMap["postFriendId"] = friendId
        hashMap["heart"] = 1
        hashMap["timestamp"] = timestamp


        // Promise method
        if(photoUri != null) { // 사진 선택했을 때
            storageRef?.putFile(photoUri!!)
                ?.continueWithTask { task: com.google.android.gms.tasks.Task<UploadTask.TaskSnapshot> ->
                    return@continueWithTask storageRef.downloadUrl
                }?.addOnSuccessListener { uri ->
                    strPostUri = uri.toString()
                    hashMap["postPhotoUri"] = strPostUri
                    mDatabaseRef.ref.child("UserPosts").child("${mFirebaseAuth!!.currentUser!!.uid}").child(postId).setValue(hashMap)
                }
        }
        else{ // 사진 선택 안했을 때
            hashMap["postPhotoUri"] = strPostUri
            mDatabaseRef.ref.child("UserPosts").child("${mFirebaseAuth!!.currentUser!!.uid}").child(postId).setValue(hashMap)
        }

        Toast.makeText(this, "게시글 추가 완료", Toast.LENGTH_SHORT).show()

    }


}





