package com.example.hp.speakup.fragments

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.hp.speakup.R
import com.example.hp.speakup.activities.AddAdministrationPost
import com.example.hp.speakup.adapters.AdministrationPostAdapter
import com.example.hp.speakup.models.AdministrationPost
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_administration.*
import kotlinx.android.synthetic.main.fragment_administration.view.*

class AdministrationFragment : Fragment() {

    lateinit var pbd: ProgressDialog
    lateinit var mAuth: FirebaseAuth
    lateinit var mDatabaseReference: DatabaseReference

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_administration, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fabAdministration.setOnClickListener {
            startActivity(Intent(context, AddAdministrationPost::class.java))
        }

        val builder = AlertDialog.Builder(view.context)
        builder.setView(R.layout.progress_bar)
        val dialog = builder.create()
        dialog.setCanceledOnTouchOutside(false)
        dialog.window.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
        dialog.show()

        mAuth = FirebaseAuth.getInstance()
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("AdministrationPosts")

        mDatabaseReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val myAdministrationPostList = ArrayList<AdministrationPost>()
                for (i in dataSnapshot.children) {
                    val a = AdministrationPost(i.child("id").value.toString(), i.child("username").value.toString(),
                            i.child("post").value.toString(), i.child("time").value.toString(), i.child("date").value.toString())
                    myAdministrationPostList.add(0, a)
                }
                view.rvAdministration.layoutManager = LinearLayoutManager(view.context)
                view.rvAdministration.adapter = AdministrationPostAdapter(myAdministrationPostList, view.context)
                dialog.hide()
            }
        })
    }
}