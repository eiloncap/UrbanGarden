package il.co.urbangarden.ui.forum

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import il.co.urbangarden.R
import il.co.urbangarden.data.FirebaseViewableObject
import il.co.urbangarden.data.forum.Topic
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class TopicAdapter : RecyclerView.Adapter<TopicHolder>() {

    private val _topics: MutableList<Topic> = ArrayList()
    var onItemClick: ((Topic) -> Unit)? = null

    fun setTopics(topics: List<Topic>) {
        _topics.clear()
        _topics.addAll(topics)
        _topics.sortByDescending { it.date }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopicHolder {
        val context = parent.context
        val view = LayoutInflater.from(context)
            .inflate(R.layout.one_line_forum_topic, parent, false)
        val holder = TopicHolder(view)

        holder.view.setOnClickListener {
            val callback = onItemClick ?: return@setOnClickListener
            val topic = _topics[holder.adapterPosition]
            callback(topic)
//            Log.d("TAG_PRINT_cancel", "cancel_in ${num.number}, id: ${num.workerId}")
        }
        return holder
    }

    override fun onBindViewHolder(holder: TopicHolder, position: Int) {
        val topic = _topics[position]
        holder.title.text = topic.topic
        holder.numQuestions.text = topic.numOfQuestions.toString()
        val dateFormat: DateFormat = SimpleDateFormat("dd-MM-yy")
        holder.date.text = dateFormat.format(topic.date).toString()
    }

    override fun getItemCount(): Int {
        return _topics.size
    }
}