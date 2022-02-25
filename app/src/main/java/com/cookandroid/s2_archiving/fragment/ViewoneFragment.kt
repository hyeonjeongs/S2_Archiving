package com.cookandroid.s2_archiving.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.cookandroid.s2_archiving.*
import com.cookandroid.s2_archiving.R
import com.google.android.material.card.MaterialCardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_view_one.*
import kotlinx.android.synthetic.main.fragment_view_one.view.*


class ViewoneFragment: Fragment(), onBackPressedListener {

    lateinit var friendId:String
    lateinit var postId:String
    lateinit var id:String
    private var mFirebaseAuth: FirebaseAuth? = FirebaseAuth.getInstance() //파이어베이스 인증
    private var mDatabaseRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("Firebase")//실시간 데이터베이스
    private var storage : FirebaseStorage? = FirebaseStorage.getInstance()
    private lateinit var activitys: Activity



    // xml
    private lateinit var viewOneImage: ImageView
    private lateinit var viewoneprofileimage : ImageView
    lateinit var viewoneName : TextView
    private lateinit var onefragmenubtn : Button

    // flag
    private var flag:Int =0



    // 프레그먼트를 안고 있는 액티비티에 붙었을 때(프래그먼트가 엑티비티에 올라온 순간)
    override fun onAttach(context: Context) {
        super.onAttach(context)
        activitys = context as Activity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_view_one, container, false)
        friendId = requireArguments().getString("friend_id").toString()
        postId = requireArguments().getString("post_id").toString()
        id = requireArguments().getString("id").toString()
        viewOneImage=view.findViewById(R.id.view_one_img)
        viewoneName = view.findViewById(R.id.viewonename)
        viewoneprofileimage = view.findViewById(R.id.ViewOneProfileImage)
        onefragmenubtn = view.findViewById(R.id.onefragmenubtn)

        //registerForContextMenu(onefragmenubtn)

        //메뉴클릭시
        onefragmenubtn.setOnClickListener {
            val views = onefragmenubtn
            showPopup(views)
        }


        return view
    }

    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
//        val inflater: MenuInflater = activitys.menuInflater
//        inflater.inflate(R.menu.showmenu, menu)
          activitys.menuInflater.inflate(R.menu.showmenu, menu)

    }

    override fun onContextItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.revise -> {
                //Toast.makeText(activitys.applicationContext,"수정",Toast.LENGTH_SHORT).show()
                Log.d("dk", "slkdfjsldfsdf")
            }
            R.id.delete -> {
//                Toast.makeText(activitys.applicationContext,"삭제",Toast.LENGTH_SHORT).show()
                Log.d("dkfsld", "ldksfj")
            }

        }
        return super.onContextItemSelected(item)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        mDatabaseRef.child("UserPosts").child("${mFirebaseAuth?.currentUser!!.uid}")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (data: DataSnapshot in snapshot.children) {
                        var post: PostData? = data.getValue(PostData::class.java)
                        if (post!!.postId == postId) {
                            if (post!!.postPhotoUri == "") {
                                view_one_img?.setImageResource(R.drawable.man)
                            } else { // Uri가 있으면 그 사진 로드하기
                                Glide.with(activitys)
                                    .load(post!!.postPhotoUri)
                                    .into(viewOneImage)
                                flag = 1
                            }

                            view_one_date?.text = post!!.postDate
                            view_one_name?.text = post!!.postDateName
                            view_one_story?.text = post!!.post
                            if (post!!.heart == 1) {
                                view_one_heart?.setImageResource(R.drawable.heart_empty)
                            } else if (post!!.heart == 0) {
                                view_one_heart?.setImageResource(R.drawable.heart_full)
                            }
                            view_one_heart?.setOnClickListener {
                                heartEvent(post!!)
                            }
                        }
                    }

                }

                override fun onCancelled(error: DatabaseError) {

                }
            })

        //친구 프로필 이미지
        mDatabaseRef.child("UserFriends").child("${mFirebaseAuth?.currentUser!!.uid}").child(
            friendId
        )
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var friend: FriendData? = snapshot.getValue(FriendData::class.java)
                    viewonename.text = friend!!.fName
                    if (friend!!.fImgUri == "") {
                        viewoneprofileimage.setImageResource(R.drawable.man)
                    } else { // Uri가 있으면 그 사진 로드하기
                        Glide.with(activitys)
                            .load(friend!!.fImgUri)
                            .into(viewoneprofileimage)
                    }

                }

                override fun onCancelled(error: DatabaseError) {

                }

            })





        btnViewOneBack.setOnClickListener{
            onBackPressed()
        }


    }

    override fun onBackPressed() {  //휴대폰의 뒤로가기 버튼 클릭 시
        if (this is ViewoneFragment) {
            if (id == "cardview_adapter") {
                var fragment: Fragment = HomeFragment()
                var activityH = this.activity as MainActivity?
                activityH?.fragmentChange_for_adapter(fragment)
            }
        }
    }

    //수정, 삭제 팝업 띄우기
    fun showPopup(v : View){
        val popup = PopupMenu(activitys.applicationContext,v)
        popup.menuInflater.inflate(R.menu.showmenu,popup.menu)

        popup.setOnMenuItemClickListener { m ->
            when(m.itemId){
                R.id.revise -> {
                    val intent = Intent(context, EditPostActivity::class.java)
                    intent.putExtra("friend_id", friendId)
                    intent.putExtra("post_id", postId)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    activitys.startActivity(intent)
                    true
                }
                R.id.delete -> {
                    val imageFileName = "IMAGE_" + postId + "_postImage_by"+friendId+".png"

                    if(flag==1){ // 만약 이미지를 포함해서 저장했다면
                        storage?.reference?.child("${mFirebaseAuth?.currentUser!!.uid}")?.child(
                            imageFileName
                        )?.delete()?.addOnSuccessListener {
                            Log.d("storage", "이미지 삭제완료")
                        }
                    }

                    mDatabaseRef.child("UserPosts").child("${mFirebaseAuth!!.currentUser!!.uid}").child(
                        postId
                    ).removeValue().addOnSuccessListener {
                        Toast.makeText(activitys, "게시글 삭제 완료", Toast.LENGTH_SHORT).show()
                    }
                    onBackPressed()
                    true
                }
                else -> false
            }
        }
        popup.show()
    }

    private fun heartEvent(postData: PostData){
        var heart:Int?

        if(postData.heart==1){//하트가 비어있는데 클릭된경우
            heart=0

        }else{
            heart=1
        }
        val hashMap: HashMap<String, Any> = HashMap()
        hashMap.put("heart", heart!!)
        mDatabaseRef.child("UserPosts").child("${mFirebaseAuth?.currentUser!!.uid}").child(postData.postId).updateChildren(
            hashMap
        )
    }

}