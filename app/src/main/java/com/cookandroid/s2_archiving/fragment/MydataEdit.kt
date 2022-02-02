package com.cookandroid.s2_archiving.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.cookandroid.s2_archiving.R

class MydataEdit : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? { //edit_mydata xml파일이랑 연결
        val view = inflater.inflate(R.layout.edit_mydata,container, false)
        return view
    }
}