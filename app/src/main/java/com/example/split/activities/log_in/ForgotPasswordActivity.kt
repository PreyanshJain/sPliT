package com.example.split.activities.log_in

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.split.R
import com.example.split.activities.extensions.showToast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_forgot_password.*
import kotlinx.android.synthetic.main.activity_log_in.*

class ForgotPasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
        supportActionBar?.title = "Login help"

        forgot_password_send_email.setOnClickListener {
            forgotPassword()
        }
    }

    private fun forgotPassword() {
        val email = forgot_password_edit_text.text.toString()
        if (email.isEmpty()) {
            return
        }
        Firebase.auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    showToast("Email sent")
                }
            }
            .addOnFailureListener {
                showToast("${it.message}", Toast.LENGTH_LONG)
            }
    }
}
