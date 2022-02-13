package com.cookandroid.s2_archiving

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import java.util.regex.Pattern
;
class ResisterActivity : AppCompatActivity() {

    private lateinit var mFirebaseAuth: FirebaseAuth // 파이어베이스 인증 처리
    private lateinit var mDatabaseRef: DatabaseReference // 실시간 데이터 베이스
    private lateinit var mEtEmail: EditText // 회원 가입 입력 필드(이메일)
    private lateinit var mEtNickname:EditText // 닉네임 입력 필드
    private lateinit var mEtPwd: EditText // 회원 가입 입력 필드(비밀번호)
    private lateinit var mEtPwdCheck: EditText // 비밀번호 확인 필드
    private lateinit var mBtnConfirmID : Button //이메일 중복확인 버튼
    private lateinit var mBtnRegister: Button // 회원 가입 버튼
    private var test: Int = 0 // 이메일 중복 검사 버튼을 눌렀는지 확인

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resister)

        mFirebaseAuth = FirebaseAuth.getInstance()

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Firebase").child("UserAccount")
        mEtEmail = findViewById<EditText>(R.id.etEmail)
        mBtnConfirmID = findViewById<Button>(R.id.btnConfirmID)
        mEtNickname=findViewById<EditText>(R.id.etNickname)
        mEtPwd = findViewById<EditText>(R.id.etPwd)
        mEtPwdCheck = findViewById<EditText>(R.id.etPwdCheck)
        mBtnRegister = findViewById<Button>(R.id.btnRegister)

        var pattern : Pattern = android.util.Patterns.EMAIL_ADDRESS

        //이메일 중복 확인 버튼

        mBtnConfirmID.setOnClickListener {
            mDatabaseRef.orderByChild("userEmail").equalTo("${mEtEmail.text.toString()}")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {

                    }
                    override fun onDataChange(snapshot: DataSnapshot) {
                        var value = snapshot.getValue()
                        if(!pattern.matcher(mEtEmail.text.toString()).matches()){
                            Toast.makeText(this@ResisterActivity,"이메일 형식으로 입력하세요",Toast.LENGTH_SHORT).show()
                        }else if(value != null){
                            Toast.makeText(this@ResisterActivity,"이미 가입되어 있는 아이디입니다",Toast.LENGTH_SHORT).show()
                        }else{
                            //가입 가능한 아이디이면 이메일 입력 창 비활성화 후 나머지 창 활성화
                            test = 1
                            Toast.makeText(this@ResisterActivity,"사용 가능한 아이디입니다",Toast.LENGTH_SHORT).show()

                        }
                    }
                })
        }
        mBtnRegister.setOnClickListener(View.OnClickListener {
            // 회원가입 처리 시작
            var strEmail: String = mEtEmail.getText().toString()
            var strNickname: String = mEtNickname.getText().toString()
            var strPhone: String =""
            var strPwd: String = mEtPwd.getText().toString()
            var strPwdCheck: String = mEtPwdCheck.getText().toString()



            if(strEmail.equals("")||strNickname.equals("")||strPwd.equals("")||strPwdCheck.equals("")){
                Toast.makeText(this, "모든 항목을 입력해주세요", Toast.LENGTH_SHORT).show()
            }
            else if(!strPwd.equals(strPwdCheck)){
                Toast.makeText(this, "비밀번호를 확인해주세요", Toast.LENGTH_SHORT).show()
            }
            else if(test==0){
                Toast.makeText(this, "이메일 중복검사를 완료해주세요", Toast.LENGTH_SHORT).show()
            }
            else {

                mFirebaseAuth?.createUserWithEmailAndPassword(strEmail, strPwd)
                    ?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            var firebaseUser: FirebaseUser? = mFirebaseAuth.currentUser
                            var account = UserAccount()
                            account.userId = firebaseUser?.uid.toString()
                            account.userEmail = firebaseUser?.email.toString()
                            account.userNickname = strNickname
                            //account.userPhone = strPhone
                            account.userPwd = strPwd

                            // setValue : database에 insert (삽입) 행위
                            mDatabaseRef.child(firebaseUser?.uid.toString())
                                .setValue(account)

                            Toast.makeText(this, "회원가입에 성공하셨습니다", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, LoginActivity::class.java)
                            startActivity(intent)
                            finish() // 현재 액티비티 파괴

                        } else {
                            Toast.makeText(this, "회원가입에 실패하셨습니다", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        })

    }

}