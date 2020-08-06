package com.example.split.activities.user_message

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.split.R
import com.example.split.activities.ChatLogActivity
import com.example.split.activities.modles.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_users.*
import kotlinx.android.synthetic.main.new_message_user_row.view.*

class UsersActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_users)

        supportActionBar?.title = "Select User"
        fetchUsers()
    }
    val key = "User_Key"
    private fun fetchUsers() {
        val ref = FirebaseDatabase.getInstance().getReference("/users")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val adapter = GroupAdapter<GroupieViewHolder>()
                snapshot.children.forEach {
                    val user = it.getValue(User::class.java)
                    val fromUid = FirebaseAuth.getInstance().uid
                    val toUid = user?.uid
                    if (user != null) {
                        if (fromUid != toUid) {
                            adapter.add(UserItem(user))
                        }
                    }
                }
                adapter.setOnItemClickListener { item, view ->
                    val userItem = item as UserItem
                    val intent = Intent(view.context, ChatLogActivity::class.java)
                    intent.putExtra(key,userItem.user)
                    startActivity(intent)
                    finish()
                }
                users_recycler_view.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}

class UserItem(val user: User) : Item<GroupieViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.new_message_user_row
    }
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.user_new_message_user_name.text = user.username
        viewHolder.itemView.user_new_message_email.text = user.email
        Picasso.get().load(user.profileImageUrl)
            .into(viewHolder.itemView.user_new_message_profile_image)
    }

}

