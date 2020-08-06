package com.example.split.activities.modles

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class User(val uid:String , val username:String , val profileImageUrl: String , val mobile_no :String , val email :String):
    Parcelable {
    constructor() : this("","","","","")
}
