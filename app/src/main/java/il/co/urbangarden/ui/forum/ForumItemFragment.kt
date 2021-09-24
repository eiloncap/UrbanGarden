package il.co.urbangarden.ui.forum

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import il.co.urbangarden.databinding.FragmentForumItemBinding

class ForumItemFragment : Fragment() {

    private lateinit var forumViewModel: ForumViewModel
    private var _binding: FragmentForumItemBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        forumViewModel =
            ViewModelProvider(requireActivity()).get(ForumViewModel::class.java)

        _binding = FragmentForumItemBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val questionTitle: TextView = binding.questionViewTitle
        val question: TextView = binding.questionView
        val recyclerView: RecyclerView = binding.answerViewRecyclerView

        questionTitle.text = forumViewModel.question?.title ?: ""
        question.text = forumViewModel.question?.question ?: ""

        setupAnswerListAdapter(forumViewModel.question!!.answers)
        
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupAnswerListAdapter(answers: List<String>) {
        val context = requireContext()
        val adapter = AnswersAdapter()

        adapter.setAnswers(answers)
        Log.d("TAG_Q ans", answers.size.toString())

        val questionRecycler = binding.answerViewRecyclerView
        questionRecycler.adapter = adapter
        questionRecycler.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)

    }
}