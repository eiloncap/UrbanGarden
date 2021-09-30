package il.co.urbangarden.ui.forum

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import il.co.urbangarden.R
import il.co.urbangarden.data.forum.Answer
import java.text.DateFormat
import java.text.SimpleDateFormat

class AnswersAdapter : RecyclerView.Adapter<AnswerHolder>() {

    private val _answers: MutableList<Answer> = ArrayList()
    var setAvatar: ((ImageView, String) -> Unit)? = null

    fun setAnswers(answers: List<Answer>) {
        _answers.clear()
        _answers.addAll(answers)
//        _answers.sortBy { it.date }
        _answers.sortBy { it.date }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnswerHolder {
        val context = parent.context
        val view = LayoutInflater.from(context)
            .inflate(R.layout.one_line_forum_answer, parent, false)

        return AnswerHolder(view)
    }

    override fun onBindViewHolder(holder: AnswerHolder, position: Int) {
        val answer = _answers[position]
        Log.d("Tag_q adapter", answer.answer)
        Log.d("Tag_q adapter", "${_answers.size-1} pos: $position")
        holder.answer.text = answer.answer
        val dateFormatHour: DateFormat = SimpleDateFormat("HH:mm")
        holder.dateHour.text = dateFormatHour.format(answer.date).toString()
        val dateFormat: DateFormat = SimpleDateFormat("dd-MM-yy")
        holder.date.text = dateFormat.format(answer.date).toString()
        setAvatar?.let { it(holder.avatar, answer.uri) }
    }

    override fun getItemCount(): Int {
        return _answers.size
    }
}