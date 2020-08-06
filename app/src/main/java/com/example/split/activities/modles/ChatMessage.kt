package com.example.split.activities.modles

class ChatMessage(val id :String, val toUid : String, val fromUid :String , val text : String , val timestamp: Long){
    constructor() : this("","","","",-1)
}