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
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage

class ViewFavoriteAdapter (val viewDataList: ArrayList<PostData>, val context: Context) : RecyclerView.Adapter<ViewFavoriteAdapter.CustomViewHolder>() {

    private var mFirebaseAuth: FirebaseAuth? = FirebaseAuth.getInstance() //파이어베이스 인증
    private var mDatabaseRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("Firebase")//실시간 데이터베이스
    private var storage: FirebaseStorage? = FirebaseStorage.getInstance()

    inner class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val viewFavFriendImg = itemView.findViewById<ImageView>(R.id.view_fav_friendImg)
        val viewFavFriendName = itemView.findViewById<TextView>(R.id.view_fav_friendName)
        val viewFavImage = itemView.findViewById<ImageView>(R.id.view_fav_img)
        val viewFavDate = itemView.findViewById<TextView>(R.id.view_fav_date)
        val viewFavSpecial = itemView.findViewById<TextView>(R.id.view_fav_special)
        val viewFavHeart = itemView.findViewById<ImageView>(R.id.view_fav_heart)
        val viewFavStory = itemView.findViewById<TextView>(R.id.view_fav_story)
        val viewFavEtc = itemView.findViewById<ImageView>(R.id.iv_fav_Etc)
        val viewFavDelete = itemView.findViewById<ImageView>(R.id.iv_fav_Delete)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.view_favorite_list, parent, false)
        return CustomViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewFavoriteAdapter.CustomViewHolder, position: Int){

        mDatabaseRef.child("UserFriends").child("${mFirebaseAuth?.currentUser!!.uid}").child(viewDataList[position].postFriendId)
            .addValueEventListener(object : ValueEventListener {

                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.value == null) { // 널이면 아무것도하지마

                    } else {
                        var friend: FriendData? = snapshot.getValue(FriendData::class.java)
                        holder.viewFavFriendName.text = friend!!.fName

                        // 사진 url 추가 후 load하는 코드 넣을 자리
                        if (friend!!.fImgUri == "") {
                            holder.viewFavFriendImg.setImageResource(R.drawable.man)
                        } else { // userPhotoUri가 있으면 그 사진 로드하기
                            Glide.with(holder.itemView)
                                .load(friend.fImgUri)
                                .into(holder.viewFavFriendImg)
                        }
                    }
                }
            })

        if (viewDataList[position].postPhotoUri == "") {
            holder.viewFavImage.setImageResource(R.drawable.camera_btn)
        } else {
            Glide.with(holder.itemView)
                .load(viewDataList[position].postPhotoUri)
                .into(holder.viewFavImage)
        }

        holder.viewFavDate.text = viewDataList[position].postDate
        holder.viewFavSpecial.text = viewDataList[position].postDateName
        holder.viewFavStory.text = viewDataList[position].post

        if (viewDataList[position].heart == 1) {
            holder.viewFavHeart.setImageResource(R.drawable.heart_empty)
        } else if (viewDataList[position].heart == 0) {
            holder.viewFavHeart.setImageResource(R.drawable.heart_full_line)
        }

        holder.viewFavEtc.setOnClickListener {   //게시글 수정 (PostActivity로 이동)
            val intent = Intent(context, EditPostActivity::class.java)
            intent.putExtra("friend_id", viewDataList[position].postFriendId)
            intent.putExtra("post_id", viewDataList[position].postId)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            this.context.startActivity(intent)
        }

        holder.viewFavHeart.setOnClickListener {
            heartEvent(position)
        }

        holder.viewFavDelete.setOnClickListener {    //게시글 삭제
            val postId = viewDataList[position].postId
            val friendId = viewDataList[position].postFriendId
            val imageFileName = "IMAGE_" + postId + "_postImage_by" + friendId + ".png"

            storage?.reference?.child("${mFirebaseAuth?.currentUser!!.uid}")?.child(imageFileName)
                ?.delete()?.addOnSuccessListener {
                Log.d("storage", "이미지 삭제완료")
            }

            mDatabaseRef.child("UserPosts").child("${mFirebaseAuth!!.currentUser!!.uid}")
                .child(postId).removeValue().addOnSuccessListener {
                Toast.makeText(context, "게시글 삭제 완료", Toast.LENGTH_SHORT).show()
            }

        }

    }

    override fun getItemCount(): Int {
        return viewDataList.size
    }

    private fun heartEvent(position: Int) {
        var viewdata: PostData = viewDataList[position]
        var heart: Int?

        if (viewdata.heart == 1) {//하트가 비어있는데 클릭된경우
            heart = 0
        } else {
            heart = 1
        }
        val hashMap: HashMap<String, Any> = HashMap()
        hashMap.put("heart", heart!!)
        mDatabaseRef.child("UserPosts").child("${mFirebaseAuth?.currentUser!!.uid}")
            .child(viewdata.postId).updateChildren(hashMap)
    }

    fun refreshFragment(fragment: Fragment, fragmentManager: FragmentManager) {
        var ft: FragmentTransaction = fragmentManager.beginTransaction()
        ft.detach(fragment).attach(fragment).commit()
    }


}