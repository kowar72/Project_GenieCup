package com.example.projectaricup

import android.app.Activity
import android.content.res.ColorStateList
import android.graphics.Color
import android.system.Os.bind
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import kotlinx.android.synthetic.main.test_item.view.*

class CalViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var ListOfDate = ArrayList<DayItem>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return CalDayViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.test_item, parent, false))
    }

    interface ItemClickListener{
        fun onClick(view : View, position: Int)
    }
    interface ItemLongClickListener{
        fun onLongkClick(view: View, position: Int)
    }

    private lateinit var itemLongClickListener: ItemLongClickListener

    private lateinit var itemClickListener : ItemClickListener

    fun setItemLongClickListener(itemLongClickListener: ItemLongClickListener){
        this.itemLongClickListener = itemLongClickListener
    }

    fun setItemClickListener(itemClickListener: ItemClickListener){
        this.itemClickListener = itemClickListener
    }

    override fun getItemCount(): Int = ListOfDate.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val DateViewHolder = holder as CalDayViewHolder
        DateViewHolder.bindView(ListOfDate[position])
        holder.itemView.setOnClickListener{
            itemClickListener.onClick(it, position)
        }
        holder.itemView.setOnLongClickListener(View.OnLongClickListener {
            itemLongClickListener.onLongkClick(it, position)
            return@OnLongClickListener true
        })

        val layoutParams = holder.itemView.layoutParams
        layoutParams.height = 130
        holder.itemView.requestLayout()

    }
    fun setDateList(listofdate : ArrayList<DayItem>){
        this.ListOfDate = listofdate
        notifyDataSetChanged()
    }
}

class CalDayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {



    fun bindView(dayitem : DayItem) {
        itemView.circleView.setText(dayitem.date.toString())
        if(!dayitem.isthismonth){
            itemView.circleView.setTextColor(Color.parseColor("#e0e0e0"))
        }
        if(dayitem.dayCount != 0){
            itemView.circleView.isOn = true
            itemView.circleImg.setImageResource(R.drawable.back_circle)
            itemView.circleImg.visibility = View.VISIBLE
            itemView.circleView.setTextColor(Color.parseColor("#bf211d"))
            if(dayitem.getQuantityBlood() != 0){
                itemView.circleView.setValue(dayitem.getQuantityBlood())
            }
        }else{
            if(dayitem.isBeforeOvulutionday || dayitem.isAfterOvulutionday){
                itemView.circleView.setTextColor(Color.parseColor("#85c7a9"))
            }
            if(dayitem.isOvulutionday){
                itemView.circleView.setTextColor(Color.parseColor("#4b856b"))
            }
            if(dayitem.isCycleExpected){
                itemView.circleView.setTextColor(Color.parseColor("#ffc6c5"))
                itemView.circleImg.setImageResource(R.drawable.dotted_circle)
            }
        }

    }

}