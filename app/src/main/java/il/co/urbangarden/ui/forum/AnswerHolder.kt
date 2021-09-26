package il.co.urbangarden.ui.forum

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import il.co.urbangarden.R

class AnswerHolder(view: View) : RecyclerView.ViewHolder(view) {
    val view = view
    val answer: TextView = view.findViewById(R.id.question_answer)
    val date: TextView = view.findViewById(R.id.date)
}
