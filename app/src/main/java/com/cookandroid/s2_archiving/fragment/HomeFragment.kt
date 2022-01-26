package com.cookandroid.s2_archiving.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.cookandroid.s2_archiving.R

class HomeFragment : Fragment() {
    //정적으로 사용되는 부분
    companion object {
        fun newInstance() : HomeFragment{
            return HomeFragment()
        }
    }

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
}