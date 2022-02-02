package com.cookandroid.s2_archiving

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cookandroid.s2_archiving.MyCustomDialog
import com.cookandroid.s2_archiving.R
import com.cookandroid.s2_archiving.adapter.MainRecyclerAdapter
import com.cookandroid.s2_archiving.model.AllCategory
import com.cookandroid.s2_archiving.model.CategoryItem

class ViewActivity : AppCompatActivity() {

    private var mainCategoryRecycler:RecyclerView? =null
    private var mainRecyclerAdapter:MainRecyclerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view)



        //add images 내부
        //첫번째 카테고리 리스트
        val categoryItemList:MutableList<CategoryItem> = ArrayList()
        categoryItemList.add(CategoryItem(1,R.drawable.food))
        categoryItemList.add(CategoryItem(1,R.drawable.food2))
        categoryItemList.add(CategoryItem(1,R.drawable.food3))
        categoryItemList.add(CategoryItem(1,R.drawable.food4))



        //두번째 카테고리 리스트
        val categoryItemList2:MutableList<CategoryItem> = ArrayList()
        categoryItemList2.add(CategoryItem(1,R.drawable.food4))
        categoryItemList2.add(CategoryItem(1,R.drawable.food3))
        categoryItemList2.add(CategoryItem(1,R.drawable.food2))
        categoryItemList2.add(CategoryItem(1,R.drawable.food))



        //add data to model class

        val allCategory:MutableList<AllCategory> = ArrayList()
        allCategory.add(AllCategory("<첫번째글>\n은수가 이번 생일에 손편지를 써줬다.\n 너무 감동받았다.\n 꽃도 트렌디하게 올해의 펜톤 컬러로 줬다. \n너무 예쁘다~!",categoryItemList))
        allCategory.add(AllCategory("<두번째글>\n~~~~!~!~!~~\n!~!~!!!!!!!~~~~~~\n~~~~~~~~~~~~~!!!!!!",categoryItemList2))
        allCategory.add(AllCategory("<세번째글>\n가나다라마바사아자차카타파하\n어쩌구 ~~~~~~",categoryItemList))
        allCategory.add(AllCategory("<네번째글> ~~~~~~~~",categoryItemList2))
        allCategory.add(AllCategory("<다섯번째글> ~~~~~~~~~~~",categoryItemList2))

        setMainCategoryRecycler(allCategory)


    }

    private fun setMainCategoryRecycler(allCategory: List<AllCategory>){

        mainCategoryRecycler=findViewById(R.id.main_recycler)
        val layoutManager: RecyclerView.LayoutManager=LinearLayoutManager(this)
        mainCategoryRecycler!!.layoutManager=layoutManager
        mainRecyclerAdapter= MainRecyclerAdapter(this,allCategory)
        mainCategoryRecycler!!.adapter=mainRecyclerAdapter

    }

    //삭제, 수정 다이얼로그
    fun onDialogBtnClicked(view: View){

        val myCustomDialog = MyCustomDialog(this)

        myCustomDialog.show()
    }

}