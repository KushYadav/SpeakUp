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
import com.example.hp.speakup.activities.ViewAdministrationPostActivity
import com.example.hp.speakup.models.AdministrationPost
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_administration.view.*
import kotlinx.android.synthetic.main.rv_items_administration_posts.view.*
import kotlinx.android.synthetic.main.rv_items_council_posts.view.*

class AdministrationPostAdapter(val posts: ArrayList<AdministrationPost>, val context: Context) : RecyclerView.Adapter<AdministrationPostAdapter.AdministrationPostViewHolder>() {

    override fun onBindViewHolder(holder: AdministrationPostViewHolder, position: Int) {
        holder.bindData(posts[position])

        val mAuth = FirebaseAuth.getInstance()
        val mDatabaseReference = FirebaseDatabase.getInstance()

        val userID = mAuth.currentUser?.uid
        if (posts[position].username == userID) {
            holder.itemView.setOnLongClickListener {
                val alertDialog = AlertDialog.Builder(context)
                alertDialog.setMessage("Want to delete this post")
                alertDialog.setPositiveButton("OK", object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        val id = posts[position].id
                        mDatabaseReference.getReference("AdministrationPosts").child(id).setValue(null)
                        mDatabaseReference.getReference("Comments").child(id).setValue(null)
                        mDatabaseReference.getReference("AdministrationPostLikes").child(id).setValue(null)
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

        holder.itemView.setOnClickListener {
            val intent = Intent(context, ViewAdministrationPostActivity::class.java)
            intent.putExtra("id", posts[position].id)
            context.startActivity(intent)
        }

        holder.itemView.btnFollowAdministration.setOnClickListener {
            val mDatabaseReference = FirebaseDatabase.getInstance().getReference("AdministrationPostLikes").child(posts[position].id)
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdministrationPostViewHolder {
        val li = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val itemView = li.inflate(R.layout.rv_items_administration_posts, parent, false)
        return AdministrationPostViewHolder(itemView)
    }

    override fun getItemCount(): Int = posts.size

    class AdministrationPostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindData(administrationPosts: AdministrationPost) {
            val userID = FirebaseAuth.getInstance().currentUser?.uid

            itemView.tvPostAdministration.text = administrationPosts.post
            itemView.tvTimeAdministration.text = administrationPosts.time
            itemView.tvDateAdministration.text = administrationPosts.date
            itemView.tvUsernameAdministration.text = administrationPosts.username

            val ref = FirebaseDatabase.getInstance().getReference("AdministrationPostLikes").child(administrationPosts.id)
            ref.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    when (dataSnapshot.child("like").child(userID.toString()).child("liked").value.toString()) {
                        "true" -> itemView.btnFollowAdministration.background.setTint(itemView.resources.getColor(R.color.DarkRed))
                        "false" -> itemView.btnFollowAdministration.background.setTint(itemView.resources.getColor(R.color.Grey))
                    }
                    itemView.tvFollowersAdministration.text = dataSnapshot.child("likes").child("count").value.toString()
                }
            })
            FirebaseDatabase.getInstance().getReference("Comments").addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    itemView.tvCountAdministrationComments.text = dataSnapshot.child(administrationPosts.id).children.count().toString()
                }
            })
        }
    }
}