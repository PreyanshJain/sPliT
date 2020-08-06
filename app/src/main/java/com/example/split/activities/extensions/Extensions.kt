package com.example.split.activities.extensions

import android.content.Context
import android.widget.Toast


fun Context.showToast(message:String , default: Int = Toast.LENGTH_SHORT){
    Toast.makeText(this , message, default).show()
}