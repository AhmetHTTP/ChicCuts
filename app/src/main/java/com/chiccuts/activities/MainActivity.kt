package com.chiccuts.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.chiccuts.R
import com.chiccuts.fragments.*
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navView: BottomNavigationView = findViewById(R.id.bottom_navigation)

        navView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_barbers -> {
                    loadFragment(BarbersListFragment())
                    true
                }
                R.id.nav_hairdressers -> {
                    loadFragment(HairdressersListFragment())
                    true
                }
                R.id.nav_appointments -> {
                    loadFragment(AppointmentsFragment())
                    true
                }
                R.id.nav_profile -> {
                    loadFragment(ProfileFragment())
                    true
                }
                else -> false
            }
        }

        // Varsayılan olarak BarbersListFragment yüklensin
        if (savedInstanceState == null) {
            loadFragment(BarbersListFragment())
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
