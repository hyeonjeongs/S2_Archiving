package com.cookandroid.s2_archiving.fragment

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cookandroid.s2_archiving.*
import com.cookandroid.s2_archiving.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_searchbtn_click.view.*

class SearchbtnClickFragment : Fragment() {

    var fragmentView : View? = null

    lateinit var adapter : RecyclerView.Adapter<PostAdapter.CustomViewHolder>
    lateinit var categoryList: ArrayList<PostData>

    //파이어베이스
    private lateinit var database : FirebaseDatabase
    private lateinit var mDatabaseRef : DatabaseReference //실시간 데이터베이스 연결
    private var mFirebaseAuth: FirebaseAuth? = null
    var uid : String? = null
    var auth : FirebaseAuth? = null

    // xml
    private lateinit var etSearch : EditText
    private lateinit var rvSearchCategory:RecyclerView



    // context
    private lateinit var activity: Activity

    companion object{
        fun newInstance() : SearchbtnClickFragment{
            return SearchbtnClickFragment()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? { //search_clickbtn xml파일이랑 연결
        fragmentView = LayoutInflater.from(this.getActivity()).inflate(R.layout.fragment_searchbtn_click,container, false)

        //파이어베이스 계정, 리얼타임 데이터베이스
        mFirebaseAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance() //파이어베이스 데이터베이스 연동
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Firebase")
        auth = FirebaseAuth.getInstance()
        uid = mFirebaseAuth!!.currentUser!!.uid

        // xml
        etSearch = fragmentView!!.findViewById(R.id.etCategorySearch)
        rvSearchCategory = fragmentView!!.findViewById(R.id.rvSearchCategory)

        categoryList = ArrayList()
        fragmentView?.rvSearchCategory?.layoutManager = GridLayoutManager(this.requireContext(),2)

        return fragmentView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        searchContentData("") // 검색 시작 전에는 모든 게시글 보여주기
        etSearch.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                var searchText: String = etSearch.text.toString()
                searchContentData(searchText)
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })


        adapter = PostAdapter(categoryList,this.requireContext(),this)
        rvSearchCategory?.adapter= adapter
    }

    //검색한 데이터 띄우기(내용 검색)
    private fun searchContentData(searchText : String) {

        if(searchText.isBlank()){
            mDatabaseRef.child("UserPosts").child("${mFirebaseAuth?.currentUser!!.uid}")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        categoryList.clear() //없으면 비어주기
                        for (data: DataSnapshot in snapshot.children) {
                            var post: PostData? = data.getValue(PostData::class.java)
                                categoryList.add(post!!)
                        }
                        adapter.notifyDataSetChanged() //리스트 저장 및 새로고침
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }
                })
        }
        else {
            mDatabaseRef.child("UserPosts").child("${mFirebaseAuth?.currentUser!!.uid}")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        categoryList.clear() //없으면 비어주기
                        for (data: DataSnapshot in snapshot.children) {
                            var post: PostData? = data.getValue(PostData::class.java)
                            if(post!!.postDate.contains(searchText)||post!!.postDateName.contains(searchText)){
                                categoryList.add(post!!)
                            }
                        }
                        adapter.notifyDataSetChanged() //리스트 저장 및 새로고침
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                })


        }
    }


}