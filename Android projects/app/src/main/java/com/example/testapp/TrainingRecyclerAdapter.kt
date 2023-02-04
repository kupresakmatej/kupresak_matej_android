package com.example.testapp

import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.w3c.dom.Text
import java.util.*
import kotlin.collections.ArrayList

enum class ItemClickType{
    REMOVE
}

class TrainingRecyclerAdapter(val items: ArrayList<ScheduledTraining>, val listener: ContentListener): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return TrainingViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.recycler_schedule_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is TrainingViewHolder -> {
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

    class TrainingViewHolder(val view: View): RecyclerView.ViewHolder(view){
        private val date = view.findViewById<TextView>(R.id.dateTextView)
        private val time = view.findViewById<TextView>(R.id.timeTextView)
        private val deleteBtn = view.findViewById<ImageView>(R.id.deleteButtonImage)

        fun bind(index: Int, training: ScheduledTraining, listener: ContentListener){
            date.setText(training.date)
            time.setText(training.time)

            deleteBtn.setOnClickListener{
                listener.onItemButtonClick(index, training, ItemClickType.REMOVE)
            }
        }
    }

    interface ContentListener{
        fun onItemButtonClick(index: Int, training: ScheduledTraining, clickType: ItemClickType)
    }
}