package com.cookandroid.s2_archiving

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import org.w3c.dom.Text
import java.util.*

class ProfileAdapter(val profileList: ArrayList<Profiles>) : RecyclerView.Adapter<ProfileAdapter.CustomViewHolder>(){
    

    //plug로 연결될 화면 (list_item.xml)을 연결시키는 작업
    //inflate는 붙이는 작업이라고 생각하면 된
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileAdapter.CustomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return CustomViewHolder(view).apply {
            //리사이클러뷰 아이템 클릭했을 때
            itemView.setOnClickListener {
                val curPos : Int = adapterPosition
                val profile: Profiles = profileList.get(curPos)
                Toast.makeText(parent.context, "이름 : ${profile.name}", Toast.LENGTH_SHORT).show()
            }
        }
    }


    //list들에 대한 총 개수를 적어주는 부
    override fun getItemCount(): Int {
        return profileList.size
    }

    //view에 대해서 데이터들을 안정적으로 match시켜준
    override fun onBindViewHolder(holder: ProfileAdapter.CustomViewHolder, position: Int) {
        holder.gender.setImageResource(profileList.get(position).gender)
        holder.name.text=profileList.get(position).name
       // holder.age.text=profileList.get(position).age.toString()
       // holder.job.text=profileList.get(position).job
        holder.star.setImageResource(profileList.get(position).star)
    }



    class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {  //view에 대한 것을 꽉 잡아주는 역할을 한
        val gender = itemView.findViewById<ImageView>(R.id.ivProfile)  //성별
        val name = itemView.findViewById<TextView>(R.id.tvName)    //이름
       // val age = itemView.findViewById<TextView>(R.id.tvAge)    //나이
       // val job = itemView.findViewById<TextView>(R.id.tvJob)    //직
        val star = itemView.findViewById<ImageView>(R.id.ivStar)
    }

}