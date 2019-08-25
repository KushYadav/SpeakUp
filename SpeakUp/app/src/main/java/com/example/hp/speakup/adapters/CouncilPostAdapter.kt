package com.example.hp.speakup.adapters

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.support.v7.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.hp.speakup.R
import com.example.hp.speakup.activities.ViewCouncilPostActivity
import com.example.hp.speakup.models.CouncilPost
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.rv_items_administration_posts.view.*
import kotlinx.android.synthetic.main.rv_items_council_posts.view.*

class CouncilPostAdapter(val posts: ArrayList<CouncilPost>, val context: Context) : RecyclerView.Adapter<CouncilPostAdapter.CouncilPostViewHolder>() {

    override fun onBindViewHolder(holder: CouncilPostViewHolder, position: Int) {
        holder.bindData(posts[position])

        val mAuth = FirebaseAuth.getInstance()
        var mDatabaseReference = FirebaseDatabase.getInstance()

        val userID = mAuth.currentUser?.uid
        if (posts[position].username == userID) {
            holder.itemView.setOnLongClickListener {
                val alertDialog = AlertDialog.Builder(context)
                alertDialog.setMessage("Want to delete this post")
                alertDialog.setPositiveButton("OK", object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        val id = posts[position].id
                        mDatabaseReference.getReference("CouncilPosts").child(id).setValue(null)
                        mDatabaseReference.getReference("Comments").child(id).setValue(null)
                        mDatabaseReference.getReference("CouncilPostLikes").child(id).setValue(null)
                        mDatabaseReference.getReference("CommentLikes").child(id).setValue(null)
                        Toast.makeText(context, "Post Removed", Toast.LENGTH_LONG).show()
                    }
                })
                alertDialog.setNegativeButton("Cancel", object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        dialog?.cancel()
                    }
                })
                alertDialog.show()
                true
            }
        }

        holder.itemView.setOnClickListener()
        {
            val intent = Intent(context, ViewCouncilPostActivity::class.java)
            intent.putExtra("id", posts[position].id)
            context.startActivity(intent)
        }

        holder.itemView.btnFollowCouncil.setOnClickListener {
            val mDatabaseReference = FirebaseDatabase.getInstance().getReference("CouncilPostLikes").child(posts[position].id)
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
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CouncilPostViewHolder {
        val li = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val itemView = li.inflate(R.layout.rv_items_council_posts, parent, false)
        return CouncilPostViewHolder(itemView)
    }

    override fun getItemCount(): Int = posts.size

    class CouncilPostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindData(councilPosts: CouncilPost) {
            val userID = FirebaseAuth.getInstance().currentUser?.uid

            itemView.tvPostCouncil.text = councilPosts.post
            itemView.tvTimeCouncil.text = councilPosts.time
            itemView.tvDateCouncil.text = councilPosts.date
            itemView.tvUsernameCouncil.text = councilPosts.username

            val ref = FirebaseDatabase.getInstance().getReference("CouncilPostLikes").child(councilPosts.id)
            ref.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    when (dataSnapshot.child("like").child(userID.toString()).child("liked").value.toString()) {
                        "true" -> itemView.btnFollowCouncil.background.setTint(itemView.resources.getColor(R.color.DarkRed))
                        "false" -> itemView.btnFollowCouncil.background.setTint(itemView.resources.getColor(R.color.Grey))
                    }
                    itemView.tvFollowersCouncil.text = dataSnapshot.child("likes").child("count").value.toString()
                }
            })

            FirebaseDatabase.getInstance().getReference("Comments").addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    itemView.tvCountCouncilComments.text = dataSnapshot.child(councilPosts.id).children.count().toString()
                }
            })
        }
    }
}


