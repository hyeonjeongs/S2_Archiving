package com.cookandroid.s2_archiving

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.cookandroid.s2_archiving.fragment.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {


    //프레그먼트를 위한 변수들
    private lateinit var homeFragment: HomeFragment
    private lateinit var likeFragment: LikeFragment
    private lateinit var userFragment: UserFragment
    private lateinit var friendpageFragment : FriendpageFragment
    private lateinit var mainActivity: MainActivity
    private lateinit var viewpageFragment : ViewpageFragment




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
                supportFragmentManager.beginTransaction().replace(R.id.fragment_frame, homeFragment,"home").commit()
            }
            R.id.like -> {
                likeFragment = LikeFragment.newInstance()
                var bundle = Bundle()
                var uid = FirebaseAuth.getInstance().currentUser?.uid
                bundle.putString("favoriteUid",uid)
                likeFragment.arguments = bundle
                supportFragmentManager.beginTransaction().replace(R.id.fragment_frame, likeFragment,"like").commit()
            }
            R.id.user -> {
                userFragment = UserFragment.newInstance()
                supportFragmentManager.beginTransaction().replace(R.id.fragment_frame, userFragment,"user").commit()

            }
        }
        true
    }


    override fun onDestroy() {
        super.onDestroy()
        Log.e("MainActivity","메인액티비티파괴")
    }

    //이 함수를 통해 다른 fragment로 이동한다.생성자가 아닌 불러오는 형식
    // 프래그먼트 친구 이름 클릭 시 프래그먼트 변경하는 함수
    fun fragmentChange_for_adapter(friendpageFragment: Fragment){
        supportFragmentManager.beginTransaction().replace(R.id.fragment_frame, friendpageFragment).commit()
    }

    fun fragemtChage_for_adapter_view(viewpageFragment: Fragment){
        supportFragmentManager.beginTransaction().replace(R.id.fragment_frame, viewpageFragment).commit()
    }


}
