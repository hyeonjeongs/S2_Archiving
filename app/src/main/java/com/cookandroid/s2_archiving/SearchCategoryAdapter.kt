package com.cookandroid.s2_archiving

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SearchCategoryAdapter(val categoryList: ArrayList<CategoryModel>):RecyclerView.Adapter<SearchCategoryAdapter.CustomViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchCategoryAdapter.CustomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.category_search_list,parent,false)
        return CustomViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchCategoryAdapter.CustomViewHolder, position: Int) {
        holder.categoryName.text = categoryList.get(position).categoryName
        holder.categoryDate.text = categoryList.get(position).categoryDate.toString()
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoryDate = itemView.findViewById<TextView>(R.id.tvCategorySearchDate)
        val categoryName = itemView.findViewById<TextView>(R.id.tvCategorySearchName)
    }

}
