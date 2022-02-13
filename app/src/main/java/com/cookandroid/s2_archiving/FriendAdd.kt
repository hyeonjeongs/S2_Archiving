package com.cookandroid.s2_archiving

import android.Manifest
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.cookandroid.s2_archiving.fragment.HomeFragment
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_edit_friend.*

//import com.example.recyclerviewkt.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.add_friend.*
import kotlinx.android.synthetic.main.add_friend.tvEditGal
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class FriendAdd : AppCompatActivity() {

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

        var year_spinner = findViewById<Spinner>(R.id.year_spinner)
        var month_spinner = findViewById<Spinner>(R.id.month_spinner)
        var day_spinner = findViewById<Spinner>(R.id.day_spinner)
        var birthDay: String = ""

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

        //생년원일 스피너
        var yData = resources.getStringArray(R.array.yearItemList)
        var adapter = ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,yData)
        year_spinner.adapter=adapter

        var mData = resources.getStringArray(R.array.monthItemList)
        var madapter = ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,mData)
        month_spinner.adapter=madapter

        var dData = resources.getStringArray(R.array.dayItemList)
        var dadapter = ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,dData)
        day_spinner.adapter=dadapter

        //생년원일 스피너 아이템 선택했을때
        year_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                birthDay = birthDay + year_spinner.selectedItem.toString()+"년"
             }
            
        }

        month_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                birthDay = birthDay + month_spinner.selectedItem.toString()+"월"
            }
        }

        day_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                birthDay = birthDay + day_spinner.selectedItem.toString()+"일"
            }
        }


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
            ivProfileImage.setImageURI(uri)
        }

        storagePermission.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)

        btnAddFriend.setOnClickListener{
            try {
                var storageReference : StorageReference = fbStorage.getReference()

                var file : Uri = Uri.fromFile(File(imgUrl))
                var riversRef : StorageReference = storageReference.child("images/"+file.lastPathSegment)
                var uploadTask : UploadTask = riversRef.putFile(file)
                val fid:String? = mDatabaseRef.ref.child("UserFriends").child("${mFirebaseAuth!!.currentUser!!.uid}").push().key

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
                        var strBday: String = birthDay
                        var strRelationship: String = etRel.text.toString()
                        var strAdd: String = etAdd.text.toString()
                        
                        hashMap.put("fId", mFirebaseAuth!!.currentUser!!.uid)
                        hashMap.put("fName", strName)
                        hashMap.put("fImgurl", downloadUrl.toString())
                        hashMap.put("fPhone", strPhone)
                        hashMap.put("fBday", strBday)
                        hashMap.put("fRel", strRelationship)
                        hashMap.put("fAdd", strAdd)
                        hashMap.put("fStar", "0")
                        hashMap.put("timstamp", timestamp)



                        mDatabaseRef.ref.child("UserFriends").child("${mFirebaseAuth!!.currentUser!!.uid}").child("${fid}").setValue(hashMap)
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
                    var strBday: String = birthDay
                    var strRelationship: String = etRel.text.toString()
                    var strAdd: String = etAdd.text.toString()
                    var id: String? = fid


                    if (id != null) {
                        hashMap.put("fId", id )
                    }
                    hashMap.put("fName", strName)
                    hashMap.put("fPhone", strPhone)
                    hashMap.put("fBday", strBday)
                    hashMap.put("fImgurl", "")
                    hashMap.put("fRel", strRelationship)
                    hashMap.put("fAdd", strAdd)
                    hashMap.put("fStar", "0")
                    hashMap.put("timstamp", timestamp)
                    mDatabaseRef.ref.child("UserFriends").child("${mFirebaseAuth!!.currentUser!!.uid}").child("${fid}").setValue(hashMap)

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


        //뒤로가기 버튼 클릭 시 홈으로 이동
        /*btnfriendback.setOnClickListener{
            val intent=Intent(this, HomeFragment::class.java)
            startActivity(intent)
            finish()
        }*/
    }

    fun setViews(){
        ivProfileImage.setOnClickListener{
            openGallery()
        }
        tvEditGal.setOnClickListener{
            openGallery()
        }
    }

    fun openGallery(){
        galleryLauncher.launch("image/*")
    }



}