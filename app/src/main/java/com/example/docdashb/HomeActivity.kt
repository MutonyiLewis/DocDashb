package com.example.docdashb

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.fragment.app.Fragment
import com.example.docdashb.databinding.ActivityHomeBinding
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    lateinit var topAppBar: MaterialToolbar
    private  lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(Home())
        topAppBar = findViewById(R.id.topbar)

        topAppBar.setNavigationOnClickListener {
            Toast.makeText(this,"nav icon", Toast.LENGTH_SHORT).show()
        }

        topAppBar.setOnMenuItemClickListener { menuItem ->
            when(menuItem.itemId){
                R.id.topbar -> {
                    Toast.makeText(this, "Menu clicked",Toast.LENGTH_SHORT).show()
                    true

                }

                else -> {
                    true
                }
            }
        }

//        Auth
//        auth = FirebaseAuth.getInstance()
//
//        binding.btnLogout.setOnClickListener {
//            auth.signOut()
//            Intent(this,LoginActivity::class.java).also {
//                it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                startActivity(it)
//                Toast.makeText(this,"You are logged out", Toast.LENGTH_SHORT).show()
//            }
//        }

        binding.bottomNavigationView.setOnItemReselectedListener {
            when(it.itemId){
                R.id.mnhome -> replaceFragment(Home())
                R.id.mnprofile -> replaceFragment(Profile())
                R.id.mnsettings -> replaceFragment(Settings())

                else -> {

                }

            }
            true
        }

    }


    private fun replaceFragment(fragment: Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.flFragment,fragment)
        fragmentTransaction.commit()
    }

}