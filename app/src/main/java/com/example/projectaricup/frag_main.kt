package com.example.projectaricup

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.amitshekhar.DebugDB
import kotlinx.android.synthetic.main.frag_main.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class Frag_main : Fragment() {

    private lateinit var viewPager : ViewPager2
    private lateinit var float_btn : ImageButton
    private lateinit var cal_activity : FrameLayout
    private lateinit var cal_layout : LinearLayout
    private lateinit var bloodStartbtn : ImageButton
    private lateinit var bloodEndbtn : ImageButton
    private var current_page = 60
    private val center_page = 60
    private lateinit var DAY_SELECT : DayItem
    private var selected : Boolean = false
    private lateinit var manager : CalManager
    private lateinit var transaction : FragmentTransaction
    private lateinit var fraglist : ArrayList<Fragment>
    private lateinit var format : SimpleDateFormat
    private lateinit var display_month : String
    private lateinit var Current : Calendar
    private lateinit var adapter: ViewPagerAdapter
    private lateinit var LONG_SELECT : DayItem
    private var bamount : Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.frag_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        setYearMonth(viewPager.currentItem)
        bloodStartbtn.setOnClickListener {
            Addperiod()
        }
        bloodEndbtn.setOnClickListener {
            DeletePeriod()
        }
        DebugDB.getAddressLog()
        seekBar2.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                seekbarText.text = "혈량 : $p1 ml"
                bamount = p1
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                bamount = p0!!.progress
            }
        })
        bloodchange.setOnClickListener {
            changeBloodAmount(LONG_SELECT, bamount)
        }

    }

    fun changeBloodAmountClick(d : DayItem){
        if(d.dayCount != 0){
            LONG_SELECT = d
            (activity as MainCalActivity).setfront(1)
            blur_layout2.visibility = View.VISIBLE
            seekBar2.progress = d.getQuantityBlood()
            seekbarText.text = "혈량 : ${seekBar2.progress} ml"
        }

    }

    fun changeBloodAmount(d : DayItem, amount : Int){
        manager.updateBloodAmount(d, amount)
        (activity as MainCalActivity).setfront(0)
        blur_layout2.visibility = View.INVISIBLE
        Toast.makeText(activity,"혈량 정보가 업데이트 되었습니다", Toast.LENGTH_SHORT).show()
        updateFragment()
    }


    fun today(day : DayItem){
        selected = true
        DAY_SELECT = day
        select_display.setText("${day.month}월 ${day.date}일 ${manager.getDayInfo(day)}")
        displayInfo(day)
    }

    private fun displayInfo(day : DayItem){
        if(day.isBeforeOvulutionday || day.isAfterOvulutionday || day.isOvulutionday){
            display_info.setText("임신 가능성이 높습니다.")
        }else if(day.dayCount != 0){
            display_info.setText(manager.displayCycleInfo(day))
        }else if(!day.isCycleExpected){
            display_info.setText("임신 가능성이 낮습니다.")
        } else{
            display_info.setText("")
        }

    }

    private fun dayItemToDate(d : DayItem) : Int {
        return d.year * 10000 + d.month * 100 + d.date
    }

    private fun calendarToDate(c : Calendar) : Int{
        return c.get(Calendar.YEAR) * 10000 + (c.get(Calendar.MONTH)+1) * 100 + c.get(Calendar.DATE)
    }

    private fun FloatingButtonAction(n : Int){  // 플로팅 버튼 액션 관리
        when(n){
            0->{    // 플로팅 버튼 클로징 액션
                blur_layout.visibility = View.INVISIBLE
                float_btn.setImageResource(R.drawable.floating_action)
                (activity as MainCalActivity).setfront(0)
                ObjectAnimator.ofFloat(bloodEndbtn,"translationY", 0f).apply { start() }
                ObjectAnimator.ofFloat(bloodStartbtn,"translationY", 0f).apply { start() }
                bloodEndbtn.visibility = View.INVISIBLE
                bloodStartbtn.visibility = View.INVISIBLE
            }
            1->{    // 플로팅 버튼 오픈 액션
                blur_layout.visibility = View.VISIBLE
                float_btn.setImageResource(R.drawable.floating_closed)
                (activity as MainCalActivity).setfront(1)
                bloodEndbtn.visibility = View.VISIBLE
                bloodStartbtn.visibility = View.VISIBLE
                ObjectAnimator.ofFloat(bloodEndbtn,"translationY", -200f).apply { start() }
                ObjectAnimator.ofFloat(bloodStartbtn,"translationY", -400f).apply { start() }
            }
        }
    }

    private fun Addperiod(){
        if(!selected){
            FloatingButtonAction(0)
            Toast.makeText(activity, "먼저 날짜를 선택해주세요",Toast.LENGTH_SHORT).show()
        }else{
            FloatingButtonAction(0)
            if(manager.betweenDay(calendarToDate(Calendar.getInstance()),dayItemToDate(DAY_SELECT)) > 0){
                Toast.makeText(activity,"미래의 날짜를 선택할 수 없습니다",Toast.LENGTH_SHORT).show()
            }else{
                if(manager.insertDayitem(DAY_SELECT)){
                    Toast.makeText(activity,"주기 추가 완료되었습니다",Toast.LENGTH_SHORT).show()
                    updateFragment()

                }else{
                    Toast.makeText(activity,"이미 등록된 날짜입니다",Toast.LENGTH_SHORT).show()
                }
            }


        }
    }

    private fun DeletePeriod(){
        if(!selected){
            FloatingButtonAction(0)
            Toast.makeText(activity, "먼저 날짜를 선택해주세요",Toast.LENGTH_SHORT).show()
        }else{
            FloatingButtonAction(0)
            if(DAY_SELECT.getdayCount() == 0){  //주기가 아닌날을 선택 -> 5일 내에 가까운 주기가 있는지 탐색하고 연결
                var nearest = manager.findNearCycle(DAY_SELECT, 5)
                if(nearest == null){
                    Toast.makeText(activity, "해당 날짜와 인접한 주기가 없습니다",Toast.LENGTH_SHORT).show()
                }else{
                    manager.stretchCycle(DAY_SELECT, nearest)
                    updateFragment()
                    Toast.makeText(activity, "주기가 업데이트 되었습니다",Toast.LENGTH_SHORT).show()
                }
            }else if(DAY_SELECT.getdayCount() == 1){    //주기 첫날을 선택 -> 주기 전체 삭제
                manager.deleteCycle(DAY_SELECT)
                updateFragment()
                Toast.makeText(activity, "주기가 삭제되었습니다",Toast.LENGTH_SHORT).show()
            }else{  //주기 첫날이 아닌날을 선택 -> 지정된 종료일로 주기 조정
                if(!manager.reduceCycle(DAY_SELECT)){
                    Toast.makeText(activity, "이미 주기의 종료일로 지정되어 있습니다",Toast.LENGTH_SHORT).show()
                }else{
                    updateFragment()
                    Toast.makeText(activity, "생리 종료일이 변경되었습니다",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun init(){

        manager = CalManager()
        manager.registerDBHelper(CalendarDBHelper(activity, "caldb.db", null, 1))
        Current = Calendar.getInstance()
        format = SimpleDateFormat("yyyy. MM")
        display_month = format.format(Current.time)

        viewPager = viewpager_cal
        viewPager.offscreenPageLimit = 2

        blur_layout.isClickable = true
        blur_layout2.isClickable = true
        blur_layout2.setOnClickListener {
            if(blur_layout2.visibility == View.VISIBLE){
                (activity as MainCalActivity).setfront(0)
                blur_layout2.visibility = View.INVISIBLE
            }
        }
        blur_layout.setOnClickListener {
            if(blur_layout.visibility == View.VISIBLE){
                FloatingButtonAction(0)
            }
        }
        bloodEndbtn = btn_bloodEnd
        bloodStartbtn = btn_bloodStart

        float_btn = floating_btn
        float_btn.setOnClickListener {
            if(blur_layout.visibility == View.VISIBLE){
                FloatingButtonAction(0)
            }else{
                FloatingButtonAction(1)
            }

        }
        cal_activity = main_cal_activity
        cal_layout = main_cal_layout

        Current.add(Calendar.MONTH,-60)
        fraglist = arrayListOf<Fragment>()
        for(i in 0..120){
            fraglist.add(CalFragment().apply {
                arguments = Bundle().apply {
                    putInt("YEAR", Current.get(Calendar.YEAR))
                    putInt("MONTH", Current.get(Calendar.MONTH)+1)
                }
            })
            Current.add(Calendar.MONTH,1)
        }

        adapter = ViewPagerAdapter(this, fraglist)
        viewPager.adapter = adapter
        viewPager.setCurrentItem(current_page, false)

        Current = Calendar.getInstance()
        viewPager.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setYearMonth(position)
                current_page = position
            }
        })
    }

    fun setYearMonth(pos : Int){
        Current = Calendar.getInstance()
        if(pos > center_page){
            Current.add(Calendar.MONTH, (pos - center_page))
        }else if(pos < center_page){
            Current.add(Calendar.MONTH, -(center_page - pos))
        }
        display_month = format.format(Current.time)
        current_month.setText(display_month)
    }

    private fun updateFragment(){
        transaction = fragmentManager!!.beginTransaction()
        transaction.detach(this).attach(this).commit()
        selected = false
    }



}





class ViewPagerAdapter(fa: Fragment, private val fragments:ArrayList<Fragment>) : FragmentStateAdapter(fa) {

    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }



}