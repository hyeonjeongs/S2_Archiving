package com.cookandroid.s2_archiving.fragment

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cookandroid.s2_archiving.*
import com.cookandroid.s2_archiving.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {

    //위젯 연결할 변수 선언
    lateinit var adapter : RecyclerView.Adapter<FriendDataAdapter.CustomViewHolder>
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var friendDataList: ArrayList<FriendData>


    //프레그먼트를 위한 변수들
    private lateinit var homeFragment: HomeFragment
    private lateinit var likeFragment: LikeFragment
    private lateinit var userFragment: UserFragment

    //파이어베이스
    private lateinit var database : FirebaseDatabase
    private lateinit var mDatabaseRef : DatabaseReference
    private var mFirebaseAuth: FirebaseAuth? = null //파이어베이스 인증

    // 버튼 연결
    private lateinit var userNickname : TextView


    //
    var photoUri: Uri? = null


    lateinit var cameraPermission: ActivityResultLauncher<String>
    lateinit var storagePermission : ActivityResultLauncher<String>

    lateinit var cameraLauncher: ActivityResultLauncher<Uri>
    lateinit var galleryLauncher: ActivityResultLauncher<String>

    //정적으로 사용되는 부분
    companion object {
        fun newInstance() : HomeFragment{
            return HomeFragment()
        }
    }

    //뷰가 생성되었을때
    //프레그먼트와 레이아웃 연결시켜줌
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home,container,false) //홈프레그먼트 xml파일이랑 연결
        //파이어베이스 계정, 리얼타임 데이터베이스
        mFirebaseAuth = FirebaseAuth.getInstance()
        friendDataList = ArrayList<FriendData>() //FriendData 객체를 담을 ArrayList

        database = FirebaseDatabase.getInstance() //파이어베이스 데이터베이스 연동
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Firebase")

        userNickname = view.findViewById(R.id.tvName)


        return view
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        //친구 추가 버튼에 클릭 리스너 연결
        ivPlus.setOnClickListener {
            activity?.let{
                val intent = Intent(context, FriendAdd::class.java)
                startActivity(intent)
            }
        }

        etSearch.setOnClickListener {
            activity?.let{
                val intent = Intent(context, ModifyAccount::class.java)
                startActivity(intent)
            }
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) { // <-- 리사이클러뷰 넣어보기
        super.onViewCreated(view, savedInstanceState)

        // 사용자의 닉네임, 사진 로드
       mDatabaseRef.child("UserAccount").child("${mFirebaseAuth?.currentUser!!.uid}").addValueEventListener(object : ValueEventListener {

            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                var user: UserAccount? = snapshot.getValue(UserAccount::class.java)
                var nickName = "${user!!.userNickname}"
                userNickname.text = nickName
                // 사진 url 추가 후 load하는 코드 넣을 자리
            }
        })

        //리사이클러뷰에 담을 데이터 가져오기(selectedItem 태그를 통해서 보여줄 게시글 구분)
        mDatabaseRef.child("UserFriends").child("${mFirebaseAuth!!.currentUser!!.uid}")
            .orderByChild("timestamp").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    friendDataList.clear()

                    for (data : DataSnapshot in snapshot.getChildren()) {
                        var friendData : FriendData? = data.getValue(FriendData::class.java)

                        friendDataList.add(friendData!!) //담은 데이터들을 배열리스트에 넣고 리사이클러뷰로 보낼 준비

                        Log.d("태그", "$friendDataList")
                    }
                    adapter.notifyDataSetChanged() //리스트 저장 및 새로고침

                }

                override fun onCancelled(error: DatabaseError) {
                }
            })

        adapter = FriendDataAdapter(friendDataList, this.requireContext())
        rvProfile.setAdapter(adapter)


//        rvProfile.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
//        //layoutManager = LinearLayoutManager(this.context)
//        rvProfile.setHasFixedSize(true)//리사이클러뷰 성능 강화
//        rvProfile.adapter = FriendDataAdapter(friendDataList)


    }


    //메모리에 올라갔을때
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

    }

    //프레그먼트를 안고 있는 엑티비티에 붙었을 때

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }


    override fun onDestroyView() {
        super.onDestroyView()
    }
}