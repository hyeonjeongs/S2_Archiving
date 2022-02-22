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
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
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
    private var storage : FirebaseStorage? = FirebaseStorage.getInstance()

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
        holder.viewStory.text = viewDataList[position].post
        holder.viewHeart.setImageResource(R.drawable.heart_empty)
        holder.viewEtc.setOnClickListener{   //게시글 수정 (PostActivity로 이동)
            val intent = Intent(context,PostActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            this.context.startActivity(intent)
        }

        holder.viewHeart.setOnClickListener {
            heartEvent(position)
        }

        if (viewDataList.get(position).heart == 1) {
            holder.viewHeart.setImageResource(R.drawable.heart_empty)
        } else if (viewDataList.get(position).heart == 0) {
            holder.viewHeart.setImageResource(R.drawable.heart_full_line)
        }

        holder.viewDelete.setOnClickListener {    //게시글 삭제
            val postId = viewDataList[position].postId
            val friendId = viewDataList[position].postFriendId
            val imageFileName = "IMAGE_" + postId + "_postImage_by"+friendId+".png"

            storage?.reference?.child("${mFirebaseAuth?.currentUser!!.uid}")?.child(imageFileName)?.delete()?.addOnSuccessListener {
                //Log.d("storage","삭제완료")
            }

            mDatabaseRef.ref.child("UserPosts").child("${mFirebaseAuth!!.currentUser!!.uid}").child(postId).removeValue().addOnSuccessListener {
                Toast.makeText(context,"게시글 삭제 완료",Toast.LENGTH_SHORT).show()
            }


        }


    }

    override fun getItemCount(): Int {
        return viewDataList.size
    }

    private fun heartEvent(position: Int){
        var viewdata:PostData = viewDataList.get(position)
        var heart:Int?

        if(viewdata.heart==1){//하트가 비어있는데 클릭된경우
            heart=0
        }else{
            heart=1
        }
        val hashMap: HashMap<String, Any> = HashMap()
        hashMap.put("heart", heart!!)
        mDatabaseRef.child("UserPosts").child("${mFirebaseAuth?.currentUser!!.uid}").child(viewdata.postId).updateChildren(hashMap)
    }

    fun refreshFragment(fragment:Fragment,fragmentManager:FragmentManager){
        var ft:FragmentTransaction = fragmentManager.beginTransaction()
        ft.detach(fragment).attach(fragment).commit()
    }


}