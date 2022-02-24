package com.cookandroid.s2_archiving.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cookandroid.s2_archiving.*
import com.cookandroid.s2_archiving.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.fragment_like.view.*

class HomeFragment : Fragment() {

    //위젯 연결할 변수 선언
    lateinit var adapter : RecyclerView.Adapter<FriendDataAdapter.CustomViewHolder>
    lateinit var cardAdapter : RecyclerView.Adapter<CardAdapter.CustomViewHolder>
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var friendDataList: ArrayList<FriendData>
    lateinit var cardDataList : ArrayList<PostData>
    lateinit var cardDataListReverse : ArrayList<PostData>

    lateinit var friendId:String

    //파이어베이스
    private lateinit var database : FirebaseDatabase
    private lateinit var mDatabaseRef : DatabaseReference
    private var mFirebaseAuth: FirebaseAuth? = null

    // xml
    private lateinit var userNickname : TextView
    private lateinit var ivProfile:ImageView

    // context
    private lateinit var activity: Activity



    //정적으로 사용되는 부분
    companion object {
        fun newInstance() : HomeFragment{
            return HomeFragment()
        }
    }

    //메모리에 올라갔을때
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    //프레그먼트를 안고 있는 엑티비티에 붙었을 때

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = context as Activity
    }


    //뷰가 생성되었을때
    //프레그먼트와 레이아웃 연결시켜줌
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val fragmentview = inflater.inflate(R.layout.fragment_home, container, false) //홈프레그먼트 xml파일이랑 연결
        //파이어베이스 계정, 리얼타임 데이터베이스
        mFirebaseAuth = FirebaseAuth.getInstance()
        friendDataList = ArrayList() //FriendData 객체를 담을 ArrayList
        cardDataList = ArrayList() //카드뷰에 실시간으로 올라오는 객체 담을 ArryaList
        cardDataListReverse = ArrayList() //카드뷰에 실시간으로 올라오는 객체를 5개만 담고 거꾸로 저장하는 ArryaList

        database = FirebaseDatabase.getInstance() //파이어베이스 데이터베이스 연동
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Firebase")
        userNickname = fragmentview.findViewById(R.id.tvName)
        ivProfile = fragmentview.findViewById(R.id.ivProfile)

        fragmentview?.cardrv?.layoutManager = LinearLayoutManager(this.requireContext(),LinearLayoutManager.HORIZONTAL, true)//가로 리사이클러뷰

        return fragmentview
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
                var fragment: Fragment = SearchbtnClickFragment()
                var activityH = this.activity as MainActivity
                activityH?.fragemtChage_for_adapter_view(fragment)
            }
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) { // <-- 리사이클러뷰 넣어보기
        super.onViewCreated(view, savedInstanceState)

        //리사이클러뷰에 담을 데이터 가져오기(selectedItem 태그를 통해서 보여줄 게시글 구분)
        // 사용자의 닉네임, 사진 로드
        mDatabaseRef.child("UserAccount").child("${mFirebaseAuth?.currentUser!!.uid}")
            .addValueEventListener(object : ValueEventListener {

                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.value == null) { // 널이면 아무것도하지마

                    } else {
                        var user: UserAccount? = snapshot.getValue(UserAccount::class.java)
                        var nickName = user!!.userNickname

                        userNickname.text = nickName
                        // 사진 url 추가 후 load하는 코드 넣을 자리
                        if ("${user!!.userPhotoUri}" == "") {
                            ivProfile.setImageResource(R.drawable.man)
                        } else { // userPhotoUri가 있으면 그 사진 로드하기
                            Glide.with(activity)
                                .load(user!!.userPhotoUri)
                                .into(ivProfile)
                        }
                    }
                }
            })

        mDatabaseRef.child("UserFriends")
            .child("${mFirebaseAuth!!.currentUser!!.uid}")
            .orderByChild("fStar")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.value == null) { // 널이면 아무것도하지마

                    } else {

                        friendDataList.clear()

                        for (data: DataSnapshot in snapshot.children) {
                            var friendData: FriendData? = data.getValue(FriendData::class.java)

                            friendDataList.add(friendData!!) //담은 데이터들을 배열리스트에 넣고 리사이클러뷰로 보낼 준비

                            Log.d("태그", "$friendDataList")
                        }
                        adapter.notifyDataSetChanged() //리스트 저장 및 새로고침

                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })

        adapter = FriendDataAdapter(friendDataList, activity, this)
        rvProfile.adapter = adapter

        mDatabaseRef.child("UserPosts").child("${mFirebaseAuth!!.currentUser!!.uid}")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.value == null) {}  // 널이면 아무것도하지마
                    else {
                        cardDataList.clear()
                        cardDataListReverse.clear()

                        for (data: DataSnapshot in snapshot.children) {
                            var postData: PostData? = data.getValue(PostData::class.java)
                            cardDataList.add(postData!!) // 리스트에 넣기
                         }

                        var size = cardDataList!!.size

                        if(size >5){ // 전체 게시글이 5개보다 많으면
                            for (i in size-5 until size){
                                cardDataListReverse.add(cardDataList[i])
                            }
                        }
                        else{ // 5개 이하라면
                            for (i in 0 until size){
                                cardDataListReverse.add(cardDataList[i])
                            }
                        }

                        cardAdapter.notifyDataSetChanged() // 리스트 저장 및 새로 고침
                        cardrv?.scrollToPosition(cardDataListReverse!!.size - 1)
                      }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })

        cardAdapter = CardAdapter(cardDataListReverse, activity, this)
        cardrv.adapter = cardAdapter

    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onDetach() {
        super.onDetach()
    }
}


