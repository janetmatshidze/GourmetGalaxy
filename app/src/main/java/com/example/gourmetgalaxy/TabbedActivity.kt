package com.example.gourmetgalaxy

import android.graphics.Color
import android.os.Bundle
import com.google.android.material.tabs.TabLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.gourmetgalaxy.databinding.ActivityTabbedBinding
import com.google.android.material.tabs.TabLayoutMediator

class TabbedActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTabbedBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTabbedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager, lifecycle)
        val viewPager: ViewPager2 = binding.viewPager
        viewPager.adapter = sectionsPagerAdapter


        val tabs: TabLayout = binding.tabs
        TabLayoutMediator(tabs, viewPager) { tab, position ->

            tab.text = when (position) {
                0 -> "Sign In"
                else -> "Sign Up"
            }
        }.attach()

        tabs.setSelectedTabIndicatorColor(Color.parseColor("#17717c"))
        tabs.setTabTextColors(Color.parseColor("#17717c"), Color.parseColor("#17717c"))
    }

}