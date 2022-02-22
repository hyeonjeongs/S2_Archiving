package com.cookandroid.s2_archiving

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cookandroid.s2_archiving.FavoriteAdapter.*
import com.cookandroid.s2_archiving.fragment.ViewoneFragment
import com.cookandroid.s2_archiving.model.MyFavorite
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class FavoriteAdapter(val postDataList: ArrayList<PostData>, val context: Context, val fragment_s: Fragment):RecyclerView.Adapter<FavoriteAdapter.CustomViewHolder>() {
    private var mFirebaseAuth: FirebaseAuth? = FirebaseAuth.getInstance() //파이어베이스 인증
    private var mDatabaseRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("Firebase")//실시간 데이터베이스
    private var heartRef : DatabaseReference = mDatabaseRef.child("UserPosts").child("${mFirebaseAuth?.currentUser!!.uid}") //게시글정보 불러오기
    private lateinit var fbStorage: FirebaseStorage
    private var activity: MainActivity? = null//메인에 함수 부르기 위해 선언하기


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.post_list,parent,false)

        return CustomViewHolder(view)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        Glide.with(holder.itemView).load(postDataList[position].postPhotoUri).into(holder.postimage)
        holder.date.text = postDataList[position].postDate
        holder.special.text = postDataList[position].postDateName

        if(postDataList[position].heart==0){
            holder.heart.setImageResource(R.drawable.heart_full)
        }
        else {
            holder.heart.setImageResource(R.drawable.heart_empty)
        }

        holder.heart.setOnClickListener {
            Log.d("FriendHeart","클릭성공!!!!!!!!!!!!")
            heartEvent(position)
        }

        holder.postimage.setOnClickListener {
            var fragment: Fragment = ViewoneFragment()
            var bundle: Bundle = Bundle()
            fragment.arguments = bundle
            bundle.putString("friend_id", postDataList[position].postFriendId)
            bundle.putString("post_id",postDataList[position].postId)
            bundle.putString("id","favorite_adapter")
            activity = fragment_s.activity as MainActivity?
            activity?.fragemtChage_for_adapter_view(fragment)
        }


    }

    private fun heartEvent(position: Int) {
            var postdata:PostData = postDataList[position]
            var heart:Int?

            if(postdata.heart==1){//하트가 비어있는데 클릭된경우
                heart=0
            }else{
                heart=1
            }
            val hashMap: HashMap<String, Any> = HashMap()
            hashMap.put("heart", heart!!)
            mDatabaseRef.child("UserPosts").child("${mFirebaseAuth?.currentUser!!.uid}").child(postdata.postId).updateChildren(hashMap)

    }

    override fun getItemCount(): Int {
        return postDataList.size
    }

    class CustomViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
        val postimage = itemView.findViewById<ImageView>(R.id.post_img)
        val date = itemView.findViewById<TextView>(R.id.post_date)
        val special = itemView.findViewById<TextView>(R.id.post_special)
        val heart = itemView.findViewById<ImageView>(R.id.post_heart)
        val postCardView = itemView.findViewById<CardView>(R.id.post_cardview)
    }
}