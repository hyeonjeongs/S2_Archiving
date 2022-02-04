package com.cookandroid.s2_archiving

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cookandroid.s2_archiving.fragment.HomeFragment
import com.cookandroid.s2_archiving.fragment.LikeFragment
import com.cookandroid.s2_archiving.fragment.UserFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {


    //프레그먼트를 위한 변수들
    private lateinit var homeFragment: HomeFragment
    private lateinit var likeFragment: LikeFragment
    private lateinit var userFragment: UserFragment

    private lateinit  var mainActivity:MainActivity




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainActivity = this@MainActivity

        bottom_navi.setOnNavigationItemSelectedListener(onBottomNaviItemSelectedListner)
        homeFragment = HomeFragment.newInstance()
        supportFragmentManager.beginTransaction().add(R.id.fragment_frame, homeFragment).commit()


    }

    //바텀네비게이션 아이템 클릭 리스너 설정
    private val onBottomNaviItemSelectedListner =  BottomNavigationView.OnNavigationItemSelectedListener {
        when(it.itemId){
            R.id.home -> {
                homeFragment = HomeFragment.newInstance()
                supportFragmentManager.beginTransaction().replace(R.id.fragment_frame, homeFragment).commit()
            }
            R.id.like -> {
                likeFragment = LikeFragment.newInstance()
                supportFragmentManager.beginTransaction().replace(R.id.fragment_frame, likeFragment).commit()
            }
            R.id.user -> {
                userFragment = UserFragment.newInstance()
                supportFragmentManager.beginTransaction().replace(R.id.fragment_frame, userFragment).commit()

            }
        }
        true
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
