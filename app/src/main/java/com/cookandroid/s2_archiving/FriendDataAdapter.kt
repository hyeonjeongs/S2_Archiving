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
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.list_item.view.*
import kotlin.collections.ArrayList
import com.bumptech.glide.Glide;

class FriendDataAdapter() : RecyclerView.Adapter<FriendDataAdapter.CustomViewHolder>() {

    private var mFirebaseAuth: FirebaseAuth? = FirebaseAuth.getInstance() //파이어베이스 인증
    private var mDatabaseRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("Firebase")//실시간 데이터베이스
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
        if(friendDataList.get(position).fImgurl == ""){
            holder.image.setImageResource(R.drawable.man)
        }else{
            Glide.with(holder.itemView)
                .load(friendDataList.get(position).fImgurl)
                .into(holder.image)
        }

        holder.fName.text = friendDataList.get(position).fName

        if(friendDataList.get(position).fStar == "0"){
            holder.star.setImageResource(R.drawable.star_empty)
        }
        else if(friendDataList.get(position).fStar == "1"){
            holder.star.setImageResource(R.drawable.star_full)
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return CustomViewHolder(view).apply {
            var i=0
            star.setOnClickListener {

                val curPos : Int = adapterPosition
                Log.e("위치",curPos.toString())

                val friendData : FriendData = friendDataList.get(curPos)
                val fId = friendData.fId

                if(i==0) { // full star로 바꿀 때
                    star.setImageResource(R.drawable.star_full)
                    i=1
                    mDatabaseRef.child("UserFriends").child("${mFirebaseAuth?.currentUser!!.uid}").child("${fId}")
                        .addValueEventListener(object : ValueEventListener {

                            override fun onCancelled(error: DatabaseError) {

                            }

                            override fun onDataChange(snapshot: DataSnapshot) {
                                val fAdd = friendData.fAdd
                                val fBday = friendData.fBday
                                val fId = friendData.fId
                                val fName = friendData.fName
                                val fPhone = friendData.fPhone
                                val fRel = friendData.fRel
                                val timestamp = friendData.timestamp
                                var fStar = "1" // 값을 1로 변경

                                val hashMap: HashMap<String, String> = HashMap()
                                hashMap.put("fAdd", fAdd)
                                hashMap.put("fBday", fBday)
                                hashMap.put("fId", fId)
                                hashMap.put("fImgurl", "")
                                hashMap.put("fName", fName)
                                hashMap.put("fPhone", fPhone)
                                hashMap.put("fRel", fRel)
                                hashMap.put("fStar", fStar)
                                hashMap.put("timstamp", timestamp)

                                mDatabaseRef.child("UserFriends").child("${mFirebaseAuth?.currentUser!!.uid}").child("${fId}").setValue(hashMap)

                            }
                        })
                }
                else if(i==1){ // empty_star로 바꿀 때
                    star.setImageResource(R.drawable.star_empty)
                    i=0
                    mDatabaseRef.child("UserFriends").child("${mFirebaseAuth?.currentUser!!.uid}").child("${fId}")
                        .addValueEventListener(object : ValueEventListener {

                            override fun onCancelled(error: DatabaseError) {

                            }

                            override fun onDataChange(snapshot: DataSnapshot) {

                                val fAdd = friendData.fAdd
                                val fBday = friendData.fBday
                                val fId = friendData.fId
                                val fName = friendData.fName
                                val fPhone = friendData.fPhone
                                val fRel = friendData.fRel
                                val timestamp = friendData.timestamp
                                val fStar = "0" // 값을 다시 0으로 변경함


                                val hashMap: HashMap<String, String> = HashMap()
                                hashMap.put("fAdd", fAdd)
                                hashMap.put("fBday", fBday)
                                hashMap.put("fId", fId)
                                hashMap.put("fImgurl", "")
                                hashMap.put("fName", fName)
                                hashMap.put("fPhone", fPhone)
                                hashMap.put("fRel", fRel)
                                hashMap.put("fStar", fStar)
                                hashMap.put("timstamp", timestamp)

                                mDatabaseRef.child("UserFriends").child("${mFirebaseAuth?.currentUser!!.uid}").child("${fId}").setValue(hashMap)

                            }
                        })
                }



                //val friendData : FriendData = friendDataList.get(curPos)
                //if(curPos != RecyclerView.NO_POSITION){
                    //var intent = Intent(context, FriendActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    //intent.putExtra("IMGURL", friendData.fImgurl)
                    //intent.putExtra("FNAME", friendData.fName)
                    //intent.putExtra("STAR", friendData.fStar)
                    //context.startActivity(intent)
               // }
            }
        }
    }
}