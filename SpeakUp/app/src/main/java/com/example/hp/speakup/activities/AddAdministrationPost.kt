package com.example.hp.speakup.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.example.hp.speakup.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_add_administration_post.*
import kotlinx.android.synthetic.main.app_bar_home_screen.*
import java.text.SimpleDateFormat
import java.util.*

class AddAdministrationPost : AppCompatActivity() {

    lateinit var mAuth: FirebaseAuth
    lateinit var mDatabaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_administration_post)


        mAuth = FirebaseAuth.getInstance()
        setSupportActionBar(tbAddAdministration)
        supportActionBar?.title = "Add Administration Post"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        btnCancelAdministrationPost.setOnClickListener {
            onBackPressed()
        }

        btnAddAdministrationPost.setOnClickListener {

            val userId = mAuth.currentUser?.uid
            val calender = Calendar.getInstance()
            val time = SimpleDateFormat("hh:mm aa").format(calender.time.time)
            val date = SimpleDateFormat("dd-MM-yyyy").format(calender.time.time)
            val id = SimpleDateFormat("yyyyMMddHHmmss").format(calender.time.time)
            val post = etAddAdministrationPost.text.toString()

            mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("AdministrationPosts").child(id)
            mDatabaseReference.child("username").setValue(userId)
            mDatabaseReference.child("time").setValue(time)
            mDatabaseReference.child("date").setValue(date)
            mDatabaseReference.child("id").setValue(id)
            mDatabaseReference.child("post").setValue(post).addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(this, "Posted Successfully", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, HomeScreenActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Failed to Post", Toast.LENGTH_SHORT).show()
                }
            }

            mDatabaseReference = FirebaseDatabase.getInstance().getReference("AdministrationPostLikes").child(id)
            mDatabaseReference.child("likes").child("count").setValue("0")
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}
