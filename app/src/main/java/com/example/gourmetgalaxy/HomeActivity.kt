package com.example.gourmetgalaxy

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val viewPager = findViewById<ViewPager2>(R.id.viewPager)
        val tabLayout = findViewById<TabLayout>(R.id.tabLayout)

        // Set up the ViewPager with the OnboardingAdapter
        val adapter = ViewPagerAdapter(this)
        viewPager.adapter = adapter

        // Link the TabLayout with the ViewPager and set custom tab views
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.customView = layoutInflater.inflate(R.layout.custom_tab, null)
        }.attach()

        val startButton = findViewById<Button>(R.id.startBtn)

        startButton.setOnClickListener {
            // Navigates to TabbedActivity class
            val intent = Intent(this, TabbedActivity::class.java)
            startActivity(intent)

        }
            val backButton = findViewById<ImageButton>(R.id.backButton)

              backButton.setOnClickListener {

                val intent = Intent(this,MainActivity::class.java)
                startActivity(intent)
            }

    }
}