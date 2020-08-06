package com.example.split.activities.sign_up

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.split.R
import com.example.split.activities.user_message.NewMessagesActivity
import com.example.split.activities.extensions.showToast
import com.example.split.activities.modles.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_sign_up.*
import java.util.*


class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        back_btn.setOnClickListener {
            finish()
        }
        btn_sign_up_account.setOnClickListener {
            sign_up_progress_bar.visibility = View.VISIBLE
            performRegistration()
        }
        sign_up_profile_image.setOnClickListener {
            Log.d("SignUp", "Profile picture selector")

            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }
    }

    private var selectImageUri: Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            Log.d("SignUp", "Profile picture selected")
            selectImageUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectImageUri)
            sign_up_profile_image.setImageBitmap(bitmap)
        }
    }

    private fun performRegistration() {
        val userName = sign_up_user_name.text.toString()
        val email = sign_up_email.text.toString()
        val mobileNo = sign_up_mobile_no.text.toString()
        val password = sign_up_password.text.toString()

        Log.d("SignUp", "E-mail : $email")
        Log.d("SignUp", "Password : $password")

        if (email.isEmpty() or password.isEmpty() or userName.isEmpty() or mobileNo.isEmpty()) {
            showToast("Please enter all the above details")
            return
        }
        if(mobileNo.length != 10 ){
            return
        }
        if (password.length <8){
            showToast("Password length must be greater then 8 characters", Toast.LENGTH_LONG)
            return
        }
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener

                Log.d("SignUp", "Successfully Created User ${it.result?.user?.uid}")
                storeImageToFirebase()
            }
            .addOnFailureListener {
                Log.d("SignUp", "Failed to Create user: ${it.message}")
                showToast("${it.message}", Toast.LENGTH_LONG)
            }
    }

    private fun storeImageToFirebase() {
        val filename = UUID.randomUUID()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")
        selectImageUri?.let { it ->
            ref.putFile(it)
                .addOnSuccessListener { it ->
                    Log.d("SignUp", "Successfully uploaded ${it.metadata?.path}")

                    ref.downloadUrl.addOnSuccessListener {
                        Log.d("SignUp", "File Location $it")

                        saveUserDataToFirebase(it.toString())
                    }
                }
                .addOnFailureListener {
                    Log.d("SignUp","Fail : ${it.message}")
                }
        }
    }
    private fun saveUserDataToFirebase(profileImageUrl: String){
        val uid = FirebaseAuth.getInstance().uid ?:""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        val user = User(uid ,sign_up_user_name.text.toString(), profileImageUrl, sign_up_mobile_no.text.toString(), sign_up_email.text.toString())
        ref.setValue(user)
            .addOnSuccessListener {
                Log.d("SignUp", "User data successfully saved")

                val intent = Intent(this , NewMessagesActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            .addOnFailureListener {
               Log.d("SignUp","Failed : ${it.message}")
           }
    }
}



