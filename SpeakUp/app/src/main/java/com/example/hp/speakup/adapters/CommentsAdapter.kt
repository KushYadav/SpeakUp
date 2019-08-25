package com.example.hp.speakup.adapters

import android.content.Context
import android.content.DialogInterface
import android.support.v7.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.hp.speakup.R
import com.example.hp.speakup.models.Comment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_view_administration_post.view.*
import kotlinx.android.synthetic.main.rv_items_comments.view.*

class CommentsAdapter(val commentList: ArrayList<Comment>, val context: Context, val id: String) : RecyclerView.Adapter<CommentsAdapter.CommentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val li = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val itemView = li.inflate(R.layout.rv_items_comments, parent, false)
        return CommentViewHolder(itemView)
    }

    override fun getItemCount(): Int = commentList.size

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.bindDate(commentList[position],id)

        val mAuth = FirebaseAuth.getInstance()
        var mDatabaseReference = FirebaseDatabase.getInstance()

        val userID = mAuth.currentUser?.uid

        if (commentList[position].username == userID) {
            holder.itemView.setOnLongClickListener {
                val alertDialog = AlertDialog.Builder(context)
                alertDialog.setMessage("Want to Delete this comment?")
                alertDialog.setPositiveButton("OK", object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        val commentID = commentList[position].id
                        mDatabaseReference.getReference("Comments").child(id).child(commentID).setValue(null)
                        mDatabaseReference.getReference("CommentLikes").child(id).child(commentID).setValue(null)
                        Toast.makeText(context, "Comment Removed", Toast.LENGTH_LONG).show()
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

        holder.itemView.btnLikeComment.setOnClickListener {
            val mDatabaseReference = FirebaseDatabase.getInstance().getReference("CommentLikes").child(id).child(commentList[position].id)
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

    class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindDate(comments: Comment,id:String) {

            val userID = FirebaseAuth.getInstance().currentUser?.uid
            itemView.tvUsernameComment.text = comments.username
            itemView.tvTimeComment.text = comments.time
            itemView.tvDateComment.text = comments.date
            itemView.tvCommentComment.text = comments.comment

            val ref = FirebaseDatabase.getInstance().getReference("CommentLikes").child(id).child(comments.id)
            ref.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    when (dataSnapshot.child("like").child(userID.toString()).child("liked").value.toString()) {
                        "true" -> itemView.btnLikeComment.background.setTint(itemView.resources.getColor(R.color.DarkRed))
                        "false" -> itemView.btnLikeComment.background.setTint(itemView.resources.getColor(R.color.Grey))
                    }
                    itemView.tvLikesComment.text = dataSnapshot.child("likes").child("count").value.toString()
                }
            })
        }
    }
}