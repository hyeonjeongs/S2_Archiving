package com.cookandroid.s2_archiving


import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_edit_friend.*
import kotlinx.android.synthetic.main.activity_modify_account.*
import kotlinx.android.synthetic.main.add_friend.*
import java.text.SimpleDateFormat
import java.util.*


class ModifyAccount : AppCompatActivity() {
   // 이미지 선택시 상수 값값
   var PICK_IMAGE_FROM_ALBUM = 0

    //파이어베이스에서 인스턴스 가져오기
    private var mFirebaseAuth: FirebaseAuth? = FirebaseAuth.getInstance()
    var storage : FirebaseStorage? = FirebaseStorage.getInstance()
    private var mDatabaseRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("Firebase")

    private lateinit var mTvEmail: TextView
    private lateinit var mEtNickName: EditText
    private lateinit var mEtBeforePwd: EditText
    private lateinit var mEtAfterPwd: EditText
    private lateinit var  btnGAl:Button
    private lateinit var btnModify: Button
    private lateinit var btnEditMyDataback:Button
    private lateinit var ivProfile: ImageView
    private lateinit var etYearSpinner: Spinner
    private lateinit var etMonthSpinner: Spinner
    private lateinit var etDaySpinner: Spinner

    private var photoUri : Uri? = null // 프로필 이미지 uri

    // 정보 저장에 쓸 변수
    private lateinit var strNickName: String
    private lateinit var strAfterPwd: String
    private lateinit var strPhotoUri: String
    private var strBday:String = ""
    private var strYear: String = ""
    private var strMonth: String =""
    private var strDate: String = ""

