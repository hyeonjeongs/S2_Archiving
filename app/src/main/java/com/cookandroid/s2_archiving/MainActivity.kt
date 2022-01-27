package com.cookandroid.s2_archiving

import android.Manifest
import android.content.Intent
import android.media.Image
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
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

    //위젯 연결할 변수 선언
//    lateinit var rvProfile : RecyclerView
//    lateinit var adapter : RecyclerView.Adapter<FriendDataAdapter.CustomViewHolder>
//    lateinit var layoutManager: RecyclerView.LayoutManager
//    lateinit var arrayList: ArrayList<FriendData>
//    lateinit var ivPlus : ImageView
//    lateinit var ivStar : ImageView

    //프레그먼트를 위한 변수들
    private lateinit var homeFragment: HomeFragment
    private lateinit var likeFragment: LikeFragment
    private lateinit var userFragment: UserFragment

    //파이어베이스
    private lateinit var database : FirebaseDatabase
    private lateinit var mDatabaseRef : DatabaseReference
    private var mFirebaseAuth: FirebaseAuth? = null //파이어베이스 인증


    //
    var photoUri: Uri? = null

//    private var mBinding: ActivityMainBinding? = null
//    private val binding get() = mBinding!!

    lateinit var cameraPermission: ActivityResultLauncher<String>
    lateinit var storagePermission : ActivityResultLauncher<String>

    lateinit var cameraLauncher: ActivityResultLauncher<Uri>
    lateinit var galleryLauncher: ActivityResultLauncher<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottom_navi.setOnNavigationItemSelectedListener(onBottomNaviItemSelectedListner)
        homeFragment = HomeFragment.newInstance()
        supportFragmentManager.beginTransaction().add(R.id.fragment_frame,homeFragment).commit()

        //아이디 연결
//        rvProfile = findViewById(R.id.rvProfile)
//        ivPlus = findViewById(R.id.ivPlus)


        //파이어베이스 계정, 리얼타임 데이터베이스
        mFirebaseAuth = FirebaseAuth.getInstance()

//        mBinding = ActivityMainBinding.inflate(layoutInflater)
//        setContentView(binding.root)



//
//        arrayList = ArrayList<FriendData>() //FriendData 객체를 담을 ArrayList

        database = FirebaseDatabase.getInstance() //파이어베이스 데이터베이스 연동
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Firebase")

        //리사이클러뷰에 담을 데이터 가져오기(selectedItem 태그를 통해서 보여줄 게시글 구분)
//        mDatabaseRef.child("UserFriends").child("${mFirebaseAuth!!.currentUser!!.uid}")
//            .orderByChild("timestamp").addValueEventListener(object : ValueEventListener {
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    arrayList.clear()
//
//                    for (data : DataSnapshot in snapshot.getChildren()) {
//                        var friendData : FriendData? = data.getValue(FriendData::class.java)
//
//                        arrayList.add(friendData!!) //담은 데이터들을 배열리스트에 넣고 리사이클러뷰로 보낼 준비
//
//                        Log.d("태그", "$arrayList")
//                    }
//                    adapter.notifyDataSetChanged() //리스트 저장 및 새로고침
//
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//                }
//            })

//        adapter = FriendDataAdapter(arrayList, this)
//        rvProfile.setAdapter(adapter)



        //
        //생년원일 스피너
//        year_spinner.adapter = ArrayAdapter.createFromResource(this, R.array.yearItemList, android.R.layout.simple_spinner_item)
//        month_spinner.adapter = ArrayAdapter.createFromResource(this, R.array.monthItemList, android.R.layout.simple_spinner_item)
//        day_spinner.adapter = ArrayAdapter.createFromResource(this, R.array.dayItemList, android.R.layout.simple_spinner_item)


        storagePermission=registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ){isGranted ->
            if(isGranted){
                setViews()
            } else{
                Toast.makeText(baseContext, "외부 저장소 권한을 승인해야 앱을 사용할 수 있습니다.", Toast.LENGTH_SHORT).show()
                finish()
            }
        }


        cameraPermission=registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ){isGranted ->
            if(isGranted){
                //openCamera()
            } else{
                Toast.makeText(baseContext, "카메라 권한을 승인해야 앱을 사용할 수 있습니다.", Toast.LENGTH_SHORT).show()
                finish()
            }
        }



        galleryLauncher = registerForActivityResult(
            ActivityResultContracts.
        GetContent()){uri->
            ivProfileImage.setImageURI(uri)
        }

        storagePermission.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)

        //친구 추가 버튼 뷰에 클릭 리스너 설정(친구추가버튼 클릭시 발생)
//        plusButton.setOnClickListener {
//            friendaddClicked()
//        }

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
    fun setViews(){
//        profileImage.setOnClickListener{
//            openGallery()
//        }
//        tvGal.setOnClickListener{
//            openGallery()
//        }
    }

    fun openGallery(){
        galleryLauncher.launch("image/*")
    }

//    fun friendaddClicked(){
//        val intent = Intent(this, FriendAdd::class.java)
//        startActivity(intent) //화면 이동시킴
//    }
}