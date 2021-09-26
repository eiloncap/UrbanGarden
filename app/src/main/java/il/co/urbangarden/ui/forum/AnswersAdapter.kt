package il.co.urbangarden.ui.forum

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import il.co.urbangarden.R
import il.co.urbangarden.data.forum.Answer
import java.text.DateFormat
import java.text.SimpleDateFormat

class AnswersAdapter : RecyclerView.Adapter<AnswerHolder>() {

    private val _answers: MutableList<Answer> = ArrayList()

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
            .inflate(R.layout.forum_answer_one_line, parent, false)

        return AnswerHolder(view)
    }

    override fun onBindViewHolder(holder: AnswerHolder, position: Int) {
        val answer = _answers[position]
        holder.answer.text = answer.answer
        val dateFormat: DateFormat = SimpleDateFormat("dd-MM-yy")
        holder.date.text = dateFormat.format(answer.date).toString()
    }

    override fun getItemCount(): Int {
        return _answers.size
    }
}