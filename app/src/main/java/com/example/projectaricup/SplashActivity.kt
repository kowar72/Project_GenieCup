package com.example.projectaricup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper

class SplashActivity : AppCompatActivity() {

    val SPLASH_VIEW_TIME: Long = 1500 // 스플래시 화면 표시 시간 (ms)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Looper.myLooper()?.let {
            Handler().postDelayed({
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent) //로그인 액티비티로 이동
                finish()
            },SPLASH_VIEW_TIME)
        }
    }
}