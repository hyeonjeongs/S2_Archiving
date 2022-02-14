package com.cookandroid.s2_archiving

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cookandroid.s2_archiving.fragment.FriendpageFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage


class PostAdapter(val postDataList : ArrayList<PostData>, val context: Context, val fragment_s: Fragment): RecyclerView.Adapter<PostAdapter.CustomViewHolder>() {
    private var mFirebaseAuth: FirebaseAuth? = FirebaseAuth.getInstance() //파이어베이스 인증
    private var mDatabaseRef: DatabaseReference =
        FirebaseDatabase.getInstance().getReference("Firebase")//실시간 데이터베이스
    private lateinit var fbStorage: FirebaseStorage

    private var activity: MainActivity? = null//메인에 함수 부르기 위해 선언하기

    // 위젲 연결할 변수 선언
    inner class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val postimage = itemView.findViewById<ImageView>(R.id.post_img)
        val date = itemView.findViewById<TextView>(R.id.post_date)
        val special = itemView.findViewById<TextView>(R.id.post_special)
        val heart = itemView.findViewById<ImageView>(R.id.post_heart)
        val postCardView = itemView.findViewById<CardView>(R.id.post_cardview)

    }

    override fun getItemCount(): Int {
        return postDataList.size
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PostAdapter.CustomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.post_list, parent, false)

        return CustomViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostAdapter.CustomViewHolder, position: Int) {
        if (postDataList.get(position).postPhotoUri == "") {
            holder.postimage.setImageResource(R.drawable.man)
        } else {
            Glide.with(holder.itemView)
                .load(postDataList.get(position).postPhotoUri)
                .into(holder.postimage)
        }
        holder.date.text = postDataList.get(position).postDate
        holder.special.text = postDataList.get(position).postDateName
        holder.heart.setImageResource(R.drawable.heart)

        holder.postCardView.setOnClickListener {
            Log.d("FriendpageFragment", "이동 성공!")
            var fragment: Fragment = FriendpageFragment()
            var bundle: Bundle = Bundle()
//            bundle.putString("friend_name",holder?.fName.text.toString())
//            bundle.putString("friend_id",holder?.fId)

            fragment.arguments = bundle
            activity = fragment_s.activity as MainActivity?
            activity?.fragmentChange_for_adapter(fragment)

        }
    }
}
