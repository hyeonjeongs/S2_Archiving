package com.cookandroid.s2_archiving

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.lang.reflect.Method

class ViewAdapter(val viewDataList: ArrayList<PostData>, val context: Context) : RecyclerView.Adapter<ViewAdapter.CustomViewHolder>() {

    private var mFirebaseAuth: FirebaseAuth? = FirebaseAuth.getInstance() //파이어베이스 인증
    private var mDatabaseRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("Firebase")//실시간 데이터베이스

    private var storage : FirebaseStorage? = FirebaseStorage.getInstance()
    private lateinit var activitys : Activity
    // flag
    private var flag:Int =0

    inner class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val viewImage = itemView.findViewById<ImageView>(R.id.view_img)
        val viewDate = itemView.findViewById<TextView>(R.id.view_date)
        val viewSpecial = itemView.findViewById<TextView>(R.id.view_special)
        val viewHeart = itemView.findViewById<ImageView>(R.id.view_heart)
        val viewStory = itemView.findViewById<TextView>(R.id.view_story)
        val viewmenuBtn = itemView.findViewById<Button>(R.id.viewmenubtn)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.view_list, parent, false)
        return CustomViewHolder(view)
    }


    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        if (viewDataList[position].postPhotoUri == "") {
            holder.viewImage.setImageResource(R.drawable.camera_btn)  //수정 필요
        } else {
            Glide.with(holder.itemView)
                .load(viewDataList[position].postPhotoUri)
                .into(holder.viewImage)
            flag = 1
        }
        holder.viewDate.text = viewDataList[position].postDate
        holder.viewSpecial.text = viewDataList[position].postDateName
        holder.viewStory.text = viewDataList[position].post

        if (viewDataList[position].heart == 1) {
            holder.viewHeart.setImageResource(R.drawable.heart_empty)
        } else if (viewDataList[position].heart == 0) {
            holder.viewHeart.setImageResource(R.drawable.heart_full)
        }


        holder.viewHeart.setOnClickListener {
            heartEvent(position)
        }


        //수정, 삭제 메뉴 클릭시 -> 팝업창 띄우기
        holder.viewmenuBtn.setOnClickListener {
            var views = holder.viewmenuBtn
            //showPopup(views,position)
            val popup = PopupMenu(context.applicationContext,views)
            popup.menuInflater.inflate(R.menu.showmenu,popup.menu)

            popup.setOnMenuItemClickListener { m ->
                when(m.itemId){
                    R.id.revise -> {
                        val intent = Intent(context,EditPostActivity::class.java)
                        intent.putExtra("friend_id",viewDataList[position].postFriendId)
                        intent.putExtra("post_id",viewDataList[position].postId)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        this.context.startActivity(intent)
                        true
                    }
                    R.id.delete -> {
                        val postId = viewDataList[position].postId
                        val friendId = viewDataList[position].postFriendId
                        val imageFileName = "IMAGE_" + postId + "_postImage_by"+friendId+".png"

                        if(flag==1){ // 만약 이미지를 포함해서 저장했다면
                            storage?.reference?.child("${mFirebaseAuth?.currentUser!!.uid}")?.child(imageFileName)?.delete()?.addOnSuccessListener {
                                Log.d("storage", "이미지 삭제완료")
                            }
                        }

                        mDatabaseRef.child("UserPosts").child("${mFirebaseAuth!!.currentUser!!.uid}").child(postId).removeValue().addOnSuccessListener {
                            Toast.makeText(context,"게시글 삭제 완료",Toast.LENGTH_SHORT).show()
                        }
                        true
                    }
                    else -> false
                }
            }

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                popup.setForceShowIcon(true)
            } else{
                try{
                    val fields = popup.javaClass.declaredFields
                    for (field in fields){
                        if ("mPopup" == field.name){
                            field.isAccessible = true
                            val menuPopupHelper = field[popup]
                            val classPopupHelper = Class.forName(menuPopupHelper.javaClass.name)
                            val setForceIcon: Method = classPopupHelper.getMethod(
                                "setForceShowIcon",
                                Boolean::class.javaPrimitiveType
                            )
                            setForceIcon.invoke(menuPopupHelper, true)
                            break
                        }
                    }
                } catch(e:Exception){
                    e.printStackTrace()
                }
            }

            popup.show()
        }


    }

    override fun getItemCount(): Int {
        return viewDataList.size
    }

    private fun heartEvent(position: Int){
        var viewdata:PostData = viewDataList[position]
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