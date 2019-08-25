package com.example.hp.speakup

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.hp.speakup.activities.HomeScreenActivity
import com.example.hp.speakup.activities.NotConnectedActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_sign_in.*

class SignInActivity : AppCompatActivity() {

    private val TAG = "GOOGLESIGNIN"
    private val RC_SIGN_IN = 1;
    private lateinit var mAuth: FirebaseAuth;
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var mGoogleApiClient: GoogleApiClient;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

        mAuth = FirebaseAuth.getInstance();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        btnPlay.setOnLongClickListener {
            val mBuilder = AlertDialog.Builder(this)
            val mView = layoutInflater.inflate(R.layout.dialog_play, null)
            mBuilder.setView(mView)
            val dialog = mBuilder.create()
            dialog.show()
            val etPlay = dialog.findViewById<EditText>(R.id.etPlay)
            val btnPlayOK = dialog.findViewById<Button>(R.id.btnPlayOK)
            btnPlayOK?.setOnClickListener {
                if (etPlay?.text.toString() == resources.getString(R.string.Play)) {
                    dialog.cancel()
                    FirebaseDatabase.getInstance().getReference().setValue(null)
                    Toast.makeText(this@SignInActivity, "Database Cleared", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@SignInActivity, "Wrong Password", Toast.LENGTH_SHORT).show()
                }
            }
            true
        }

        llActivityMain.setOnClickListener {
            signIn()
        }
    }

    private fun signIn() {

        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                Log.e(TAG, "Google Sign In failed", e)
            }

        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount?) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + account!!.getId())
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, object : OnCompleteListener<AuthResult> {
                    override fun onComplete(task: Task<AuthResult>) {
                        if (task.isSuccessful) {
                            Log.d(TAG, "signInWithCredential:success");
                            val signInIntent = Intent(this@SignInActivity, HomeScreenActivity::class.java)
                            signInIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            signInIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(signInIntent)
                        } else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                        }
                    }
                })
    }

    override fun onStart() {
        super.onStart()

        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        val isConnected = activeNetworkInfo != null && activeNetworkInfo.isConnected

        if (isConnected) {
            val currentUser = mAuth.currentUser
            if (currentUser != null) {
                val signInIntent = Intent(this@SignInActivity, HomeScreenActivity::class.java)
                signInIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                signInIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(signInIntent, ActivityOptions.makeCustomAnimation(this, R.anim.abc_fade_in, R.anim.abc_fade_out).toBundle())
            }
        } else {
            val intent = Intent(this, NotConnectedActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }


    }
}
