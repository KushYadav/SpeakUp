package com.example.hp.speakup.fragments


import android.app.AlertDialog
import android.app.ProgressDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar

import com.example.hp.speakup.R
import com.example.hp.speakup.adapters.CouncilPostAdapter
import com.example.hp.speakup.models.CouncilPost
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_my_council_post.*
import kotlinx.android.synthetic.main.fragment_my_council_post.view.*

class MyCouncilPost : Fragment() {

    //    lateinit var pbd: ProgressDialog
    lateinit var mAuth: FirebaseAuth
    lateinit var mDatabaseReference: DatabaseReference

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        activity?.setTitle("My Council Posts")

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_council_post, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val builder = AlertDialog.Builder(view.context)
        builder.setView(R.layout.progress_bar)
        val dialog = builder.create()
        dialog.setCanceledOnTouchOutside(false)
        dialog.window.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
        dialog.show()

        mAuth = FirebaseAuth.getInstance()
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("CouncilPosts")

        mDatabaseReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val uid = mAuth.currentUser?.uid
                val myCouncilPostList = ArrayList<CouncilPost>()
                for (i in dataSnapshot.children) {
                    if (i.child("username").value.toString() == uid) {
                        val a = CouncilPost(i.child("id").value.toString(), i.child("username").value.toString(),
                                i.child("post").value.toString(), i.child("time").value.toString(), i.child("date").value.toString())
                        myCouncilPostList.add(0, a)
                    }
                    view.rvMyCouncilPosts.layoutManager = LinearLayoutManager(view.context)
                    view.rvMyCouncilPosts.adapter = CouncilPostAdapter(myCouncilPostList, view.context)
                    dialog.hide()
                }
            }
        })

    }


}
