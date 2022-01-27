package com.cookandroid.s2_archiving.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cookandroid.s2_archiving.*
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {
    //정적으로 사용되는 부분
    companion object {
        fun newInstance() : HomeFragment{
            return HomeFragment()
        }
    }
<<<<<<< HEAD

    //메모리에 올라갔을때
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    //프레그먼트를 안고 있는 엑티비티에 붙었을 때
    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    //뷰가 생성되었을때
    //프레그먼트와 레이아웃 연결시켜줌
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home,container,false) //홈프레그먼트 xml파일이랑 연결

        return view
    }
=======
>>>>>>> 978e629 (navi change)

    //뷰가 생성되었을때
    //프레그먼트와 레이아웃 연결시켜줌
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home,container,false) //홈프레그먼트 xml파일이랑 연결

        return view
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        //친구 추가 버튼에 클릭 리스너 연결
        ivPlus.setOnClickListener {
            activity?.let{
                val intent = Intent(context, FriendAdd::class.java)
                startActivity(intent)
            }
        }

        btnSearch.setOnClickListener {
            activity?.let{
                val intent = Intent(context, ModifyAccount::class.java)
                startActivity(intent)
            }
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //
        // recyclerview 데이터
        val profileList = arrayListOf(
                Profiles(R.drawable.woman, "조윤진", R.drawable.star_empty),
                Profiles(R.drawable.man, "김씨", R.drawable.star_full),
                Profiles(R.drawable.woman, "이땡땡", R.drawable.star_full),
                Profiles(R.drawable.woman, "최씨", R.drawable.star_empty),
                Profiles(R.drawable.man, "박땡땡", R.drawable.star_full),
                Profiles(R.drawable.woman, "신땡땡", R.drawable.star_empty),
                Profiles(R.drawable.man, "윤씨", R.drawable.star_empty),
                Profiles(R.drawable.woman, "권씨", R.drawable.star_empty),
                Profiles(R.drawable.woman, "강씨", R.drawable.star_empty),
                Profiles(R.drawable.man, "서씨", R.drawable.star_full)

        )

        rvProfile.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
        //layoutManager = LinearLayoutManager(this.context)
        rvProfile.setHasFixedSize(true)//리사이클러뷰 성능 강화
        rvProfile.adapter = ProfileAdapter(profileList)


    }


    //메모리에 올라갔을때
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

    }

    //프레그먼트를 안고 있는 엑티비티에 붙었을 때

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

}