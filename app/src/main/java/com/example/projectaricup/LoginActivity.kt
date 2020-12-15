package com.example.projectaricup

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val non_signup_btn = findViewById<ImageView>(R.id.non_signup_btn)
        non_signup_btn.setOnClickListener({
            Toast.makeText(this@LoginActivity,"비회원으로 시작합니다.",Toast.LENGTH_LONG).show()
            val intent = Intent(this, MainCalActivity::class.java)
            startActivity(intent)
            finish()
        })
    }


}