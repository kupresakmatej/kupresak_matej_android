package com.example.testapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView


class ShowVideoFragment : Fragment() {
    private lateinit var buttonPressed: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_show_video, container, false)

        var bundle = this.arguments
        var name = bundle?.getString("name")
        var description = bundle?.getString("description")
        var url = bundle?.getString("url")

        val nameTextView = view.findViewById<TextView>(R.id.exceriseNameTextView)
        val desc = view.findViewById<TextView>(R.id.videoDescriptionTextView)

        nameTextView.text = name
        desc.text = description

        var youtube = view.findViewById<YouTubePlayerView>(R.id.videoView)
        lifecycle.addObserver(youtube)

        youtube?.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                val videoId = url.toString()
                youTubePlayer.loadVideo(videoId, 0f)
            }
        })

        returnToVideoList(view)

        return view
    }

    private fun returnToVideoList(view: View) {
        val returnButton = view.findViewById<Button>(R.id.backToVideoListButton)

        returnButton.setOnClickListener {
            val videoListFragment = VideoListFragment()
            val transaction = fragmentManager?.beginTransaction()
            transaction?.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
            transaction?.replace(R.id.mainFrame, videoListFragment)?.commit()
        }
    }
}