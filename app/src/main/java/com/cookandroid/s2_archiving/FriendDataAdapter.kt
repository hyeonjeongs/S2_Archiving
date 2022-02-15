package com.cookandroid.s2_archiving

import android.content.Context

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cookandroid.s2_archiving.fragment.FriendpageFragment
import com.cookandroid.s2_archiving.fragment.ViewpageFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage



class FriendDataAdapter(val friendDataList: ArrayList<FriendData>, val context: Context, val fragment_s:Fragment) : RecyclerView.Adapter<FriendDataAdapter.CustomViewHolder>() {

    private var mFirebaseAuth: FirebaseAuth? = FirebaseAuth.getInstance() //파이어베이스 인증
    private var mDatabaseRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("Firebase")//실시간 데이터베이스

    private var activity : MainActivity? = null//메인에 함수 부르기 위해 선언하기



    //위젯 연결할 변수 선언
    inner class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image = itemView.findViewById<ImageView>(R.id.ivProfile)
        val fName = itemView.findViewById<TextView>(R.id.tvName)
        val star = itemView.findViewById<ImageView>(R.id.ivStar)
        var fId = ""
        fun bind(data: FriendData,context: Context){
            fName.text = data.fName
            fId = data.fId
        }
    }

    override fun getItemCount(): Int {
        if(friendDataList != null) {
            return friendDataList.size
        } else {
            return 0
        }
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {

        if (friendDataList.get(position).fImgUri == "") {
            holder.image.setImageResource(R.drawable.man)
        } else {
            Glide.with(holder.itemView)
                .load(friendDataList.get(position).fImgUri)
                .into(holder.image)
        }

        holder.fName.text = friendDataList.get(position).fName
        holder.fId = friendDataList.get(position).fId

        holder.fName.setOnClickListener {
            Log.d("FriendpageFragment", "이동 성공!")
            var fragment:Fragment = FriendpageFragment()
            var bundle: Bundle = Bundle()
            bundle.putString("friend_name",holder?.fName.text.toString())
            bundle.putString("friend_id",holder?.fId)

            fragment.arguments=bundle
            activity = fragment_s.activity as MainActivity?
            activity?.fragmentChange_for_adapter(fragment)

        }

        holder.star.setOnClickListener {
            starAction(position)
        }

        if (friendDataList.get(position).fStar == "0") {
            holder.star.setImageResource(R.drawable.star_empty)
        } else if (friendDataList.get(position).fStar == "1") {
            holder.star.setImageResource(R.drawable.star_full)
        }

    }

    private fun starAction(position: Int) {
        var friendData:FriendData = friendDataList.get(position)
        var star:String?
        if(friendData.fStar=="0"){
            star ="1"
        }
        else{
            star="0"
        }
        val hashMap: HashMap<String, Any> = HashMap()
        hashMap.put("fStar", star!!)
        mDatabaseRef.child("UserFriends").child("${mFirebaseAuth?.currentUser!!.uid}").child("${friendData.fId}").updateChildren(hashMap)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return CustomViewHolder(view)
    }
}


