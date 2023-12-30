package com.example.watchit

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.ComponentActivity

class ForgotPasswordActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.forgot_password_main)

        val rememberpasswordtextView: TextView = findViewById(R.id.CreateAccountLinkTextView)
        rememberpasswordtextView.setOnClickListener {
            val intent = Intent(this@ForgotPasswordActivity, MainActivity::class.java)
            startActivity(intent)
        }
    }
}