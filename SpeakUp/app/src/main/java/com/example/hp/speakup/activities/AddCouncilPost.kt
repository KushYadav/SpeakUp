package com.example.hp.speakup.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.example.hp.speakup.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_add_council_post.*
import kotlinx.android.synthetic.main.fragment_home.*
import java.text.SimpleDateFormat
import java.util.*

class AddCouncilPost : AppCompatActivity() {

    lateinit var mAuth: FirebaseAuth
    lateinit var mDatabaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_council_post)

        mAuth = FirebaseAuth.getInstance()
        setSupportActionBar(tbAddAdministration)
        supportActionBar?.title = "Add Council Post"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        btnCancelCouncilPost.setOnClickListener {
            onBackPressed()
        }

        btnAddCouncilPost.setOnClickListener {

            val userId = mAuth.currentUser?.uid
            val calender = Calendar.getInstance()
            val time = SimpleDateFormat("hh:mm aa").format(calender.time.time)
            val date = SimpleDateFormat("dd-MM-yyyy").format(calender.time.time)
            val id = SimpleDateFormat("yyyyMMddHHmmss").format(calender.time.time)
            val post = etAddCouncilPost.text.toString()

            mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("CouncilPosts").child(id)
            mDatabaseReference.child("username").setValue(userId)
            mDatabaseReference.child("time").setValue(time)
            mDatabaseReference.child("date").setValue(date)
            mDatabaseReference.child("id").setValue(id)
            mDatabaseReference.child("post").setValue(post).addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(this, "Posted Successfully", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@AddCouncilPost, HomeScreenActivity::class.java)
                    intent.putExtra("currentItem", "1")
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Failed to Post", Toast.LENGTH_SHORT).show()
                }
            }

            mDatabaseReference = FirebaseDatabase.getInstance().getReference("CouncilPostLikes").child(id)
            mDatabaseReference.child("likes").child("count").setValue("0")
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}
