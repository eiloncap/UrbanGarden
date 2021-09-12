package il.co.urbangarden.ui.forum

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import il.co.urbangarden.R

class AnswersAdapter : RecyclerView.Adapter<AnswerHolder>() {

    private val _answers: MutableList<String> = ArrayList()

    fun setAnswers(answers: List<String>) {
        _answers.clear()
        _answers.addAll(answers)
//        _answers.sortBy { it.date }
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
        holder.answer.text = answer


//        holder.date.text = answer.date.toString()
    }

    override fun getItemCount(): Int {
        return _answers.size
    }
}