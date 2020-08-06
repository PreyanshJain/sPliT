package com.example.split.activities.log_in

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.split.R
import com.example.split.activities.extensions.showToast
import com.example.split.activities.user_message.NewMessagesActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_log_in.*


class LogInActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)

        back_btn.setOnClickListener {
            finish()
        }

        btn_log_in_account.setOnClickListener {
            log_in_progress_bar.visibility = View.VISIBLE
            checkingRegistration()
        }

        log_in_forgot_password.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }
    }
    private fun checkingRegistration(){
        val email = log_in_email.text.toString()
        val password = log_in_password.text.toString()

        if(email.isEmpty() or password.isEmpty()){
            showToast("Please enter the e-mail and password")
            return
        }

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password)
            .addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener

                Log.d("LogIn", "Successfully LogIn")
                val intent = Intent(this , NewMessagesActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            .addOnFailureListener {
                showToast("Invalid e-mail or password")
                Log.d("LogIn", "E-mail error : ${it.message}")
            }
    }
}