package com.cookandroid.s2_archiving

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.instaprac.FriendAdapter
import com.example.instaprac.FriendModel
import kotlinx.android.synthetic.main.category_search.*

class SearchCategory: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.category_search)


        val categorylist = arrayListOf( //리스트형태의 객체들을 넣어줌
                CategoryModel(210324,"생일"),
                CategoryModel(201225,"크리스마스"),
                CategoryModel(220101,"새해"),
                CategoryModel(200324,"생일"),
                CategoryModel(2021,"기념일"),
                CategoryModel(2019,"집들이"),
                CategoryModel(201225,"크리스마스"),
                CategoryModel(220101,"새해")
        )

        val rvSearchCategory = findViewById<RecyclerView>(R.id.rvSearchCategory)
        rvSearchCategory.layoutManager = GridLayoutManager(this,2) //한 행에 두 열씩
        rvSearchCategory.setHasFixedSize(true) //리사이클러뷰 성능 개선

        rvSearchCategory.adapter = SearchCategoryAdapter(categorylist) //위에서 선언한 리스트를 어뎁터에 변수인 profileList로 전달



        btnCategorySearchBack.setOnClickListener(View.OnClickListener {
            super.onBackPressed()
            val intent = Intent(this, MainActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish()
        })
    }
}