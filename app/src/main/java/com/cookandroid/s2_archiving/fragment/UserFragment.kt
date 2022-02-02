package com.cookandroid.s2_archiving.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.cookandroid.s2_archiving.FriendAdd
import com.cookandroid.s2_archiving.ModifyAccount
import com.cookandroid.s2_archiving.R
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_user.*

class UserFragment : Fragment() {

    companion object{
        fun newInstance() : UserFragment{
            return UserFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? { //like_fragment xml파일이랑 연결
        val view = inflater.inflate(R.layout.fragment_user,container, false)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // 정보수정에 버튼 리스너
        btnChangeInfo.setOnClickListener {
            activity?.let{
                val intent = Intent(context, ModifyAccount::class.java)
                startActivity(intent)
            }
        }

    }

}