package com.example.projectaricup

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.frag_report.*
import java.util.*
import kotlin.collections.ArrayList

class Frag_report : Fragment() {

    private lateinit var circleImg : ImageView
    private lateinit var manager : CalManager
    private lateinit var graphitems : ArrayList<GraphItem>
    private val graphadapter = GraphAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.frag_report, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        circleImg = rotate_render
        rotate_circle()
        manager = CalManager()
        manager.registerDBHelper(CalendarDBHelper(activity, "caldb.db", null, 1))
        displayMeanCycle()
        displayNextCycle()
        displayRenderView()
        initGraph()
        reportDisplay()
    }

    private fun rotate_circle(){
        var currentDegree = circleImg.rotation
        var ObjectAnim : ObjectAnimator = ObjectAnimator.ofFloat(circleImg, View.ROTATION, currentDegree, currentDegree + 360f)
        ObjectAnim.setDuration(3000)
        ObjectAnim.repeatCount = ValueAnimator.INFINITE
        ObjectAnim.start()
    }

    private fun reportDisplay(){
        report_display.text = manager.getReportString(Calendar.getInstance())
    }

    private fun displayMeanCycle(){
        val cycle = manager.getMeanCycle()
        if(cycle == 0){
            cycle_text.setText("평균주기\n0일")
        }else{
            cycle_text.setText("평균주기\n${cycle}일")
        }

    }


    private fun displayNextCycle(){
        predict_display.text = manager.reportDisplayNextCycle(calendarToDayitem(Calendar.getInstance()))

    }

    private fun displayRenderView(){
        present_text.text = manager.reportdisplayRenderCircleInfo(calendarToDayitem(Calendar.getInstance()))
    }

    private fun initGraph(){
        graphitems = arrayListOf()
        var daylist = manager.getDayList()
        if(daylist == null){

        }else{
            var current_month : Int = 0
            for(i in daylist){
                if(i.month != current_month){
                    graphitems.add(GraphItem(i.year,i.month,i.date,0,true))
                    current_month = i.month
                }
                graphitems.add(GraphItem(i.year,i.month,i.date,i.getQuantityBlood(),false))

            }
            var linearmanager = LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)
            linearmanager.stackFromEnd = true
            graphadapter.setItemList(graphitems)
            graphadapter.setItemClickListener(object : GraphAdapter.ItemClickListener{
                override fun onClick(view: View, position: Int) {
                    var view : GraphViewHolder = bloodgraph.findViewHolderForAdapterPosition(position) as GraphViewHolder
                    view.touchGraph()
                }

            })
            bloodgraph.apply {
                layoutManager = linearmanager
                adapter = graphadapter
            }
            bloodgraph.recycledViewPool.setMaxRecycledViews(0,0)

        }
    }

    private fun calendarToDate(c : Calendar) : Int{
        return c.get(Calendar.YEAR) * 10000 + (c.get(Calendar.MONTH)+1) * 100 + c.get(Calendar.DATE)
    }

    private fun calendarToDayitem(c : Calendar) : DayItem{
        return DayItem(c.get(Calendar.YEAR), (c.get(Calendar.MONTH)+1), c.get(Calendar.DATE), false)
    }
}