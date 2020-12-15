package com.example.projectaricup

import android.graphics.Color
import android.opengl.Visibility
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.frag_main.*
import kotlinx.android.synthetic.main.main_container.*
import java.text.SimpleDateFormat
import java.util.*

class MainCalActivity : AppCompatActivity() {
    private lateinit var bottomBar : ConstraintLayout
    private lateinit var btn_config : ImageButton
    private lateinit var btn_maincal : ImageButton
    private lateinit var btn_report : ImageButton
    private lateinit var fragment_frame : FrameLayout
    private lateinit var transaction: FragmentTransaction
    private lateinit var manager : FragmentManager
    private lateinit var fr1 : Fragment
    private lateinit var fr2 : Fragment
    private lateinit var fr3 : Fragment
    private var btn1 : Boolean = false
    private var btn2 : Boolean = true
    private var btn3 : Boolean = false



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_container)

        btn_config = button_config
        btn_maincal = button_main
        btn_report = button_report
        fragment_frame = frag_zone
        bottomBar = bottm_navibar

        fr1 = Frag_report()
        fr2 = Frag_main()
        fr3 = Frag_config()

        btn_report.setOnClickListener {
            setFragment(1)
        }
        btn_maincal.setOnClickListener {
            setFragment(2)
        }
        btn_config.setOnClickListener {
            setFragment(3)
        }

        setFragment(2)

    }

    fun setfront(i : Int){
        when(i){
            0 ->{
                bottomBar.bringToFront()
            }
            1 ->{
                fragment_frame.bringToFront()
            }

        }

    }




    private fun setFragment(n : Int){
        manager = supportFragmentManager
        transaction = manager.beginTransaction()
        changeBtnImg(n)
        when(n){
            1 -> {
                fr1 = Frag_report()
                transaction.replace(R.id.frag_zone, fr1).commitAllowingStateLoss()
            }
            2->{
                fr2 = Frag_main()
                transaction.replace(R.id.frag_zone, fr2).commitAllowingStateLoss()
            }
            3->{
                fr3 = Frag_config()
                transaction.replace(R.id.frag_zone, fr3).commitAllowingStateLoss()
            }
        }
    }

    private fun changeBtnImg(n : Int){
        when(n){
            1 ->{
                if(!btn1){
                    btn1 = true
                    btn_report.setImageResource(R.drawable.menu_report_on)
                    if(btn2){
                        btn_maincal.setImageResource(R.drawable.menu_main_off)
                        btn2 = false
                    }
                    if(btn3){
                        btn_config.setImageResource(R.drawable.menu_setting_off)
                        btn3 = false
                    }
                }
            }
            2 ->{
                if(!btn2){
                    btn2 = true
                    btn_maincal.setImageResource(R.drawable.menu_main_on)
                    if(btn1){
                        btn_report.setImageResource(R.drawable.menu_report_off)
                        btn1 = false
                    }
                    if(btn3){
                        btn_config.setImageResource(R.drawable.menu_setting_off)
                        btn3 = false
                    }
                }

            }
            3->{
                if(!btn3){
                    btn3 = true
                    btn_config.setImageResource(R.drawable.menu_setting_on)
                    if(btn1){
                        btn_report.setImageResource(R.drawable.menu_report_off)
                        btn1 = false
                    }
                    if(btn2){
                        btn_maincal.setImageResource(R.drawable.menu_main_off)
                        btn2 = false
                    }
                }

            }
        }
    }


}



