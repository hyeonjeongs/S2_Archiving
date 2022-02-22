package com.cookandroid.s2_archiving

import android.app.Activity
import android.app.PendingIntent.getActivity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import java.util.*
import kotlin.collections.HashMap

class EditPostActivity : AppCompatActivity() {
    // 이미지 선택시 상수 값값
    var PICK_IMAGE_FROM_ALBUM = 0
    var photoUri: Uri? = null

    private var postId: String = ""
    private lateinit var friendId:String

    private lateinit var mFirebaseAuth: FirebaseAuth // 파이어베이스 인증 처리
    private lateinit var mDatabaseRef: DatabaseReference // 실시간 데이터 베이스
    private var storage : FirebaseStorage? = FirebaseStorage.getInstance()

    private lateinit var mEtEditFriendName: TextView
    private lateinit var mEtEditDate: EditText // 날짜
    private lateinit var mEtEditDateName: EditText // 기념일 이름
    private lateinit var mEtEditPost: EditText // 글 내용
    private lateinit var mBtnEditPostRegister: Button // 게시글 업로드 버튼
    private lateinit var mBtnEditPostClose: ImageView
    private lateinit var ivEditPostData: ImageView

    // string
    private lateinit var strDate: String
    private lateinit var strDateName:String
    private lateinit var strPost:String
    private lateinit var strUri:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_post)

        //파이어베이스 계정, 리얼타임 데이터베이스
        mFirebaseAuth = FirebaseAuth.getInstance()
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Firebase")

        // xml
        mEtEditFriendName = findViewById(R.id.tvEditPostName)
        mEtEditDate = findViewById(R.id.etEditPostDate)
        mEtEditDateName = findViewById(R.id.etEditPostName)
        mEtEditPost = findViewById(R.id.etEditWritePost)
        mBtnEditPostRegister = findViewById(R.id.btnEditPostRegister)
        mBtnEditPostClose = findViewById(R.id.ivEditPostClose)
        ivEditPostData = findViewById(R.id.ivEditPostCamera)

        friendId = intent.getStringExtra("friend_id").toString()
        postId = intent.getStringExtra("post_id").toString()

        mDatabaseRef.child("UserFriends").child("${mFirebaseAuth?.currentUser!!.uid}").child(friendId).addValueEventListener(object :
            ValueEventListener {

            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                var friend:FriendData? = snapshot.getValue(FriendData::class.java)
                    mEtEditFriendName.text = friend!!.fName
            }
        })

        mDatabaseRef.child("UserPosts").child("${mFirebaseAuth?.currentUser!!.uid}").child(postId).addValueEventListener(object :
            ValueEventListener {

            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                var post:PostData? = snapshot.getValue(PostData::class.java)
                mEtEditDate.setText(post!!.postDate)
                mEtEditDateName.setText(post!!.postDateName)
                mEtEditPost.setText(post!!.post)
                if (post!!.postPhotoUri == "") {
                    ivEditPostData.setImageResource(R.drawable.man)
                } else { // userPhotoUri가 있으면 그 사진 로드하기
                    var GlideRequestManager = Glide.with(applicationContext)
                    GlideRequestManager
                        .load(post!!.postPhotoUri)
                        .into(ivEditPostData)
                }

                // 원래 정보 담아두기
                strDate = post!!.postDate
                strDateName = post!!.postDateName
                strPost = post!!.post
                strUri = post!!.postPhotoUri

            }

        })

        ivEditPostData.setOnClickListener{
            // open the album
            val photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type="image/*"
            startActivityForResult(photoPickerIntent,PICK_IMAGE_FROM_ALBUM)
        }

        mBtnEditPostRegister.setOnClickListener {

            // 입력 값이 있을 경우
            if(mEtEditDate.text.isNotBlank()){
                strDate = mEtEditDate.text.toString()
            }
            if(mEtEditDateName.text.isNotBlank()){
                strDateName = mEtEditDateName.text.toString()
            }
            if(mEtEditPost.text.isNotBlank()){
                strPost = mEtEditPost.text.toString()
            }

            editPost()
            finish()
        }

        mBtnEditPostClose.setOnClickListener {
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
                ivEditPostData.setImageURI(photoUri)
            } else {
                // Exit the addPhotoActivity if you leave the album without selecting it
            }
        }
    }

    private fun editPost() {
        var hashMap: HashMap<String, Any> = HashMap()

        var imageFileName = "IMAGE_" + postId + "_postImage_by"+friendId+".png"
        var storageRef = storage?.reference?.child("${mFirebaseAuth?.currentUser!!.uid}")?.child(imageFileName)

        hashMap["post"] = strPost
        hashMap["postDate"] = strDate
        hashMap["postDateName"] = strDateName

        // Promise method
        if(photoUri != null) { // 사진 선택했을 때
            storageRef?.putFile(photoUri!!)
                ?.continueWithTask { task: com.google.android.gms.tasks.Task<UploadTask.TaskSnapshot> ->
                    return@continueWithTask storageRef.downloadUrl
                }?.addOnSuccessListener { uri ->
                    strUri = uri.toString()
                    hashMap["postPhotoUri"] = strUri
                    mDatabaseRef.ref.child("UserPosts").child("${mFirebaseAuth!!.currentUser!!.uid}").child(postId).updateChildren(hashMap)
                }
        }
        else{ // 사진 선택 안했을 때
            hashMap["postPhotoUri"] = strUri
            mDatabaseRef.ref.child("UserPosts").child("${mFirebaseAuth!!.currentUser!!.uid}").child(postId).updateChildren(hashMap)
        }

        Toast.makeText(this, "게시글 수정 완료", Toast.LENGTH_SHORT).show()

    }
}