    private var activity:Activity = this@ModifyAccount


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modify_account)

        //위젯 연결
        mEtNickName = findViewById(R.id.etEditNameMyData)
        mEtBeforePwd = findViewById(R.id.etCurrentPwdMyData)
        mEtAfterPwd = findViewById(R.id.etNewPwdMyData)
        mTvEmail = findViewById(R.id.etEditEmailMyData)
        btnGAl = findViewById(R.id.imageEditMyData)
        btnModify = findViewById(R.id.btnEditEditMyData)
        ivProfile = findViewById(R.id.ivEditImageMydata)
        btnEditMyDataback = findViewById(R.id.btnEditMyDataback)
        etYearSpinner = findViewById(R.id.edit_my_year_spinner)
        etMonthSpinner = findViewById(R.id.edit_my_month_spinner)
        etDaySpinner = findViewById(R.id.edit_my_day_spinner)

        // 사용자의 닉네임, 이메일 출력(이메일은 수정 불가능)
        mDatabaseRef.child("UserAccount").child("${mFirebaseAuth?.currentUser!!.uid}").addValueEventListener(object :
                ValueEventListener {

            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                var user: UserAccount? = snapshot.getValue(UserAccount::class.java)
                // 사용자가 미리 볼 수 있도록 setText
                mTvEmail.text = "${user!!.userEmail}"
                mEtNickName.setText("${user!!.userNickname}")

                // 원래 정보 담아두기
                strNickName = user!!.userNickname
                strAfterPwd = user!!.userPwd
                strPhotoUri = user!!.userPhotoUri
//                strBday = if (user!!.userBirth == null){
//                    "1990년01월01일"
//                } else{
//                    user!!.userBirth
//                }
//                etYearSpinner.setSelection((((strBday).substring(0,4)).toInt())-1990)
//                etMonthSpinner.setSelection((((strBday).substring(5,7)).toInt())-1)
//                etDaySpinner.setSelection((((strBday).substring(8,10)).toInt())-1)

                if(strPhotoUri==""){
                    ivProfile.setImageResource(R.drawable.user)
                }
                else{ // userPhotoUri가 있으면 그 사진 로드하기
                    if(activity.isFinishing) return;
                    else {
                        Glide.with(activity)
                            .load(strPhotoUri)
                            .into(ivProfile)
                    }
                }

            }
        })


        //생년원일 스피너
        var yData = resources.getStringArray(R.array.yearItemList)
        var adapter = ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,yData)
        edit_my_year_spinner.adapter=adapter

        var mData = resources.getStringArray(R.array.monthItemList)
        var madapter = ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,mData)
        edit_my_month_spinner.adapter=madapter

        var dData = resources.getStringArray(R.array.dayItemList)
        var dadapter = ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,dData)
        edit_my_day_spinner.adapter=dadapter

        //생년원일 스피너 아이템 선택했을때
        edit_my_year_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                strYear = ""
                strYear =  edit_my_year_spinner.selectedItem.toString()+"년"
            }

        }

        edit_my_month_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                strMonth = ""
                strMonth = edit_my_month_spinner.selectedItem.toString()+"월"
            }
        }

        edit_my_day_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                strDate = ""
                strDate = edit_my_day_spinner.selectedItem.toString()+"일"
            }
        }

        btnEditMyDataback.setOnClickListener {
            super.onBackPressed()
            finish()
        }


        btnGAl.setOnClickListener{
            // open the album
            val photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type="image/*"
            startActivityForResult(photoPickerIntent,PICK_IMAGE_FROM_ALBUM)
        }

        //정보 수정 버튼
        btnModify.setOnClickListener {

            // 변경된 정보 확인
            if(mEtNickName.text.isNotEmpty()){
                strNickName = mEtNickName.text.toString()
            }

            var comparePassword:String = strAfterPwd// 현재 비밀번호
             if(mEtBeforePwd.text.isNotEmpty()&&mEtAfterPwd.text.isNotEmpty()){ // 비밀번호를 변경하고자 한다면
                if(comparePassword.equals(mEtBeforePwd.text.toString())) {
                    changePassword()
                    strAfterPwd = mEtAfterPwd.text.toString() // 새로운 비밀번호로 업데이트
                }
                else{ // 현재 비밀번호 입력 오류 시
                    Toast.makeText(this@ModifyAccount, "현재 비밀번호를 확인해주세요", Toast.LENGTH_SHORT) // 원래 비밀번호로 update
                }
             }
            strBday = strYear + strMonth +strDate

            modifyAccount()
            finish()
        }

    }

    // onActivityResult
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == PICK_IMAGE_FROM_ALBUM){
            if(resultCode== Activity.RESULT_OK){
                // This is path to the selected image
                photoUri = data?.data
                ivProfile.setImageURI(photoUri)
            }
            else{
                // Exit the addPhotoActivity if you leave the album without selecting it
            }
        }
    }

    // 정보 업데이트
    private fun modifyAccount() {
        Log.e("정보업데이트","되는거니..?")
        // Make filename
        var imageFileName = "IMAGE_" + "${mFirebaseAuth?.currentUser!!.uid}"+ "_profile_.png"
        var storageRef = storage?.reference?.child("${mFirebaseAuth?.currentUser!!.uid}")?.child(imageFileName)
        var hashMap: HashMap<String, Any> = HashMap()

        hashMap.put("userNickname", strNickName)
        hashMap.put("userPwd", strAfterPwd)
        hashMap.put("userBirth", strBday)

        // Promise method
        if(photoUri != null) { // 사진 선택한 경우
            storageRef?.putFile(photoUri!!)
                ?.continueWithTask { task: com.google.android.gms.tasks.Task<UploadTask.TaskSnapshot> ->
                    return@continueWithTask storageRef.downloadUrl
                }?.addOnSuccessListener { uri ->
                    Log.e("이놈 왜 안돼","이놈 왜 안돼")
                    strPhotoUri = uri.toString()
                    hashMap.put("userPhotoUri", strPhotoUri)
                    mDatabaseRef.child("UserAccount")
                        .child("${mFirebaseAuth?.currentUser!!.uid}")
                        .updateChildren(hashMap)
                        .addOnSuccessListener { Log.e("changeinfo", "정보 변경 완료") }

                }
        }
        else{ // 사진 선택 안한 경우
            hashMap.put("userPhotoUri", strPhotoUri)
            mDatabaseRef.child("UserAccount")
                .child("${mFirebaseAuth?.currentUser!!.uid}")
                .updateChildren(hashMap)
                .addOnSuccessListener { Log.e("changeinfo", "정보 변경 완료") }
        }
    }

    // 비밀번호 변경 메소드
    private fun changePassword() {
        val user: FirebaseUser? = mFirebaseAuth!!.currentUser


        user?.updatePassword(mEtAfterPwd.text.toString())?.addOnCompleteListener{ task->
            if(task.isSuccessful){
                Log.e("changepw", "비밀번호 변경 완료")
            }
            else{
                Log.e("changepw", "비밀번호 변경 실패")
            }
        }
    }

}