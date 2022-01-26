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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.add_friend.*

class MainActivity : AppCompatActivity() {

    //위젯 연결할 변수 선언
    lateinit var rvProfile : RecyclerView
    lateinit var adapter : RecyclerView.Adapter<FriendDataAdapter.CustomViewHolder>
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var arrayList: ArrayList<FriendData>
    lateinit var ivPlus : ImageView

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

            val intent = Intent(this, ModifyAccount::class.java)
            startActivity(intent)

        })



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