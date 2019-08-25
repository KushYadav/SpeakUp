package com.example.hp.speakup.activities

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.example.hp.speakup.R
import com.example.hp.speakup.adapters.CommentsAdapter
import com.example.hp.speakup.models.Comment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_view_administration_post.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ViewAdministrationPostActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_administration_post)

        val id = intent.getStringExtra("id")

        val mAuth = FirebaseAuth.getInstance()
        var mDatabaseReference = FirebaseDatabase.getInstance().getReference("AdministrationPosts").child(id)

        //Show Post

        val userID = mAuth.currentUser?.uid
        mDatabaseReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                tvUsernameViewAdministrationPost.text = dataSnapshot.child("username").value.toString()
                tvPostViewAdministrationPost.text = dataSnapshot.child("post").value.toString()
                tvTimeViewAdministrationPost.text = dataSnapshot.child("time").value.toString()
                tvDateViewAdministrationPost.text = dataSnapshot.child("date").value.toString()
            }
        })

        mDatabaseReference = FirebaseDatabase.getInstance().getReference("AdministrationPostLikes").child(id)
        mDatabaseReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                when (dataSnapshot.child("like").child(userID.toString()).child("liked").value.toString()) {
                    "true" -> btnFollowViewAdministrationPost.background.setTint(resources.getColor(R.color.DarkRed))
                    "false" -> btnFollowViewAdministrationPost.background.setTint(resources.getColor(R.color.Grey))
                }
                tvFollowersViewAdministrationPost.text = dataSnapshot.child("likes").child("count").value.toString()
            }
        })
        //Handle Following & Followers

        btnFollowViewAdministrationPost.setOnClickListener {

            mDatabaseReference = FirebaseDatabase.getInstance().getReference("AdministrationPostLikes").child(id)
            mDatabaseReference.addListenerForSingleValueEvent(
                    object : ValueEventListener {
                        override fun onCancelled(databaseError: DatabaseError) {

                        }

                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            var flag = false
                            for (i in dataSnapshot.child("like").children) {
                                if (i.key.toString() == userID) {
                                    flag = true;
                                }
                            }
                            if (flag) {
                                val isLiked = dataSnapshot.child("like").child(userID.toString()).child("liked").value.toString()
                                val likeCount = Integer.valueOf(dataSnapshot.child("likes").child("count").value.toString())
                                if (isLiked == "false") {
                                    mDatabaseReference.child("like").child(userID.toString()).child("liked").setValue("true")
                                    mDatabaseReference.child("likes").child("count").setValue((likeCount + 1).toString())
                                } else {
                                    mDatabaseReference.child("like").child(userID.toString()).child("liked").setValue("false")
                                    mDatabaseReference.child("likes").child("count").setValue((likeCount - 1).toString())
                                }
                            } else {
                                val likeCount = Integer.valueOf(dataSnapshot.child("likes").child("count").value.toString())
                                mDatabaseReference.child("like").child(userID.toString()).child("liked").setValue("true")
                                mDatabaseReference.child("likes").child("count").setValue((likeCount + 1).toString())
                            }
                        }

                    }
            )
        }

        //Write Comment

        btnCommentViewAdministrationPost.setOnClickListener {
            mDatabaseReference = FirebaseDatabase.getInstance().getReference("Comments").child(id)
            val comment = etCommentViewAdministrationPost.text.toString()
            val calender = Calendar.getInstance()
            val time = SimpleDateFormat("hh:mm aa").format(calender.time.time)
            val date = SimpleDateFormat("dd-MM-yyyy").format(calender.time.time)
            val newID = SimpleDateFormat("yyyyMMddHHmmss").format(calender.time.time)

            mDatabaseReference.child(newID).child("username").setValue(userID)
            mDatabaseReference.child(newID).child("time").setValue(time)
            mDatabaseReference.child(newID).child("date").setValue(date)
            mDatabaseReference.child(newID).child("id").setValue(newID)
            mDatabaseReference.child(newID).child("comment").setValue(comment).addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(this, "Your comment is added Successfully", Toast.LENGTH_SHORT).show()
                    etCommentViewAdministrationPost.setText("")
                    val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
                } else {
                }
            }
            mDatabaseReference = FirebaseDatabase.getInstance().getReference("CommentLikes").child(id).child(newID)
            mDatabaseReference.child("likes").child("count").setValue("0")


        }

        //View Comment

        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Comments").child(id)
        mDatabaseReference.addChildEventListener(object : ChildEventListener {
            val commentsList = ArrayList<Comment>()
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, p1: String?) {
                if (dataSnapshot.children.count() == 5) {
                    val a = Comment(dataSnapshot.child("id").value.toString(), dataSnapshot.child("username").value.toString(),
                            dataSnapshot.child("comment").value.toString(), dataSnapshot.child("time").value.toString(),
                            dataSnapshot.child("date").value.toString())
                    commentsList.add(0, a)
                    rvViewAdministrationPost.adapter = CommentsAdapter(commentsList, this@ViewAdministrationPostActivity, id)
                    rvViewAdministrationPost.layoutManager = LinearLayoutManager(this@ViewAdministrationPostActivity)
                }
            }

            override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {
                if (dataSnapshot.children.count() == 5) {
                    val a = Comment(dataSnapshot.child("id").value.toString(), dataSnapshot.child("username").value.toString(),
                            dataSnapshot.child("comment").value.toString(), dataSnapshot.child("time").value.toString(),
                            dataSnapshot.child("date").value.toString())
                    commentsList.add(0, a)
                    rvViewAdministrationPost.adapter = CommentsAdapter(commentsList, this@ViewAdministrationPostActivity, id)
                    rvViewAdministrationPost.layoutManager = LinearLayoutManager(this@ViewAdministrationPostActivity)
                }
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.children.count() == 5) {
                    for (i in commentsList) {
                        if (i.id == dataSnapshot.key) {
                            commentsList.remove(i)
                            break
                        }
                    }
                    rvViewAdministrationPost.adapter = CommentsAdapter(commentsList, this@ViewAdministrationPostActivity, id)
                    rvViewAdministrationPost.layoutManager = LinearLayoutManager(this@ViewAdministrationPostActivity)
                }
            }
        })
    }
}
