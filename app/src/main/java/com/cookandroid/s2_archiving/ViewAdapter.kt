package com.cookandroid.s2_archiving

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class ViewAdapter(
    val vDataList: ArrayList<PostData>,
    val context: Context,
    val fragmet_s: Fragment
) : RecyclerView.Adapter<ViewAdapter.CustomViewHolder>() {

    private var mFirebaseAuth: FirebaseAuth? = FirebaseAuth.getInstance() //파이어베이스 인증
    private var mDatabaseRef: DatabaseReference =
        FirebaseDatabase.getInstance().getReference("Firebase")//실시간 데이터베이스
    private lateinit var fbStorage: FirebaseStorage

    private var activity: MainActivity? = null//메인에 함수 부르기 위해 선언하기


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CustomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.view_list, parent, false)
        return CustomViewHolder(view)
    }


    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        if (vDataList.get(position).postPhotoUri == "") {
            holder.viewImage.setImageResource(R.drawable.camera)  //수정 필요
        } else {
            Glide.with(holder.itemView)
                .load(vDataList.get(position).postPhotoUri)
                .into(holder.viewImage)
        }
        holder.viewDate.text = vDataList.get(position).postDate
        holder.viewSpecial.text = vDataList.get(position).postDateName
        holder.viewHeart.setImageResource(R.drawable.heart)
        holder.viewStory.text = vDataList.get(position).post
        holder.viewEtc.setOnClickListener{
            val myCustomDialog = MyCustomDialog(context)
            myCustomDialog.show()
        }
    }

    override fun getItemCount(): Int {
        return vDataList.size
    }

    class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val viewImage = itemView.findViewById<ImageView>(R.id.view_img)
        val viewDate = itemView.findViewById<TextView>(R.id.view_date)
        val viewSpecial = itemView.findViewById<TextView>(R.id.view_special)
        val viewHeart = itemView.findViewById<ImageView>(R.id.view_heart)
        val viewStory = itemView.findViewById<TextView>(R.id.view_story)
        val viewEtc = itemView.findViewById<ImageView>(R.id.ivEtc)
    }

}