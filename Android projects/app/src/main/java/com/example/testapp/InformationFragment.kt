package com.example.testapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.FirebaseError
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class InformationFragment : Fragment() {
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_information, container, false)

        auth = FirebaseAuth.getInstance()
        database = Firebase.database.reference

        navigationControler(view)

        adjustWeight(view)

        sendEmailWithTips(view)

        return view
    }

    private fun sendEmailWithTips(view: View) {
        val sendEmailButton = view.findViewById<Button>(R.id.submitLetUsKnowButton)

        sendEmailButton.setOnClickListener {
            var improvementText = view.findViewById<EditText>(R.id.letUsKnowEditText)

            database.child("users/" + auth.currentUser?.uid).child("fullName").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val name = dataSnapshot.value.toString()

                    val selectorIntent = Intent(Intent.ACTION_SENDTO)
                    selectorIntent.data = Uri.parse("mailto:")

                    val emailIntent = Intent(Intent.ACTION_SEND)
                    emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("matej.kupresak@gmail.com"))
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Tips for improvement - " + name)
                    emailIntent.putExtra(Intent.EXTRA_TEXT, improvementText.text.toString())
                    emailIntent.selector = selectorIntent

                    requireActivity().startActivity(Intent.createChooser(emailIntent, "Send email..."))
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

                fun onCancelled(firebaseError: FirebaseError?) {}
            })
        }
    }

    private fun adjustWeight(view: View) {
        val submitWeightChangeButton = view.findViewById<Button>(R.id.submitChangedWeightButton)

        var currentWeight = view.findViewById<EditText>(R.id.weightChangeEditText)

        submitWeightChangeButton.setOnClickListener {

            if(currentWeight.text.toString().length <= 0)
            {
                Toast.makeText(context, "Weight field cannot be empty.", Toast.LENGTH_LONG).show()
            }
            else
            {
                database.child("users/" + auth.currentUser?.uid).child("weight").setValue(currentWeight.text.toString())
                Toast.makeText(context, "Weight updated!", Toast.LENGTH_LONG).show()

                currentWeight.text.clear()
            }
        }
    }

    private fun navigationControler(view: View) {
        val homeNavButton = view.findViewById<ImageView>(R.id.homeNavButton)
        val videoNavButton = view.findViewById<ImageView>(R.id.videoNavButton)
        val progressNavButton = view.findViewById<ImageView>(R.id.progressNavButton)
        val scheduleNavButton = view.findViewById<ImageView>(R.id.calendarNavButton)

        homeNavButton.setOnClickListener {
            val homeFragment = HomeFragment()
            val transaction = fragmentManager?.beginTransaction()
            transaction?.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
            transaction?.replace(R.id.mainFrame, homeFragment)?.commit()
        }

        videoNavButton.setOnClickListener {
            val videoFragment = VideoFragment()
            val transaction = fragmentManager?.beginTransaction()
            transaction?.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
            transaction?.replace(R.id.mainFrame, videoFragment)?.commit()
        }

        progressNavButton.setOnClickListener {
            val progressFragment = ProgressFragment()
            val transaction = fragmentManager?.beginTransaction()
            transaction?.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
            transaction?.replace(R.id.mainFrame, progressFragment)?.commit()
        }

        scheduleNavButton.setOnClickListener {
            val scheduleFragment = ScheduleFragment()
            val transaction = fragmentManager?.beginTransaction()
            transaction?.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
            transaction?.replace(R.id.mainFrame, scheduleFragment)?.commit()
        }
    }
}