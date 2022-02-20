package com.cookandroid.s2_archiving

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.cookandroid.s2_archiving.FavoriteAdapter.*
import com.cookandroid.s2_archiving.model.MyFavorite
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class FavoriteAdapter(val context: Context, val fragment_s: Fragment):RecyclerView.Adapter<FavoriteAdapter.CustomViewHolder>() {

    var myFavorite : ArrayList<MyFavorite> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.favorite_list,parent,false)

        return CustomViewHolder(view)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
       /* holder.favoriteimage.setImageResource(FavoriteList.get(position).imageUrl)
        holder.friendname.text = FavoriteList.get(position).friendName
        holder.date.text = FavoriteList.get(position).date.toString()
        holder.special.text = FavoriteList.get(position).specialDay
        holder.heart.setImageResource(FavoriteList.get(position).favoriteImage)*/
    }

    override fun getItemCount(): Int {
        return myFavorite.size
    }

    class CustomViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
        val favoriteimage = itemView.findViewById<ImageView>(R.id.favorite_img)
        val friendname = itemView.findViewById<TextView>(R.id.favorite_friendname)
        val date = itemView.findViewById<TextView>(R.id.favorite_date)
        val special = itemView.findViewById<TextView>(R.id.favorite_special)
        val heart = itemView.findViewById<ImageView>(R.id.favorite_heart)
    }
}