package com.cookandroid.s2_archiving

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.list_item.view.*
import kotlin.collections.ArrayList
import com.bumptech.glide.Glide;

class FriendDataAdapter() : RecyclerView.Adapter<FriendDataAdapter.CustomViewHolder>() {

    private var mFirebaseAuth: FirebaseAuth? = null //파이어베이스 인증
    private lateinit var mDatabaseRef: DatabaseReference //실시간 데이터베이스
    private lateinit var fbStorage: FirebaseStorage

    private lateinit var friendDataList : ArrayList<FriendData>
    private lateinit var context : Context

    constructor(friendDataList: ArrayList<FriendData>, context: Context) : this(){
        this.friendDataList = friendDataList
        this.context = context
    }



    //위젯 연결할 변수 선언
    class CustomViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val image = itemView.findViewById<ImageView>(R.id.ivProfile)
        val fName = itemView.findViewById<TextView>(R.id.tvName)
        val star = itemView.findViewById<ImageView>(R.id.ivStar)
    }

    override fun getItemCount(): Int {
        if(friendDataList != null){
            return friendDataList.size
        }else
        {
            return 0
        }
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        if(friendDataList.get(position).fImgurl == null){
            holder.image.setImageResource(R.drawable.man)
        }else{
            Glide.with(holder.itemView)
                .load(friendDataList.get(position).fImgurl)
                .into(holder.image)
        }

        holder.fName.text = friendDataList.get(position).fName

        if(friendDataList.get(position).fStar == null){
            holder.star.setImageResource(R.drawable.star_empty)
        }else{
            Glide.with(holder.itemView)
                .load(friendDataList.get(position).fStar)
                .into(holder.star)
        }



    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return CustomViewHolder(view).apply {
            itemView.setOnClickListener {
                val curPos : Int = adapterPosition
                val friendData : FriendData = friendDataList.get(curPos)
                if(curPos != RecyclerView.NO_POSITION){
                    var intent = Intent(context, FriendActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent.putExtra("IMGURL", friendData.fImgurl)
                    intent.putExtra("FNAME", friendData.fName)
                    intent.putExtra("STAR", friendData.fStar)
                    context.startActivity(intent)
                }
            }
        }
    }
}