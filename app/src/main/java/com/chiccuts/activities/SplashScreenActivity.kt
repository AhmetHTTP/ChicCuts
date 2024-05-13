package com.chiccuts.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class SplashScreenActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()

        if (auth.currentUser != null) {
            // User is signed in, navigate to MainActivity
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            // No user is signed in, navigate to LoginActivity
            startActivity(Intent(this, LoginActivity::class.java))
        }
        finish()
    }
}
