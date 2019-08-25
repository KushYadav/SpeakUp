package com.example.hp.speakup.activities

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.example.hp.speakup.R
import com.example.hp.speakup.SignInActivity
import com.example.hp.speakup.fragments.HomeFragment
import com.example.hp.speakup.fragments.MyAdministrationPost
import com.example.hp.speakup.fragments.MyCouncilPost
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_home_screen.*
import kotlinx.android.synthetic.main.app_bar_home_screen.*
import kotlinx.android.synthetic.main.nav_header_home_screen.*
import kotlinx.android.synthetic.main.nav_header_home_screen.view.*

class HomeScreenActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var mAuth: FirebaseAuth;
    private lateinit var mGoogleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_screen)
        setSupportActionBar(toolbar)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        mAuth = FirebaseAuth.getInstance();

        nav_view.getHeaderView(0).tvUsername.text = mAuth.currentUser?.uid

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        val currentItem = intent.getStringExtra("currentItem")
        if (currentItem != null) {
            val bundle = Bundle()
            val homeFragment = HomeFragment()
            bundle.putInt("currentItem", 1)
            homeFragment.arguments = bundle
            supportFragmentManager.beginTransaction().replace(R.id.FragmentContainer, homeFragment).commit()
        } else {
            supportFragmentManager.beginTransaction().replace(R.id.FragmentContainer, HomeFragment()).commit()
        }

        nav_view.setNavigationItemSelectedListener(this)
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.home_screen, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_sign_out -> {
                signOut()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_home -> {
                supportFragmentManager.beginTransaction().replace(R.id.FragmentContainer, HomeFragment()).commit()
            }
            R.id.nav_Administration -> {
                supportFragmentManager.beginTransaction().replace(R.id.FragmentContainer, MyAdministrationPost()).commit()

            }
            R.id.nav_Council -> {
                supportFragmentManager.beginTransaction().replace(R.id.FragmentContainer, MyCouncilPost()).commit()
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }


    fun signOut() {
        mAuth.signOut()
        mGoogleSignInClient.signOut().addOnCompleteListener(this, object : OnCompleteListener<Void> {
            override fun onComplete(task: Task<Void>) {
                Toast.makeText(this@HomeScreenActivity, "Signed out Successfully", Toast.LENGTH_SHORT).show();
                val signOutIntent = Intent(this@HomeScreenActivity, SignInActivity::class.java)
                signOutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                signOutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(signOutIntent)
            }
        })
    }
}
