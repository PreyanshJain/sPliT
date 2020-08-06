package com.example.split.activities.modles

import com.example.split.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.new_message_activity_row.view.*

class LatestMessageRow(var chatMessage : ChatMessage) : Item<GroupieViewHolder>(){
    var chatPartnerUser : User? = null
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.new_message.text = chatMessage.text
        val chatPartnerId :String
        if (chatMessage.fromUid == FirebaseAuth.getInstance().uid){
            chatPartnerId = chatMessage.toUid
        }else{
            chatPartnerId = chatMessage.fromUid
        }
        val ref = FirebaseDatabase.getInstance().getReference("/users/$chatPartnerId")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                chatPartnerUser = snapshot.getValue(User::class.java)
                viewHolder.itemView.new_message_user_name.text = chatPartnerUser?.username
                val uri = chatPartnerUser?.profileImageUrl
                val target = viewHolder.itemView.new_message_profile_image
                Picasso.get().load(uri).into(target)
            }
            override fun onCancelled(error: DatabaseError) {
            }

        })

    }
    override fun getLayout(): Int {
        return R.layout.new_message_activity_row
    }
}