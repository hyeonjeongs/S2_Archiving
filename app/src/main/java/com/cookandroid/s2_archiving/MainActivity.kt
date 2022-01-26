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
<<<<<<< HEAD
=======
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
>>>>>>> 978e629 (navi change)
import androidx.recyclerview.widget.RecyclerView
import com.cookandroid.s2_archiving.fragment.HomeFragment
import com.cookandroid.s2_archiving.fragment.LikeFragment
import com.cookandroid.s2_archiving.fragment.UserFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.add_friend.*
import kotlinx.android.synthetic.main.fragment_home.*

class MainActivity : AppCompatActivity() {


    //프레그먼트를 위한 변수들
    private lateinit var homeFragment: HomeFragment
    private lateinit var likeFragment: LikeFragment
    private lateinit var userFragment: UserFragment




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

<<<<<<< HEAD
        bottom_navi.setOnNavigationItemSelectedListener(onBottomNaviItemSelectedListner)
        homeFragment = HomeFragment.newInstance()
        supportFragmentManager.beginTransaction().add(R.id.fragment_frame,homeFragment).commit()
=======
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentview) as NavHostFragment //네비게이션들을 담는 호스트
        val navController = navHostFragment.navController //네비게이션 컨트롤러
        NavigationUI.setupWithNavController(bottomNavi, navController) //바텀네비게이션 뷰와 네비게이션을 묶어준다.

        //아이디 연결
        rvProfile = findViewById(R.id.rvProfile)
        ivPlus = findViewById(R.id.ivPlus)

        //파이어베이스 계정, 리얼타임 데이터베이스
        mFirebaseAuth = FirebaseAuth.getInstance()

//        mBinding = ActivityMainBinding.inflate(layoutInflater)
//        setContentView(binding.root)


        //recyclerview 데이터
//        val profileList = arrayListOf(
//            Profiles(R.drawable.woman, "조윤진", R.drawable.star_empty),
//            Profiles(R.drawable.man, "김씨", R.drawable.star_full),
//            Profiles(R.drawable.woman, "이땡땡", R.drawable.star_full),
//            Profiles(R.drawable.woman, "최씨", R.drawable.star_empty),
//            Profiles(R.drawable.man, "박땡땡", R.drawable.star_full),
//            Profiles(R.drawable.woman, "신땡땡", R.drawable.star_empty),
//            Profiles(R.drawable.man, "윤씨", R.drawable.star_empty),
//            Profiles(R.drawable.woman, "권씨", R.drawable.star_empty),
//            Profiles(R.drawable.woman, "강씨", R.drawable.star_empty),
//            Profiles(R.drawable.man, "서씨", R.drawable.star_full)
//
//        )

        rvProfile.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        layoutManager = LinearLayoutManager(this)
        rvProfile.setHasFixedSize(true)//리사이클러뷰 성능 강화
        //rvProfile.adapter = ProfileAdapter(profileList)

        arrayList = ArrayList<FriendData>() //FriendData 객체를 담을 ArrayList

        database = FirebaseDatabase.getInstance() //파이어베이스 데이터베이스 연동
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Firebase")

        //리사이클러뷰에 담을 데이터 가져오기(selectedItem 태그를 통해서 보여줄 게시글 구분)
        mDatabaseRef.child("UserFriends").child("${mFirebaseAuth!!.currentUser!!.uid}")
            .orderByChild("timestamp").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    arrayList.clear()

                    for (data : DataSnapshot in snapshot.getChildren()) {
                        var friendData : FriendData? = data.getValue(FriendData::class.java)

                        arrayList.add(friendData!!) //담은 데이터들을 배열리스트에 넣고 리사이클러뷰로 보낼 준비

                        Log.d("태그", "$arrayList")
                    }
                    adapter.notifyDataSetChanged() //리스트 저장 및 새로고침

                }

                override fun onCancelled(error: DatabaseError) {
                }
            })

        adapter = FriendDataAdapter(arrayList, this)
        rvProfile.setAdapter(adapter)

        //친구 추가 버튼에 클릭 리스너 연결
        ivPlus.setOnClickListener {
            Toast.makeText(baseContext,"친구추가화면", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, FriendAdd::class.java)
            startActivity(intent) //화면 이동시킴
        }

        btnSearch.setOnClickListener(View.OnClickListener{
>>>>>>> 978e629 (navi change)


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