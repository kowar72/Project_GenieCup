package com.example.projectaricup

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.frag_config.*
import kotlinx.android.synthetic.main.frag_report.*
import java.util.*

class Frag_config : Fragment() {

    private lateinit var nonmemberlayout : ConstraintLayout
    private lateinit var memberlayout : ConstraintLayout
    private lateinit var swDataBackup : Switch
    private lateinit var swHaveChild : Switch
    private lateinit var swCycleAlarm : Switch
    private lateinit var swOvulationAlarm : Switch
    private lateinit var logoutBtn : TextView
    private lateinit var loginBtn : ImageButton
    private lateinit var memberMenu1 : LinearLayout
    private lateinit var memberMenu2 : LinearLayout
    private lateinit var cycleAlarmperiod : TextView
    private lateinit var ovulationAlarmperiod : TextView
    private var CHANNEL_ID = ""
    private lateinit var notificationManager : NotificationManager
    private lateinit var alarmTime : Calendar
    private var PARAM_OVULATION : Int = 2
    private var PARAM_CYCLE : Int = 1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.frag_config, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initfrag()
        createNotificationChannel()

    }

    fun initfrag(){
        nonmemberlayout = config_nonmember
        memberlayout = config_member
        swDataBackup = data_switch
        swHaveChild = havechild_switch
        swCycleAlarm = cyclealarm_switch
        cycleAlarmperiod = cyclealarmrate
        ovulationAlarmperiod = ovulationalarmrate
        swCycleAlarm.setOnCheckedChangeListener { compoundButton, isOn ->
            if(isOn){
                Toast.makeText(activity, "생리 시작 3일 전부터 알람이 발송됩니다.",Toast.LENGTH_SHORT).show()
                cycleAlarmperiod.visibility = View.VISIBLE
                var cal : Calendar = Calendar.getInstance()
                cal.add(Calendar.SECOND, 300)
                notificationManager.notify(0, notificationBuild(cal, PARAM_CYCLE).build())
            }else{
                Toast.makeText(activity, "알람이 해제되었습니다.",Toast.LENGTH_SHORT).show()
                cycleAlarmperiod.visibility = View.INVISIBLE

            }
        }
        swOvulationAlarm = ovulationalarm_switch
        swOvulationAlarm.setOnCheckedChangeListener { compoundButton, isOn ->
            if(isOn){
                Toast.makeText(activity, "배란 시작 3일 전부터 알람이 발송됩니다.",Toast.LENGTH_SHORT).show()
                ovulationAlarmperiod.visibility = View.VISIBLE
                var cal : Calendar = Calendar.getInstance()
                cal.add(Calendar.SECOND, 300)
                notificationManager.notify(0, notificationBuild(cal, PARAM_OVULATION).build())
            }else{
                Toast.makeText(activity, "알람이 해제되었습니다.",Toast.LENGTH_SHORT).show()
                ovulationAlarmperiod.visibility = View.INVISIBLE
            }
        }
        logoutBtn = logoutbtn
        loginBtn = config_login_btn
        memberMenu1 = linear1
        memberMenu2 = linear5
        loginBtn.setOnClickListener {
            nonmemberlayout.visibility = View.INVISIBLE
            memberlayout.visibility = View.VISIBLE
            memberMenu1.visibility = View.VISIBLE
            memberMenu2.visibility = View.VISIBLE
        }
        logoutBtn.setOnClickListener {
            nonmemberlayout.visibility = View.VISIBLE
            memberlayout.visibility = View.INVISIBLE
            memberMenu1.visibility = View.INVISIBLE
            memberMenu2.visibility = View.INVISIBLE
        }
    }

    fun notificationBuild(cal : Calendar, param : Int) : NotificationCompat.Builder{
        var str_title = ""
        var str_content = ""
        if(param == PARAM_CYCLE){
            str_title = "생리 예정일 알림"
            str_content = "생리 시작 3일전 입니다."
        }else if(param == PARAM_OVULATION){
            str_title = "배란 시작일 알림"
            str_content = "배란 시작 3일전 입니다."
        }
        var builder = NotificationCompat.Builder(requireActivity(), CHANNEL_ID)
            .setSmallIcon(R.drawable.genie_app_icon)
            .setContentTitle(str_title)
            .setContentText(str_content)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setWhen(cal.timeInMillis)

        return builder
    }

    private fun createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val name = getString(R.string.channel0)
            val descriptionText = getString(R.string.chanel0description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            notificationManager = activity?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}