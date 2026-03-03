package com.codepath.lab6

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.codepath.lab6.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.serialization.json.Json

fun createJson() = Json {
    isLenient = true
    ignoreUnknownKeys = true
    useAlternativeNames = false
}

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar: Toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.app_name)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ParkFragment.newInstance())
                .commit()
        }

        val bottomNav: BottomNavigationView = binding.bottomNavigation
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_parks -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, ParkFragment.newInstance())
                        .commit()
                    true
                }
                R.id.navigation_campgrounds -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, CampgroundFragment.newInstance())
                        .commit()
                    true
                }
                else -> false
            }
        }
        bottomNav.selectedItemId = R.id.navigation_parks
    }
}
