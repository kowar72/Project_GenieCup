package com.example.projectaricup

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.graph_item.view.*

class GraphAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var ListOfitem = ArrayList<GraphItem>()
    private lateinit  var graphviewholder : GraphViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return GraphViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.graph_item, parent, false))
    }

    override fun getItemCount(): Int {
        return ListOfitem.size
    }

    interface ItemClickListener{
        fun onClick(view : View, position: Int)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        graphviewholder = holder as GraphViewHolder
        graphviewholder.bindView(ListOfitem[position])
        holder.itemView.setOnClickListener{
            itemClickListener.onClick(it, position)
        }
    }

    fun setItemList(list : ArrayList<GraphItem>){
        this.ListOfitem = list
        notifyDataSetChanged()
    }




    private lateinit var itemClickListener : GraphAdapter.ItemClickListener

    fun setItemClickListener(itemClickListener: ItemClickListener){
        this.itemClickListener = itemClickListener
    }
}

class GraphViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
    fun bindView(item : GraphItem){
        itemView.graphview.setValue(item.amount)
        if(item.islabel){
            itemView.daylabel.text = "${item.month}월"
            itemView.daylabel.setTextColor(Color.parseColor("#ffffff"))
            itemView.daylabel.setBackgroundResource(R.drawable.graph_label)
            itemView.amount_label.visibility = View.GONE
        }else{
            itemView.daylabel.text = item.date.toString()
            itemView.daylabel.setBackgroundResource(0)
            itemView.amount_label.text = "${item.amount}ml"

        }
    }

    fun touchGraph(){
        if(itemView.amount_label.text != "" && itemView.amount_label.text != "0ml"){
            if(itemView.amount_label.visibility == View.INVISIBLE){
                itemView.amount_label.visibility = View.VISIBLE
            }else{
                itemView.amount_label.visibility = View.INVISIBLE
            }
        }

    }




}