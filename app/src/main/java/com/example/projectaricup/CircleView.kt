package com.example.projectaricup

import android.content.Context
import android.content.res.Resources
import android.content.res.TypedArray
import android.graphics.*
import android.graphics.Color.WHITE
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import kotlin.math.*

class CircleView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet
) : View(context, attrs){

    val MIN_VALUE : Int = 0;
    val MAX_VALUE : Int = 100;
    var isOn : Boolean = false
    var isexpected : Boolean = false

    private var center : PointF = PointF()
    private var circleRect : RectF = RectF()
    private var segment : Path = Path()
    private var segment2 : Path = Path()
    private var strokePaint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var fillPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var radius : Int = 0

    private var fillColor : Int = 0
    private var strokeColor : Int = 0
    private var strokeWidth : Float = 0f
    private var value : Int = 0
    private var mtext : String = ""


    private lateinit var pText : Paint

    init{
        
        var a : TypedArray = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.CircleView,
            0, 0
        )
        fillColor = a.getColor(R.styleable.CircleView_android_fillColor, Color.WHITE)
        strokeColor = a.getColor(R.styleable.CircleView_strokeColor, Color.WHITE)
        strokeWidth = a.getFloat(R.styleable.CircleView_strokewidth, 1f)
        value = a.getInteger(R.styleable.CircleView_value,0)
        adjustValue(value)

        a.recycle()

        fillPaint.setColor(fillColor)
        strokePaint.setColor(strokeColor)
        strokePaint.strokeWidth = strokeWidth
        strokePaint.style = Paint.Style.STROKE

        pText = Paint(Paint.ANTI_ALIAS_FLAG)
        pText.textSize = 50f
        pText.setColor(context.getColor(R.color.warm_grey))
        pText.textAlign = Paint.Align.CENTER

        fillPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.XOR)
        setLayerType(View.LAYER_TYPE_SOFTWARE, null)
    }

    fun setFillColor(fillColor : Int){
        this.strokeColor = strokeColor
        fillPaint.setColor(fillColor)
        invalidate()
    }

    fun getFillColor() : Int {
        return fillColor
    }

    fun setStrokeColor(strokeColor : Int){
        this.strokeColor = strokeColor
        strokePaint.setColor(strokeColor)
        invalidate()
    }

    fun getStrokeColor() : Int{
        return strokeColor
    }

    fun setStrokeWidth(strokeWidth : Float){
        this.strokeWidth = strokeWidth
        strokePaint.strokeWidth = strokeWidth
        invalidate()
    }

    fun getStrokeWidth() : Float {
        return strokeWidth
    }

    fun setValue(Value : Int){
        adjustValue(Value)
        setPaths()
        invalidate()
    }

    fun getValue() : Int{
        return value
    }

    private fun adjustValue(Value : Int){
        this.value = min(MAX_VALUE, max(MIN_VALUE, Value))
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        center.x = (width / 2).toFloat()
        center.y = (height / 2).toFloat()
        radius = min(width, height) / 2 - strokeWidth.toInt()
        circleRect.set(center.x - radius, center.y - radius, center.x + radius, center.y + radius)
        setPaths()
    }

    private fun setPaths(){
        var y : Float = center.y + radius - (2 * radius * value / 100 - 1).toFloat()
        var x : Float = center.x - sqrt(radius.toDouble().pow(2.toDouble()) - Math.pow((y - center.y).toDouble(), 2.toDouble())).toFloat()

        var angle : Float = Math.toDegrees(atan((center.y - y) / (x - center.x)).toDouble()).toFloat()
        var startAngle : Float = 180 - angle
        var sweepAngle = 2 * angle - 180

        segment.rewind()
        segment.addArc(circleRect, startAngle, sweepAngle)
        segment.close()

    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        var textX : Float = (canvas!!.width / 2f)
        var textY : Float = ((canvas!!.height / 2) - ((pText.descent() + pText.ascent())/2))

        if(isOn) {
            canvas!!.drawText(mtext, textX, textY,pText)
            canvas!!.drawPath(segment, fillPaint)

        }else{
            canvas!!.drawText(mtext, textX, textY,pText)
        }

    }

    fun setText(s : String){
        this.mtext = s
        invalidate()
    }

    fun getText() : String{
        return this.mtext
    }

    fun setTextColor(textcolor : Int){
        this.pText.color = textcolor
        invalidate()
    }







}




