package com.example.hp.speakup.fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.hp.speakup.R
import com.example.hp.speakup.adapters.HomeTabsAdapter
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        activity?.title = "Speak Up"
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vpHome.adapter = HomeTabsAdapter(childFragmentManager)
        tlHome.setupWithViewPager(vpHome)
        val currentItem = arguments?.getInt("currentItem")
        if (currentItem != null) {
            vpHome.currentItem = Integer.valueOf(currentItem)
        }
    }
}
