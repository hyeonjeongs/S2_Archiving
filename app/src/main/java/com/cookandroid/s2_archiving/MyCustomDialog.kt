package com.cookandroid.s2_archiving

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Window
import androidx.core.content.ContextCompat.startActivity
import kotlinx.android.synthetic.main.custom_dialog.*


//7번 게시글 화면 - 삭제 수정
class MyCustomDialog(context: Context): Dialog(context) {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.custom_dialog)

        // 배경 투명하게
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        btnCustomEdit.setOnClickListener{ //게시글 수정(=쓰기)으로 이동 (PostActivity)
            //val intent = Intent(context,PostActivity::class.java)

        }
        btnCustomRemove.setOnClickListener{ //게시글 삭제하기

        }
    }
}