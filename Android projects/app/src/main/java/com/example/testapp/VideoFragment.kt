package com.example.testapp

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.Fragment

class VideoFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_video, container, false)

        navigationController(view)

        showVideosForChosenCategory(view)

        return view
    }

    private fun navigationController(view: View) {
        val homeNavButton = view.findViewById<ImageView>(R.id.homeNavButton)
        val scheduleNavButton = view.findViewById<ImageView>(R.id.calendarNavButton)
        val progressNavButton = view.findViewById<ImageView>(R.id.progressNavButton)
        val infoNavButton = view.findViewById<ImageView>(R.id.infoNavButton)

        homeNavButton.setOnClickListener {
            val homeFragment = HomeFragment()
            val transaction = fragmentManager?.beginTransaction()
            transaction?.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
            transaction?.replace(R.id.mainFrame, homeFragment)?.commit()
        }

        scheduleNavButton.setOnClickListener {
            val scheduleFragment = ScheduleFragment()
            val transaction = fragmentManager?.beginTransaction()
            transaction?.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
            transaction?.replace(R.id.mainFrame, scheduleFragment)?.commit()
        }

        progressNavButton.setOnClickListener {
            val progressFragment = ProgressFragment()
            val transaction = fragmentManager?.beginTransaction()
            transaction?.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
            transaction?.replace(R.id.mainFrame, progressFragment)?.commit()
        }

        infoNavButton.setOnClickListener {
            val infoFragment = InformationFragment()
            val transaction = fragmentManager?.beginTransaction()
            transaction?.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
            transaction?.replace(R.id.mainFrame, infoFragment)?.commit()
        }
    }

    private fun showVideosForChosenCategory(view: View) {
        val backButton = view.findViewById<Button>(R.id.backButton)
        val chestButton = view.findViewById<Button>(R.id.chestButton)
        val legsButton = view.findViewById<Button>(R.id.legsButton)
        val ankleButton = view.findViewById<Button>(R.id.ankleButton)
        val coreButton = view.findViewById<Button>(R.id.coreButton)

        backButton.setOnClickListener {
            val sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE)
            sharedPref.edit().putString("buttonPressed", "back").apply()
            sharedPref.edit().commit();

            val categoryFragment = VideoListFragment()
            val transaction = fragmentManager?.beginTransaction()
            transaction?.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
            transaction?.replace(R.id.mainFrame, categoryFragment)?.commit()
        }
        chestButton.setOnClickListener {
            val sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE)
            sharedPref.edit().putString("buttonPressed", "chest").apply()
            sharedPref.edit().commit();

            val categoryFragment = VideoListFragment()
            val transaction = fragmentManager?.beginTransaction()
            transaction?.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
            transaction?.replace(R.id.mainFrame, categoryFragment)?.commit()
        }
        legsButton.setOnClickListener {
            val sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE)
            sharedPref.edit().putString("buttonPressed", "legs").apply()
            sharedPref.edit().commit();

            val categoryFragment = VideoListFragment()
            val transaction = fragmentManager?.beginTransaction()
            transaction?.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
            transaction?.replace(R.id.mainFrame, categoryFragment)?.commit()
        }
        ankleButton.setOnClickListener {
            val sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE)
            sharedPref.edit().putString("buttonPressed", "ankle").apply()
            sharedPref.edit().commit();

            val categoryFragment = VideoListFragment()
            val transaction = fragmentManager?.beginTransaction()
            transaction?.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
            transaction?.replace(R.id.mainFrame, categoryFragment)?.commit()
        }
        coreButton.setOnClickListener {
            val sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE)
            sharedPref.edit().putString("buttonPressed", "core").apply()
            sharedPref.edit().commit();

            val categoryFragment = VideoListFragment()
            val transaction = fragmentManager?.beginTransaction()
            transaction?.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
            transaction?.replace(R.id.mainFrame, categoryFragment)?.commit()
        }
    }
}