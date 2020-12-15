package com.example.projectaricup

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import kotlinx.android.synthetic.main.graph_item.view.*

class GraphView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet
): View(context, attrs){
    private var graphColor : Int = Color.parseColor("#e54c17")
    private var gPaint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var value : Float = 0f


    init{
        gPaint.setColor(graphColor)
    }

    fun setValue(v : Int){
        this.value = v.toFloat()

        invalidate()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        System.out.println("height: ${canvas!!.height}")
        if(value != 0f){
            var calculate_value = (value * height) / 100f
            canvas!!.drawRect((width/2f)-25, height - calculate_value, (width/2f)+25,height.toFloat(), gPaint)

        }else{
            gPaint.setColor(Color.WHITE)
            //canvas!!.drawRect((width/2f)-25, height.toFloat(), (width/2f)+25,height.toFloat(), gPaint)
        }

    }
}