package com.cookandroid.s2_archiving

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_edit_friend.*

//import com.example.recyclerviewkt.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.add_friend.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class EditFriendActivity : AppCompatActivity() {

    // 상수 선언 및 uri 변수
    var PICK_IMAGE_FROM_ALBUM = 0
    var photoUri: Uri? = null

    // xml
    private lateinit var etName: EditText
    private lateinit var etPhone: EditText
    private lateinit var etRel: EditText
    private lateinit var etAdd: EditText
    private lateinit var btnGal: Button
    private lateinit var ivProfile:ImageView

    // firebase
    private var mFirebaseAuth: FirebaseAuth? = null //파이어베이스 인증
    private lateinit var mDatabaseRef: DatabaseReference //실시간 데이터베이스
    var storage : FirebaseStorage? = FirebaseStorage.getInstance()

    // 정보 저장에 쓸 변수
    private lateinit var strName:String
    private lateinit var strPhone:String
    private lateinit var strBday:String
    private lateinit var strRel:String
    private lateinit var strAdd:String
    private lateinit var strUri:String

    // Intent로 넘어오는 정보
    private lateinit var friendId:String

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_friend)


        //파이어베이스 계정, 리얼타임 데이터베이스
        mFirebaseAuth = FirebaseAuth.getInstance()
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Firebase")

        friendId = intent.getStringExtra("fId").toString()

        etName = findViewById(R.id.etEditName)
        etPhone = findViewById(R.id.etEditPhone)
        etRel = findViewById(R.id.etEditRel)
        etAdd = findViewById(R.id.etEditAdd)
        ivProfile = findViewById(R.id.ivEditProfileImage)

        mDatabaseRef.child("UserFriends").child("${mFirebaseAuth?.currentUser!!.uid}").child(friendId!!)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    // 사용자가 미리 볼 수 있도록 setText
                    var friend: FriendData? = snapshot.getValue(FriendData::class.java)
                    etName.setText(friend!!.fName)
                    etPhone.setText(friend!!.fPhone)
                    etRel.setText(friend!!.fBday)
                    etAdd.setText(friend!!.fAdd)

                    // 원래 정보 가져오기
                    strName = friend.fName
                    strPhone = friend.fPhone
                    strRel = friend.fRel
                    strAdd = friend.fAdd
                    strUri = friend.fImgUri

                    Log.d("택", "${friend?.fName}")
                }
                override fun onCancelled(error: DatabaseError) {
                    Log.d("Tag", "Failed")
                }
            })

        //생년원일 스피너
        var yData = resources.getStringArray(R.array.yearItemList)
        var adapter = ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,yData)
        edit_year_spinner.adapter=adapter

        var mData = resources.getStringArray(R.array.monthItemList)
        var madapter = ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,mData)
        edit_month_spinner.adapter=madapter

        var dData = resources.getStringArray(R.array.dayItemList)
        var dadapter = ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,dData)
        edit_day_spinner.adapter=dadapter

        //생년원일 스피너 아이템 선택했을때
        edit_year_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                strBday = strBday + edit_year_spinner.selectedItem.toString()+"년"
            }
        }

        edit_month_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                strBday = strBday + edit_month_spinner.selectedItem.toString()+"월"
            }
        }

        edit_day_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                strBday = strBday + edit_day_spinner.selectedItem.toString()+"일"
            }
        }

        if(strUri.isNotBlank()){
            Glide.with(this)
                .load(strUri)
                .into(ivProfile)
        }

        btnGal.setOnClickListener{
            // open the album
            val photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type="image/*"
            startActivityForResult(photoPickerIntent,PICK_IMAGE_FROM_ALBUM)
        }


        btnEditAddFriend.setOnClickListener{
            // 변경된 정보 확인
            if(etName.text.isNotEmpty()){
                strName = etName.text.toString()
            }
            if(etPhone.text.isNotEmpty()){
                strPhone = etPhone.text.toString()
            }
            if(etRel.text.isNotEmpty()){
                strRel = etRel.text.toString()
            }
            if(etAdd.text.isNotEmpty()){
                strAdd = etAdd.text.toString()
            }
            editFriend()
        }

    }

    private fun editFriend() {
        // Make filename
        var timestamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        var imageFileName = "IMAGE_" + timestamp + "_.png"
        var storageRef = storage?.reference?.child("images")?.child(imageFileName)

        // Promise method
        if(photoUri != null) {
            storageRef?.putFile(photoUri!!)
                ?.continueWithTask { task: com.google.android.gms.tasks.Task<UploadTask.TaskSnapshot> ->
                    return@continueWithTask storageRef.downloadUrl
                }?.addOnSuccessListener { uri ->
                    strUri = uri.toString()
                    setResult(Activity.RESULT_OK)
                    finish()
                }
        }

        // hashmap data
        val hashMap: HashMap<String, Any> = HashMap()
        hashMap.put("fPhone", strPhone)
        hashMap.put("fBday", strBday)
        hashMap.put("fRel", strRel)
        hashMap.put("fAdd", strAdd)
        hashMap.put("fImgUri", strUri!!)

        mDatabaseRef.child("UserFriends").child("${mFirebaseAuth?.currentUser!!.uid}").child(friendId!!)
            .updateChildren(hashMap)
            .addOnSuccessListener { Log.e("changeinfo", "정보 변경 완료") }
            .addOnFailureListener { Log.e("changepw", "정보 변경 실패") }
    }
}


