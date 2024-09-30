package com.example.gourmetgalaxy

import android.graphics.Color
import android.os.Bundle
import com.google.android.material.tabs.TabLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.gourmetgalaxy.databinding.ActivityTabbedBinding
import com.google.android.material.tabs.TabLayoutMediator

class TabbedActivity : AppCompatActivity() {
    lateinit var viewPager2: ViewPager2
    private lateinit var binding: ActivityTabbedBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTabbedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize viewPager2
        viewPager2 = binding.viewPager // Initialize viewPager2 here

        val sectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager, lifecycle)
        viewPager2.adapter = sectionsPagerAdapter // Use viewPager2 instead of viewPager

        val tabs: TabLayout = binding.tabs
        TabLayoutMediator(tabs, viewPager2) { tab, position ->
            tab.text = when (position) {
                0 -> "Sign In"
                1 -> "Sign Up"
                else -> null
            }
        }.attach()

        tabs.setSelectedTabIndicatorColor(Color.parseColor("#17717c"))
        tabs.setTabTextColors(Color.parseColor("#17717c"), Color.parseColor("#17717c"))
    }
}
