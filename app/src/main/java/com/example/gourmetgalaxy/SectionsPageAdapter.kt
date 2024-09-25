package com.example.gourmetgalaxy

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class SectionsPagerAdapter(fm: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fm, lifecycle) {

    override fun getItemCount(): Int {
        return 2 // We have two tabs
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> SignInFragment()  // First tab is Sign In
            else -> SignUpFragment() // Second tab is Sign Up
        }
    }
}
