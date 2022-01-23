package com.cookandroid.s2_archiving

import android.Manifest
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask

//import com.example.recyclerviewkt.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.add_friend.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class FriendAdd : AppCompatActivity() {


    //fork test
    //변경된거 확인해보기


    //
    var photoUri: Uri? = null

//    private var mBinding: ActivityMainBinding? = null
//    private val binding get() = mBinding!!

    lateinit var cameraPermission: ActivityResultLauncher<String>
    lateinit var storagePermission :ActivityResultLauncher<String>

    lateinit var cameraLauncher:ActivityResultLauncher<Uri>
    lateinit var galleryLauncher:ActivityResultLauncher<String>

    lateinit var btnAddFriend: TextView
    lateinit var etName: EditText
    lateinit var etPhone: EditText
    lateinit var etRel: EditText
    lateinit var etAdd: EditText

    var imgUrl : String = ""

    private var mFirebaseAuth: FirebaseAuth? = null //파이어베이스 인증
    private lateinit var mDatabaseRef: DatabaseReference //실시간 데이터베이스
    private lateinit var fbStorage: FirebaseStorage
    private lateinit var storageRef: StorageReference
    private var GALLEY_CODE : Int = 10

    var timestamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_friend)

        btnAddFriend = findViewById(R.id.btnAddFriend)
        etName = findViewById(R.id.etName)
        etPhone = findViewById(R.id.etPhone)
        etRel = findViewById(R.id.etRel)
        etAdd = findViewById(R.id.etAdd)


        //파이어베이스 계정, 리얼타임 데이터베이스
        mFirebaseAuth = FirebaseAuth.getInstance()
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Firebase")
        fbStorage = FirebaseStorage.getInstance()

        mDatabaseRef.child("UserAccount").child("${mFirebaseAuth!!.currentUser!!.uid}")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        //파이어베이스의 데이터를 가져옴
                        var user: UserAccount? = snapshot.getValue(UserAccount::class.java)
                        Log.d("택", "${user!!.userEmail.toString()}")
                    }
                    override fun onCancelled(error: DatabaseError) {
                        Log.d("Tag", "Failed")
                    }
                })

        btnAddFriend.setOnClickListener{
            try {
                var storageReference : StorageReference = fbStorage.getReference()

                var file : Uri = Uri.fromFile(File(imgUrl))
                var riversRef : StorageReference = storageReference.child("images/"+file.lastPathSegment)
                var uploadTask : UploadTask = riversRef.putFile(file)

                var urlTask : Task<Uri> = uploadTask.continueWithTask(Continuation {
                    if(!it.isSuccessful){
                        it.exception
                    }
                    riversRef.downloadUrl
                }).addOnCompleteListener {
                    if(it.isSuccessful)
                    {
                        var downloadUrl : Uri? = it.result

                        val hashMap : HashMap<String, String> = HashMap()

                        var strName: String = etName.text.toString()
                        var strPhone = etPhone.text.toString()
                        //var strBday: String = select_spinner!!.getSelectedItem().toString()
                        var strRelationship: String = etRel.text.toString()
                        var strAdd: String = etAdd.text.toString()

                        hashMap.put("imgUrl", downloadUrl.toString())
                        hashMap.put("uid", mFirebaseAuth!!.currentUser!!.uid)
                        hashMap.put("fName", strName)
                        hashMap.put("fPhone", strPhone)
                        //hashMap.put("fBday", strBday)
                        hashMap.put("fRel", strRelationship)
                        hashMap.put("fAdd", strAdd)
                        hashMap.put("timstamp", timestamp)


                        mDatabaseRef.ref.child("UserFriends").child("${mFirebaseAuth!!.currentUser!!.uid}").push().setValue(hashMap)
                            .addOnCompleteListener {
                                if(it.isSuccessful){
                                    Toast.makeText(this, "업로드", Toast.LENGTH_SHORT).show()
                                }
                            }

                        Toast.makeText(this, "친구 추가 완료", Toast.LENGTH_SHORT).show()
                        var intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }.addOnFailureListener {

                    val hashMap : HashMap<String, String> = HashMap()

                    var strName: String = etName.text.toString()
                    var strPhone = etPhone.text.toString()
                    //var strBday: String = select_spinner!!.getSelectedItem().toString()
                    var strRelationship: String = etRel.text.toString()
                    var strAdd: String = etAdd.text.toString()


                    hashMap.put("uid", mFirebaseAuth!!.currentUser!!.uid)
                    hashMap.put("fName", strName)
                    hashMap.put("fPhone", strPhone)
                    //hashMap.put("fBday", strBday)
                    hashMap.put("fRel", strRelationship)
                    hashMap.put("fAdd", strAdd)
                    hashMap.put("timstamp", timestamp)
                    mDatabaseRef.ref.child("UserFriends").child("${mFirebaseAuth!!.currentUser!!.uid}").push().setValue(hashMap)

                    Toast.makeText(this, "등록완료", Toast.LENGTH_SHORT).show()
                    var intent = Intent(this, MainActivity::class.java)
                    //intent.putExtra("SELECTED_ITEM", selectedItem)
                    startActivity(intent)
                    finish()

                }
            }catch (e : NullPointerException){
                Toast.makeText(this, "이미지 선택 안함", Toast.LENGTH_SHORT).show();
            }
        }

//        mBinding = ActivityMainBinding.inflate(layoutInflater)
//        setContentView(binding.root)


//        //recyclerview 데이터
//        val profileList = arrayListOf(
//            Profiles(R.drawable.woman, "조윤진", R.drawable.star_empty),
//            Profiles(R.drawable.man, "김씨", R.drawable.star_full),
//            Profiles(R.drawable.woman, "이땡땡", R.drawable.star_full),
//            Profiles(R.drawable.woman, "최씨", R.drawable.star_empty),
//            Profiles(R.drawable.man, "박땡땡", R.drawable.star_full),
//            Profiles(R.drawable.woman, "신땡땡", R.drawable.star_empty),
//            Profiles(R.drawable.man, "윤씨", R.drawable.star_empty),
//            Profiles(R.drawable.woman, "권씨", R.drawable.star_empty),
//            Profiles(R.drawable.woman, "강씨", R.drawable.star_empty),
//            Profiles(R.drawable.man, "서씨", R.drawable.star_full)
//
//        )
//
//        rvProfile.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
//        rvProfile.setHasFixedSize(true)
//        rvProfile.adapter = ProfileAdapter(profileList)
//        plusButton.setOnClickListener {
//            Toast.makeText(baseContext,"친구추가화면", Toast.LENGTH_SHORT).show()
//        }





        //생년원일 스피너
        year_spinner.adapter = ArrayAdapter.createFromResource(this, R.array.yearItemList, android.R.layout.simple_spinner_item)
        month_spinner.adapter = ArrayAdapter.createFromResource(this, R.array.monthItemList, android.R.layout.simple_spinner_item)
        day_spinner.adapter = ArrayAdapter.createFromResource(this, R.array.dayItemList, android.R.layout.simple_spinner_item)


        storagePermission=registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ){isGranted ->
            if(isGranted){
                setViews()
            } else{
                Toast.makeText(baseContext, "외부 저장소 권한을 승인해야 앱을 사용할 수 있습니다.",Toast.LENGTH_SHORT).show()
                finish()
            }
        }


        cameraPermission=registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ){isGranted ->
            if(isGranted){
                //openCamera()
            } else{
                Toast.makeText(baseContext, "카메라 권한을 승인해야 앱을 사용할 수 있습니다.",Toast.LENGTH_SHORT).show()
                finish()
            }
        }



        galleryLauncher = registerForActivityResult(ActivityResultContracts.
        GetContent()){uri->
            profileImage.setImageURI(uri)
        }

        storagePermission.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    fun setViews(){
        profileImage.setOnClickListener{
            openGallery()
        }
        tvGal.setOnClickListener{
            openGallery()
        }
    }

    fun openGallery(){
        galleryLauncher.launch("image/*")
    }



}



