package com.cookandroid.s2_archiving

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class CardAdapter(val postDataList : ArrayList<PostData>, val context: Context, val fragment_s: Fragment) : RecyclerView.Adapter<CardAdapter.CustomViewHolder>() {
    private var mFirebaseAuth: FirebaseAuth? = FirebaseAuth.getInstance() //파이어베이스 인증
    private var mDatabaseRef: DatabaseReference =
        FirebaseDatabase.getInstance().getReference("Firebase")//실시간 데이터베이스
    private lateinit var fbStorage: FirebaseStorage

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
        //holder.cvfriendname.text = postDataList[position].postFriendId //수정하기
        /*Glide.with(holder.itemView).load(postDataList[position].postPhotoUri).into(holder.postimage)
        holder.date.text = postDataList[position].postDate
        holder.special.text = postDataList[position].postDateName*/
    }

    override fun getItemCount(): Int {
        return postDataList.size
    }

    inner class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cvimage = itemView.findViewById<ImageView>(R.id.cvImge)
        val cvdate = itemView.findViewById<TextView>(R.id.dayNumber)
        //val cvfriendname = itemView.findViewById<TextView>(R.id.fName)
        val cvspecial = itemView.findViewById<TextView>(R.id.dayName)
        /*
        val postimage = itemView.findViewById<ImageView>(R.id.post_img)
        val date = itemView.findViewById<TextView>(R.id.post_date)
        val special = itemView.findViewById<TextView>(R.id.post_special)
        val heart = itemView.findViewById<ImageView>(R.id.post_heart)*/
    }
}