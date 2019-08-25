package com.example.hp.speakup.fragments


import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.hp.speakup.R
import com.example.hp.speakup.activities.AddCouncilPost
import com.example.hp.speakup.adapters.CouncilPostAdapter
import com.example.hp.speakup.models.CouncilPost
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_council.*
import kotlinx.android.synthetic.main.fragment_council.view.*
import java.util.*

class CouncilFragment : Fragment() {

    //    lateinit var pbd: ProgressDialog
    lateinit var mAuth: FirebaseAuth
    lateinit var mDatabaseReference: DatabaseReference

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_council, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fabCouncil.setOnClickListener {
            startActivity(Intent(context, AddCouncilPost::class.java))
        }

        mAuth = FirebaseAuth.getInstance()
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("CouncilPosts")

        mDatabaseReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val myCouncilPostList = ArrayList<CouncilPost>()
                for (i in dataSnapshot.children) {
                    val a = CouncilPost(i.child("id").value.toString(), i.child("username").value.toString(),
                            i.child("post").value.toString(), i.child("time").value.toString(), i.child("date").value.toString())
                    myCouncilPostList.add(0, a)

                    view.rvCouncil.layoutManager = LinearLayoutManager(view.context)
                    view.rvCouncil.adapter = CouncilPostAdapter(myCouncilPostList, view.context)
                }
            }
        })
    }
}
