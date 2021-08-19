package il.co.urbangarden.ui.forum

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import il.co.urbangarden.data.forum.Question
import il.co.urbangarden.databinding.FragmentForumBinding

class ForumFragment : Fragment() {

    private lateinit var forumViewModel: ForumViewModel
    private var _binding: FragmentForumBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        forumViewModel =
            ViewModelProvider(this).get(ForumViewModel::class.java)

        _binding = FragmentForumBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textNotifications
        forumViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })

//        textView
        setupListAdapter(
            listOf(
                Question(image = " ",title = " yoyoyoy yo"),
                Question(
                    title = "what is wronng with my fucking plant the hel",
                    answers = listOf("1", "2", "3")
                ),
                Question(),
                Question(),
                Question(),
                Question()
            )
        )
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupListAdapter(questions: List<Question>) {
        val context = requireContext()
        val adapter = QuestionAdapter()

        adapter.setPeople(questions)
        Log.d("TAG_Q", questions.size.toString())
//
        adapter.onCancelClick = { question: Question ->
            Log.d("TAG_Q", "clicked")

        }

        val questionRecycler = binding.recycleView
        questionRecycler.adapter = adapter
        questionRecycler.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)

    }
}