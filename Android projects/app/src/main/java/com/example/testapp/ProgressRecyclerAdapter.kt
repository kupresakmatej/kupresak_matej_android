package com.example.testapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

enum class ItemClickTypeProgress{
    REMOVE
}

class ProgressRecyclerAdapter(val items: ArrayList<Progress>, val listener: ContentProgressListener): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ProgressViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.recycler_progress_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is ProgressViewHolder -> {
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

    class ProgressViewHolder(val view: View): RecyclerView.ViewHolder(view){
        private val calories = view.findViewById<TextView>(R.id.caloriesLostTextView)
        private val timeSpent = view.findViewById<TextView>(R.id.timeSpentTextView)
        private val deleteBtn = view.findViewById<ImageView>(R.id.deleteButtonImage2)

        fun bind(index: Int, progress: Progress, listener: ContentProgressListener){
            calories.setText("You lost: " + progress.calories + " calories.")
            timeSpent.setText("You spent " + progress.timeSpent + " minutes working out.")

            deleteBtn.setOnClickListener{
                listener.onItemButtonClick(index, progress, ItemClickTypeProgress.REMOVE)
            }
        }
    }

    interface ContentProgressListener{
        fun onItemButtonClick(index: Int, progress: Progress, clickType: ItemClickTypeProgress)
    }
}