package il.co.urbangarden.ui.forum

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import il.co.urbangarden.R

class QuestionHolder(view: View) : RecyclerView.ViewHolder(view) {
    val view = view
    val image: ImageView = view.findViewById(R.id.question_image)
    val title: TextView = view.findViewById(R.id.question_title)
    val answersNum: TextView = view.findViewById(R.id.answers)
    val date: TextView = view.findViewById(R.id.date)
    val avatar: ImageView = view.findViewById(R.id.avatarIcon)
}
