package com.cookandroid.s2_archiving

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_edit_friend.*

//import com.example.recyclerviewkt.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.add_friend.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class FriendAdd : AppCompatActivity() {


    var photoUri: Uri? = null
    // 이미지 선택시 상수 값값
    var PICK_IMAGE_FROM_ALBUM = 0

    // xml 연결
    lateinit var btnAddFriend: Button
    lateinit var btnGal:Button
    lateinit var etName: EditText
    lateinit var etPhone: EditText
    lateinit var etRel: EditText
    lateinit var etAdd: EditText
    lateinit var ivProfileImage:ImageView
    lateinit var year_spinner : Spinner
    lateinit var month_spinner : Spinner
    lateinit var day_spinner : Spinner

    //파이어베이스에서 인스턴스 가져오기
    private var mFirebaseAuth: FirebaseAuth? = FirebaseAuth.getInstance()
    var storage : FirebaseStorage? = FirebaseStorage.getInstance()
    private var mDatabaseRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("Firebase")

    // 정보 저장에 쓸 변수
    var strName: String =""
    var strPhone: String =""
    var strBday: String =""
    var strYear: String = ""
    var strMonth: String =""
    var strDate: String = ""
    var strRelationship: String =""
    var strAdd: String =""
    var strFid:String =""
    var strUri:String =""

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_friend)

        btnAddFriend = findViewById(R.id.btnAddFriend)
        btnGal = findViewById(R.id.btnEditGal)
        etName = findViewById(R.id.etName)
        etPhone = findViewById(R.id.etPhone)
        etRel = findViewById(R.id.etRel)
        etAdd = findViewById(R.id.etAdd)
        ivProfileImage = findViewById(R.id.ivProfileImage)

        year_spinner = findViewById<Spinner>(R.id.year_spinner)
        month_spinner = findViewById<Spinner>(R.id.month_spinner)
        day_spinner = findViewById<Spinner>(R.id.day_spinner)

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
                strYear = ""
                strYear =  year_spinner.selectedItem.toString()+"년"
            }

        }

        month_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                strMonth = ""
                strMonth = month_spinner.selectedItem.toString()+"월"
            }
        }

        day_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                strDate = ""
                strDate = day_spinner.selectedItem.toString()+"일"
            }
        }

        btnGal.setOnClickListener{
            // open the album
            val photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type="image/*"
            startActivityForResult(photoPickerIntent,PICK_IMAGE_FROM_ALBUM)
        }

        btnAddFriend.setOnClickListener{
            // data 가져오기
            strName = etName.text.toString()
            strPhone = etPhone.text.toString()
            strRelationship = etRel.text.toString()
            strBday = strYear + strMonth +strDate
            strAdd = etAdd.text.toString()
            strFid = mDatabaseRef.ref.child("UserFriends").child("${mFirebaseAuth!!.currentUser!!.uid}").push().key.toString()
            strUri = ""
            if(strName.isNotBlank()){
                friendAdd()
                finish()
            }
            else{
                Toast.makeText(this, "친구 이름은 필수 기입 항목입니다", Toast.LENGTH_SHORT).show()
            }
        }

        btnfriendback.setOnClickListener{ // 백
            finish() // 그냥 추가하는 화면만 finish 시키기
        }

    }

    // onActivityResult
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == PICK_IMAGE_FROM_ALBUM){
            if(resultCode== Activity.RESULT_OK){
                // This is path to the selected image
                photoUri = data?.data
                ivProfileImage.setImageURI(photoUri)
            }
            else{
                // Exit the addPhotoActivity if you leave the album without selecting it
            }
        }
    }

    fun friendAdd(){
        // Make filename
        var timestamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        var imageFileName = "IMAGE_" + strFid + "_friendProfile_.png"
        var storageRef = storage?.reference?.child("${mFirebaseAuth?.currentUser!!.uid}")?.child(imageFileName)

        // hashmap data
        var hashMap: HashMap<String, Any> = HashMap()
        hashMap.put("fName",strName)
        hashMap.put("fPhone", strPhone)
        hashMap.put("fId", strFid!!)
        hashMap.put("fBday", strBday)
        hashMap.put("fRel", strRelationship)
        hashMap.put("fAdd", strAdd)
        hashMap.put("fStar", 1)
        hashMap.put("timstamp", timestamp)


        // Promise method
        if(photoUri != null) { // 사진 선택했을 때
            storageRef?.putFile(photoUri!!)
                ?.continueWithTask { task: com.google.android.gms.tasks.Task<UploadTask.TaskSnapshot> ->
                    return@continueWithTask storageRef.downloadUrl
                }?.addOnSuccessListener { uri ->
                    strUri = uri.toString()
                    hashMap.put("fImgUri", strUri)
                    mDatabaseRef.ref.child("UserFriends").child("${mFirebaseAuth!!.currentUser!!.uid}").child(strFid!!).setValue(hashMap)
                }
        }
        else{ // 사진 선택 안했을 때
            hashMap.put("fImgUri",strUri)
            mDatabaseRef.ref.child("UserFriends").child("${mFirebaseAuth!!.currentUser!!.uid}").child(strFid!!).setValue(hashMap)
        }

    }
}