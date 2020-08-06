package com.example.split.activities

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.split.R
import com.example.split.activities.modles.ChatMessage
import com.example.split.activities.modles.User
import com.example.split.activities.user_message.NewMessagesActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.chat_log.*
import kotlinx.android.synthetic.main.chat_log_from.view.*
import kotlinx.android.synthetic.main.chat_log_to.view.*

class ChatLogActivity : AppCompatActivity() {
    companion object {
        const val Tag = "ChatLogActivity"
    }
    private var toUser : User? = null
    val adapter = GroupAdapter<GroupieViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chat_log)

        chat_log_recycler_view.adapter = adapter

        toUser = intent.getParcelableExtra<User>("User_Key")
        supportActionBar?.title = toUser!!.username

        listenForMessages()
        chat_log_send_btn.setOnClickListener {
            Log.d(Tag, "Tried to send...")
            val text = chat_log_edit_text.text.toString()
            if (text.isEmpty()) return@setOnClickListener

            performSendMessage()
        }
    }

    private fun listenForMessages() {
        val fromUid = FirebaseAuth.getInstance().uid
        val toUid = toUser?.uid
        val ref = FirebaseDatabase.getInstance().getReference("/user_messages/$fromUid/$toUid")
        ref.addChildEventListener(object : ChildEventListener {

            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java)
                val user  = intent.getParcelableExtra<User>("User_Key")
                if (chatMessage != null) {
                    Log.d(Tag, chatMessage.text)
                    if (chatMessage.fromUid == FirebaseAuth.getInstance().uid ) {
                        val currentUser = NewMessagesActivity.currentUser
                        adapter.add(ChatToItem(chatMessage.text,currentUser!! ))
                    }else{
                        adapter.add(ChatFromItem(chatMessage.text, user))
                    }
                    chat_log_recycler_view.scrollToPosition(adapter.itemCount-1)
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            }
            override fun onChildRemoved(snapshot: DataSnapshot) {
            }

        })
    }

    private fun performSendMessage() {
        val text = chat_log_edit_text.text.toString()
        val fromUid = FirebaseAuth.getInstance().uid
        val user = intent.getParcelableExtra<User>("User_Key")
        val toUid = user.uid
        val fromRef = FirebaseDatabase.getInstance().getReference("/user_messages/$fromUid/$toUid").push()
        val toRef = FirebaseDatabase.getInstance().getReference("/user_messages/$toUid/$fromUid").push()

        if (fromUid == null) return

        val chatMessage =
            ChatMessage(fromRef.key!!, toUid, fromUid, text, System.currentTimeMillis())

        fromRef.setValue(chatMessage)
            .addOnSuccessListener {
                Log.d(Tag, "Successfully saved message ${fromRef.key}")
                chat_log_edit_text.text.clear()
                chat_log_recycler_view.scrollToPosition(adapter.itemCount-1)
            }
        toRef.setValue(chatMessage)

        val latestMessageRef = FirebaseDatabase.getInstance().getReference("Latest_Message/$fromUid/$toUid")
        latestMessageRef.setValue(chatMessage)
        val latestMessageToRef = FirebaseDatabase.getInstance().getReference("Latest_Message/$toUid/$fromUid")
        latestMessageToRef.setValue(chatMessage)
    }

}
class ChatFromItem(private val text: String, private val user: User) : Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        val uri = user.profileImageUrl
        viewHolder.itemView.from_text_view_chat_log.text = text
        val target = viewHolder.itemView.from_profile_image_chat_log
        Picasso.get().load(uri).into(target)
    }

    override fun getLayout(): Int {
        return R.layout.chat_log_from
    }
}

class ChatToItem(private val text: String, val user: User) : Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int){
        val uri = user.profileImageUrl
        viewHolder.itemView.to_text_view_chat_log.text = text
        val target = viewHolder.itemView.to_profile_image_chat_log
        Picasso.get().load(uri).into(target)
    }

    override fun getLayout(): Int {
        return R.layout.chat_log_to
    }
}
