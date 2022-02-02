package com.cookandroid.s2_archiving

import android.Manifest
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_write.*

class WriteActivity : AppCompatActivity() {


    var photoUri: Uri? = null

    lateinit var cameraPermission: ActivityResultLauncher<String>
    lateinit var storagePermission : ActivityResultLauncher<String>

    lateinit var cameraLauncher: ActivityResultLauncher<Uri>
    lateinit var galleryLauncher: ActivityResultLauncher<String>

    var imgUrl : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write)



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
            ivWriteCamera.setImageURI(uri)
        }

        storagePermission.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)

    }

    fun setViews(){
        ivWriteCamera.setOnClickListener{
            openGallery()
        }
        tvWriteImage.setOnClickListener{
            openGallery()
        }
    }

    fun openGallery(){
        galleryLauncher.launch("image/*")
    }



}