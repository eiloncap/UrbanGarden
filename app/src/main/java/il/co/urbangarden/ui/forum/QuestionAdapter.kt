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
import il.co.urbangarden.data.forum.Question
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class QuestionAdapter : RecyclerView.Adapter<QuestionHolder>() {

    private val _questions: MutableList<Question> = ArrayList()
    var onItemClick: ((Question) -> Unit)? = null
    var setImg: ((FirebaseViewableObject, ImageView) -> Unit)? = null
    var setAvatar: ((ImageView, String) -> Unit)? = null

    fun setQuestions(questions: List<Question>) {
        _questions.clear()
        _questions.addAll(questions)
        _questions.sortByDescending { it.date }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionHolder {
        val context = parent.context
        val view = LayoutInflater.from(context)
            .inflate(R.layout.one_line_forum_question, parent, false)
        val holder = QuestionHolder(view)

        holder.view.setOnClickListener {
            val callback = onItemClick ?: return@setOnClickListener
            val question = _questions[holder.adapterPosition]
            callback(question)
//            Log.d("TAG_PRINT_cancel", "cancel_in ${num.number}, id: ${num.workerId}")
        }
        return holder
    }

    override fun onBindViewHolder(holder: QuestionHolder, position: Int) {
        val question = _questions[position]
        if (question.imgFileName != "") {
            holder.image.alpha = 1F
            Log.d("Tag_q adapter img debug", question.imgFileName)
            setImg?.let { it(question, holder.image) }
        } else {
            holder.image.visibility = View.GONE
        }
        holder.title.text = question.title

        holder.answersNum.text = question.numOfAnswers.toString()
//        holder.date.text = question.date.toString()
        val dateFormat: DateFormat = SimpleDateFormat("dd-MM-yy")
        holder.date.text = dateFormat.format(question.date).toString()

        setAvatar?.let { it(holder.avatar, question.uri) }
    }

    override fun getItemCount(): Int {
        return _questions.size
    }
}