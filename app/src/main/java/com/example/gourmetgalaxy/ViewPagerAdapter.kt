package com.example.gourmetgalaxy

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(fragment: HomeActivity) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 3 // Number of slides

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> FirstFragment()
            1 -> SecondFragment()
            2 -> ThirdFragment()
            else -> throw IllegalStateException("Unexpected position $position")
        }
    }
}
