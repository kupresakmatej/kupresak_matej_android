package com.example.testapp

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class VideoListFragment : Fragment(), VideoRecyclerAdapter.VideoContentListener {
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var videoAdapter: VideoRecyclerAdapter
    private lateinit var category: String
    private lateinit var buttonPressed: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_video_list, container, false)

        auth = FirebaseAuth.getInstance()
        database = Firebase.database.reference

        backToChooseCategory(view)

        var bundle = this.arguments
        val sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE)
        buttonPressed = sharedPref.getString("buttonPressed", "nothingFound").toString()
        category = buttonPressed

        displayVideoForCategory(view)

        return view
    }

    private fun displayVideoForCategory(view: View) {
        val videoRecyclerView = view.findViewById<RecyclerView>(R.id.videoRecyclerView)

        videoRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = VideoRecyclerAdapter(ArrayList(), this@VideoListFragment)
        }

        database.child("videos/").child(category).addValueEventListener(object:
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list: ArrayList<WorkoutVideo> = ArrayList()
                for(snapshot in snapshot.children)
                {
                    var fullDesc = snapshot.child("description").getValue().toString()
                    var separated = fullDesc.split(".").toTypedArray()
                    var shortDesc = separated[0]

                    var video = WorkoutVideo(snapshot.child("name").getValue().toString(), shortDesc, snapshot.child("description").getValue().toString(), snapshot.child("url").getValue().toString())

                    list.add(video)
                }

                videoAdapter = VideoRecyclerAdapter(list, this@VideoListFragment)
                videoRecyclerView.apply {
                    layoutManager = LinearLayoutManager(context)
                    adapter = videoAdapter
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun backToChooseCategory(view: View) {
        val backButton = view.findViewById<Button>(R.id.backToChooseCategoryButton)

        backButton.setOnClickListener {
            val videoFragment = VideoFragment()
            val transaction = fragmentManager?.beginTransaction()
            transaction?.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
            transaction?.replace(R.id.mainFrame, videoFragment)?.commit()
        }
    }

    override fun onItemButtonClick(index: Int, video: WorkoutVideo, clickType: ItemClickTypeVideo) {
        if (clickType == ItemClickTypeVideo.SHOW) {
            videoAdapter.removeItem(index)

            var breakDescription = video.description.replace(".", "\n")

            var bundle = Bundle()
            bundle.putString("name", video.name)
            bundle.putString("description", breakDescription)
            bundle.putString("url", video.url)

            val showVideoFragment = ShowVideoFragment()
            showVideoFragment.arguments = bundle
            val transaction = fragmentManager?.beginTransaction()
            transaction?.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
            transaction?.replace(R.id.mainFrame, showVideoFragment)?.commit()
        }
    }
}