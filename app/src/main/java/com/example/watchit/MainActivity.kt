package com.example.watchit

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fragmentManager: FragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
//        transaction.replace(R.id.FragmentLayout, FeedFragment())
//        transaction.replace(R.id.FragmentLayout, SearchFragment())
        transaction.replace(R.id.FragmentLayout, Profile())
        transaction.addToBackStack(null)
        transaction.commit()

        bottomMenu()
    }

    private fun bottomMenu() {
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when(item.itemId) {
                R.id.Home -> {
                    // Respond to navigation item 1 click
                    Toast.makeText(this@MainActivity, "Home selected!", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.Upload -> {
                    // Respond to navigation item 2 click
                    Toast.makeText(this@MainActivity, "Upload selected!", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }
    }
}