package com.example.hp.speakup.adapters

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.example.hp.speakup.fragments.AdministrationFragment
import com.example.hp.speakup.fragments.CouncilFragment

class HomeTabsAdapter(fm:FragmentManager):FragmentPagerAdapter(fm){

    private val fragmentArray = arrayOf(AdministrationFragment(), CouncilFragment())
    private val titleArray = arrayOf("Discuss Administration", "Discuss Council")

    override fun getItem(position: Int): Fragment {
        return fragmentArray[position]
    }

    override fun getCount(): Int=2

    override fun getPageTitle(position: Int): CharSequence? {
        return titleArray[position]
    }
}