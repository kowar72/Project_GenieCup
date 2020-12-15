package com.example.projectaricup

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import kotlinx.android.synthetic.main.cal_grid.*

class CalFragment : Fragment() {

    private val caladapter = CalViewAdapter()
    private var manager = CalManager()
    var YEAR : Int = 0
    var MONTH : Int = 0
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.cal_grid, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        manager.registerDBHelper(CalendarDBHelper(activity, "caldb.db", null, 1))
        YEAR = arguments!!.getInt("YEAR")
        MONTH = arguments!!.getInt("MONTH")
        var list : ArrayList<DayItem> = manager.GetCalArray(YEAR!!, MONTH!!)
        var CalendarList = manager.getDataList(list)
        CalendarList = manager.predictNextPeriod(CalendarList)
        caladapter.setDateList(CalendarList)
        caladapter.setItemClickListener(object : CalViewAdapter.ItemClickListener{
            override fun onClick(view: View, position: Int) {
                (parentFragment as Frag_main).today(list.get(position))
            }
       })
        caladapter.setItemLongClickListener(object : CalViewAdapter.ItemLongClickListener{
            override fun onLongkClick(view: View, position: Int) {
                (parentFragment as Frag_main).changeBloodAmountClick(list.get(position))
            }
        })



        cal_grid_recycle.apply{
            layoutManager = GridLayoutManager(this.context, 7)
            adapter = caladapter
        }
    }


}