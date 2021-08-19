package il.co.urbangarden.ui.forum

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import il.co.urbangarden.R
import il.co.urbangarden.data.forum.Question

class QuestionAdapter : RecyclerView.Adapter<QuestionHolder>() {

    private val _questions: MutableList<Question> = ArrayList()
    var onCancelClick: ((Question) -> Unit)? = null

    fun setPeople(questions: List<Question>) {
        _questions.clear()
        _questions.addAll(questions)
        _questions.sortBy { it.date }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionHolder {
        val context = parent.context
        val view = LayoutInflater.from(context)
            .inflate(R.layout.forum_one_line, parent, false)
        val holder = QuestionHolder(view)

        holder.view.setOnClickListener {
            val callback = onCancelClick ?: return@setOnClickListener
            val question = _questions[holder.adapterPosition]
            callback(question)
//            Log.d("TAG_PRINT_cancel", "cancel_in ${num.number}, id: ${num.workerId}")
        }

        return holder
    }

    override fun onBindViewHolder(holder: QuestionHolder, position: Int) {
        val question = _questions[position]
        if (question.image != "") {
//            TODO set image
        }
        holder.title.text = question.title

        holder.answersNum.text = question.answers.size.toString()
        holder.date.text = question.date.toString()
    }

    override fun getItemCount(): Int {
        return _questions.size
    }
}