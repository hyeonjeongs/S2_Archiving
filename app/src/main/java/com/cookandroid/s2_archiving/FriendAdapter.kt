package com.example.instaprac

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cookandroid.s2_archiving.R

class FriendAdapter(val friendList : ArrayList<FriendModel>): RecyclerView.Adapter<FriendAdapter.CustomViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendAdapter.CustomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.friend_list,parent,false)

        return CustomViewHolder(view)
    }

    override fun onBindViewHolder(holder: FriendAdapter.CustomViewHolder, position: Int) {
        holder.profileimage.setImageResource(friendList.get(position).profileimage)
        holder.date.text = friendList.get(position).date.toString()
        holder.special.text = friendList.get(position).special //숫자인 age를 문자열형태로 변환하여 저장
        holder.heart.setImageResource(friendList.get(position).heart)
    }

    override fun getItemCount(): Int {
        return friendList.size
    }

    class CustomViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView) {
        val profileimage = itemView.findViewById<ImageView>(R.id.friend_img)
        val date = itemView.findViewById<TextView>(R.id.friend_date)
        val special = itemView.findViewById<TextView>(R.id.friend_special)
        val heart = itemView.findViewById<ImageView>(R.id.freind_heart)

    }
}