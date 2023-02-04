package com.example.testapp

import android.provider.MediaStore.Video
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.VideoView
import androidx.recyclerview.widget.RecyclerView

enum class ItemClickTypeVideo{
    SHOW
}

class VideoRecyclerAdapter(val items: ArrayList<WorkoutVideo>, val listener: VideoContentListener): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return VideoViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.recycler_video_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is VideoViewHolder -> {
                holder.bind(position, items[position], listener)
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun removeItem(index: Int) {
        items.removeAt(index)
        notifyItemRemoved(index)
        notifyItemRangeChanged(index, items.size)
    }

    class VideoViewHolder(val view: View): RecyclerView.ViewHolder(view){
        private val name = view.findViewById<TextView>(R.id.videoNameTextView)
        private val description = view.findViewById<TextView>(R.id.videoRecyclerDescriptionTextView)
        private val showVideoButton = view.findViewById<Button>(R.id.showVideoButton)

        fun bind(index: Int, video: WorkoutVideo, listener: VideoContentListener){
            name.setText(video.name)
            name.textAlignment = View.TEXT_ALIGNMENT_VIEW_START
            description.setText(video.shortDescription)
            description.textAlignment = View.TEXT_ALIGNMENT_VIEW_START

            showVideoButton.setOnClickListener{
                listener.onItemButtonClick(index, video, ItemClickTypeVideo.SHOW)
            }
        }
    }

    interface VideoContentListener{
        fun onItemButtonClick(index: Int, video: WorkoutVideo, clickType: ItemClickTypeVideo)
    }
}