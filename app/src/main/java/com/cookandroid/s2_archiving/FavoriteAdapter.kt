package com.cookandroid.s2_archiving

import android.content.Context
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
import com.cookandroid.s2_archiving.model.MyFavorite
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class FavoriteAdapter(val context: Context, val fragment_s: Fragment):RecyclerView.Adapter<FavoriteAdapter.CustomViewHolder>() {
    private var mFirebaseAuth: FirebaseAuth? = FirebaseAuth.getInstance() //파이어베이스 인증
    private var mDatabaseRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("Firebase")//실시간 데이터베이스
    private var heartRef : DatabaseReference = mDatabaseRef.child("UserPosts").child("${mFirebaseAuth?.currentUser!!.uid}") //게시글정보 불러오기
    private lateinit var fbStorage: FirebaseStorage
    var postDataList : ArrayList<PostData> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.post_list,parent,false)

        return CustomViewHolder(view)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        if (postDataList[position].heart == 0) { //좋아하는 페이지만 나오도록
            Glide.with(holder.itemView).load(postDataList[position].postPhotoUri).into(holder.postimage)
            holder.date.text = postDataList[position].postDate
            holder.special.text = postDataList[position].postDateName
            holder.heart.setImageResource(R.drawable.heart_full)
        } else {
            Log.d("likepage_null","나오면 안됨!!!!!")
        }
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