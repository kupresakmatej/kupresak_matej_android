package com.example.testapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.ArrayList

class ProgressFragment : Fragment(), ProgressRecyclerAdapter.ContentProgressListener {
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var progressAdapter: ProgressRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_progress, container, false)

        navigationController(view)

        auth = FirebaseAuth.getInstance()
        database = Firebase.database.reference

        inputProgress(view)

        displayProgress(view)

        return view
    }

    private fun displayProgress(view: View) {
        val progressRecyclerView = view.findViewById<RecyclerView>(R.id.progressRecyclerView)

        progressRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = ProgressRecyclerAdapter(ArrayList(), this@ProgressFragment)
        }

        database.child("users/" + auth.currentUser?.uid).child("progress").addValueEventListener(object:
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list: ArrayList<Progress> = ArrayList()
                for(snapshot in snapshot.children)
                {
                    var progress = Progress(snapshot.child("calories").getValue().toString(), snapshot.child("timeSpent").getValue().toString())

                    list.add(progress)
                }

                progressAdapter = ProgressRecyclerAdapter(list, this@ProgressFragment)
                progressRecyclerView.apply {
                    layoutManager = LinearLayoutManager(context)
                    adapter = progressAdapter
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun inputProgress(view: View) {
        val submitProgressButton = view.findViewById<Button>(R.id.submitProgressButton)
        var caloriesEditText = view.findViewById<EditText>(R.id.caloriesSpentEditText)
        var timeEditText = view.findViewById<EditText>(R.id.timeSpentEditText)

        submitProgressButton.setOnClickListener {
            val progressToAdd = Progress(caloriesEditText.text.toString(), timeEditText.text.toString())
            database.child("users/" + auth.currentUser?.uid).child("progress").child(timeEditText.text.toString()).setValue(progressToAdd)
        }
    }

    private fun navigationController(view: View) {
        val homeNavButton = view.findViewById<ImageView>(R.id.homeNavButton)
        val scheduleNavButton = view.findViewById<ImageView>(R.id.calendarNavButton)
        val videoNavButton = view.findViewById<ImageView>(R.id.videoNavButton)
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

        videoNavButton.setOnClickListener {
            val videoFragment = VideoFragment()
            val transaction = fragmentManager?.beginTransaction()
            transaction?.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
            transaction?.replace(R.id.mainFrame, videoFragment)?.commit()
        }

        infoNavButton.setOnClickListener {
            val infoFragment = InformationFragment()
            val transaction = fragmentManager?.beginTransaction()
            transaction?.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
            transaction?.replace(R.id.mainFrame, infoFragment)?.commit()
        }
    }

    override fun onItemButtonClick(index: Int, progress: Progress, clickType: ItemClickTypeProgress) {
        if (clickType == ItemClickTypeProgress.REMOVE) {
            progressAdapter.removeItem(index)
            progress.timeSpent?.let {
                database.child("users/" + auth.currentUser?.uid).child("progress").child(progress.timeSpent).removeValue()
            }
        }
    }
}