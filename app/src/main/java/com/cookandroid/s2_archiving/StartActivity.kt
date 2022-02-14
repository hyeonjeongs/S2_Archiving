package com.cookandroid.s2_archiving

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity

/*앱 처음 실행 화면*/
class StartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        var intent = Intent(this, LoginActivity::class.java)
        //2초 뒤 MainActivity로 가기
        Handler().postDelayed({ startActivity(intent) }, 2000L)

    }
}