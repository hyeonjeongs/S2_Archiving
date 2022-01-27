package com.cookandroid.s2_archiving

import android.Manifest
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.RecyclerView
import com.cookandroid.s2_archiving.fragment.HomeFragment
import com.cookandroid.s2_archiving.fragment.LikeFragment
import com.cookandroid.s2_archiving.fragment.UserFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.add_friend.*

class MainActivity : AppCompatActivity() {


    //프레그먼트를 위한 변수들
    private lateinit var homeFragment: HomeFragment
    private lateinit var likeFragment: LikeFragment
    private lateinit var userFragment: UserFragment




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottom_navi.setOnNavigationItemSelectedListener(onBottomNaviItemSelectedListner)
        homeFragment = HomeFragment.newInstance()
        supportFragmentManager.beginTransaction().add(R.id.fragment_frame,homeFragment).commit()


    }

    //바텀네비게이션 아이템 클릭 리스너 설정
    private val onBottomNaviItemSelectedListner =  BottomNavigationView.OnNavigationItemSelectedListener {
        when(it.itemId){
            R.id.home -> {
                homeFragment = HomeFragment.newInstance()
                supportFragmentManager.beginTransaction().replace(R.id.fragment_frame,homeFragment).commit()
            }
            R.id.like -> {
                likeFragment = LikeFragment.newInstance()
                supportFragmentManager.beginTransaction().replace(R.id.fragment_frame,likeFragment).commit()
            }
            R.id.user -> {
                userFragment = UserFragment.newInstance()
                supportFragmentManager.beginTransaction().replace(R.id.fragment_frame,userFragment).commit()

            }
        }
        true
    }
}