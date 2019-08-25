package com.example.hp.speakup.activities

import android.app.ActivityOptions
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.hp.speakup.R
import com.example.hp.speakup.SignInActivity
import kotlinx.android.synthetic.main.activity_not_connected.*

class NotConnectedActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_not_connected)

        btnTryAgainNotConnected.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent, ActivityOptions.makeCustomAnimation(this, R.anim.abc_fade_in, R.anim.abc_fade_out).toBundle())
        }
    }
}
