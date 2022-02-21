package com.cookandroid.s2_archiving

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.security.AccessController.getContext

class ViewAdapter(val viewDataList: ArrayList<PostData>, val context: Context, val fragmet_s: Fragment) : RecyclerView.Adapter<ViewAdapter.CustomViewHolder>() {

    private var mFirebaseAuth: FirebaseAuth? = FirebaseAuth.getInstance() //파이어베이스 인증
    private var mDatabaseRef: DatabaseReference =
        FirebaseDatabase.getInstance().getReference("Firebase")//실시간 데이터베이스
    private lateinit var fbStorage: FirebaseStorage

    private var activity: MainActivity? = null//메인에 함수 부르기 위해 선언하기

    inner class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val viewImage = itemView.findViewById<ImageView>(R.id.view_img)
        val viewDate = itemView.findViewById<TextView>(R.id.view_date)
        val viewSpecial = itemView.findViewById<TextView>(R.id.view_special)
        val viewHeart = itemView.findViewById<ImageView>(R.id.view_heart)
        val viewStory = itemView.findViewById<TextView>(R.id.view_story)
        val viewEtc = itemView.findViewById<ImageView>(R.id.ivEtc)
        val viewDelete = itemView.findViewById<ImageView>(R.id.ivDelete)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.view_list, parent, false)
        return CustomViewHolder(view)
    }


    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        if (viewDataList[position].postPhotoUri == "") {
            holder.viewImage.setImageResource(R.drawable.camera)  //수정 필요
        } else {
            Glide.with(holder.itemView)
                .load(viewDataList[position].postPhotoUri)
                .into(holder.viewImage)
        }
        holder.viewDate.text = viewDataList[position].postDate
        holder.viewSpecial.text = viewDataList[position].postDateName
        holder.viewHeart.setImageResource(R.drawable.heart)
        holder.viewStory.text = viewDataList[position].post
        holder.viewEtc.setOnClickListener{   //게시글 수정 (PostActivity로 이동)
            val intent = Intent(context,PostActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            this.context.startActivity(intent)
        }

        Log.e("aa",viewDataList!!.toString()) // 삭제 정상작동 되면 지우기
        holder.viewDelete.setOnClickListener {    //게시글 삭제
            mDatabaseRef.ref.child("UserPosts").child("${mFirebaseAuth!!.currentUser!!.uid}").removeValue().addOnSuccessListener {
                Toast.makeText(context,"게시글 삭제 완료",Toast.LENGTH_SHORT).show()
            }

        }

    }

    override fun getItemCount(): Int {
        return viewDataList.size
    }


}