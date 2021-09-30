package il.co.urbangarden.ui.forum

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import il.co.urbangarden.R

class TopicHolder(view: View) : RecyclerView.ViewHolder(view) {
    val view = view
    val title: TextView = view.findViewById(R.id.topicTitle)
    val numQuestions: TextView = view.findViewById(R.id.numQ)
    val date: TextView = view.findViewById(R.id.lastQDate)
}
