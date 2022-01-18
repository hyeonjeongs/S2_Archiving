package com.cookandroid.s2_archiving

import android.Manifest
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
//import com.example.recyclerviewkt.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.add_friend.*

class friendAdd : AppCompatActivity() {


    //
    var photoUri: Uri? = null

//    private var mBinding: ActivityMainBinding? = null
//    private val binding get() = mBinding!!

    lateinit var cameraPermission: ActivityResultLauncher<String>
    lateinit var storagePermission :ActivityResultLauncher<String>

    lateinit var cameraLauncher:ActivityResultLauncher<Uri>
    lateinit var galleryLauncher:ActivityResultLauncher<String>
    //

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


//        mBinding = ActivityMainBinding.inflate(layoutInflater)
//        setContentView(binding.root)


//        //recyclerview 데이터
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
//
//        rvProfile.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
//        rvProfile.setHasFixedSize(true)
//        rvProfile.adapter = ProfileAdapter(profileList)
//        plusButton.setOnClickListener {
//            Toast.makeText(baseContext,"친구추가화면", Toast.LENGTH_SHORT).show()
//        }





        //생년원일 스피너
        year_spinner.adapter = ArrayAdapter.createFromResource(this, R.array.yearItemList, android.R.layout.simple_spinner_item)
        month_spinner.adapter = ArrayAdapter.createFromResource(this, R.array.monthItemList, android.R.layout.simple_spinner_item)
        day_spinner.adapter = ArrayAdapter.createFromResource(this, R.array.dayItemList, android.R.layout.simple_spinner_item)


        storagePermission=registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ){isGranted ->
            if(isGranted){
                setViews()
            } else{
                Toast.makeText(baseContext, "외부 저장소 권한을 승인해야 앱을 사용할 수 있습니다.",Toast.LENGTH_SHORT).show()
                finish()
            }
        }


        cameraPermission=registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ){isGranted ->
            if(isGranted){
                //openCamera()
            } else{
                Toast.makeText(baseContext, "카메라 권한을 승인해야 앱을 사용할 수 있습니다.",Toast.LENGTH_SHORT).show()
                finish()
            }
        }



        galleryLauncher = registerForActivityResult(ActivityResultContracts.
        GetContent()){uri->
            profileImage.setImageURI(uri)
        }

        storagePermission.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    fun setViews(){
        profileImage.setOnClickListener{
            openGallery()
        }
        tvGal.setOnClickListener{
            openGallery()
        }
    }

    fun openGallery(){
        galleryLauncher.launch("image/*")
    }



    //

}



