package com.cookandroid.s2_archiving

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cookandroid.s2_archiving.fragment.ViewoneFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage

class CardAdapter(val postDataList : ArrayList<PostData>, val context: Context, val fragment_s: Fragment) : RecyclerView.Adapter<CardAdapter.CustomViewHolder>() {
    private var mFirebaseAuth: FirebaseAuth? = FirebaseAuth.getInstance() //파이어베이스 인증
    private var mDatabaseRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("Firebase")//실시간 데이터베이스
    private lateinit var fbStorage: FirebaseStorage

    private lateinit var fId:String
    private var fName:String = ""

    private var activity: MainActivity? = null//메인에 함수 부르기 위해 선언하기


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.newpost_list, parent, false)

        return CustomViewHolder(view)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        if (postDataList[position].postPhotoUri == "") {
            holder.cvimage.setImageResource(R.drawable.man)
        } else {
            Glide.with(holder.itemView)
                .load(postDataList[position].postPhotoUri)
                .into(holder.cvimage)
        }
        holder.cvdate.text = postDataList[position].postDate
        holder.cvspecial.text = postDataList[position].postDateName

        fId = postDataList[position].postFriendId
        // 친구 이름 가져오기
        mDatabaseRef.child("UserFriends").child("${mFirebaseAuth?.currentUser!!.uid}").child(fId).addValueEventListener(object :
            ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.value == null) { // 널이면 아무것도하지마

                }
                else { // 카드뷰 수정되면 프로필 이미지 로드
                    var friend:FriendData? = snapshot.getValue(FriendData::class.java)
                    fName = friend!!.fName
                    holder.cvfriendname.text = fName
                }
            }
        })


        holder.cardView.setOnClickListener { // 카드뷰로 나중에 바꾸기
            var fragment: Fragment = ViewoneFragment()
            var bundle: Bundle = Bundle()
            fragment.arguments = bundle
            bundle.putString("friend_id", postDataList[position].postFriendId)
            bundle.putString("post_id",postDataList[position].postId)
            bundle.putString("id","cardview_adapter")
            activity = fragment_s.activity as MainActivity?
            activity?.fragemtChage_for_adapter_view(fragment)
        }

    }

    override fun getItemCount(): Int {
        return postDataList.size
    }

    inner class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cvimage = itemView.findViewById<ImageView>(R.id.cvImge)
        val cvdate = itemView.findViewById<TextView>(R.id.dayNumber)
        val cvfriendname = itemView.findViewById<TextView>(R.id.fName)
        val cvspecial = itemView.findViewById<TextView>(R.id.dayName)
        val cardView = itemView.findViewById<CardView>(R.id.cardView)

    }
}