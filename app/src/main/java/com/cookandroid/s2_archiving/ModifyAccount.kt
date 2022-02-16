package com.cookandroid.s2_archiving


import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_modify_account.*
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

    private var photoUri : Uri? = null // 프로필 이미지 uri

    // 정보 저장에 쓸 변수
    private lateinit var strNickName: String
    private lateinit var strAfterPwd: String
    private lateinit var strPhotoUri: String

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