package il.co.urbangarden.ui.forum

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import il.co.urbangarden.R
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
            ViewModelProvider(requireActivity()).get(ForumViewModel::class.java)

        _binding = FragmentForumBinding.inflate(inflater, container, false)
        val root: View = binding.root

//        val textView: TextView = binding.textNotifications
//        forumViewModel.text.observe(viewLifecycleOwner, Observer {
//            textView.text = it
//        })

        setupQuestionListAdapter(
            listOf(
                Question(image = " ", title = " yoyoyoy yo"),
                Question(
                    title = "what is wronng with my fucking plant the hel",
                    answers = listOf("1", "2", "3", "asdfjs;alkdjflk alsdfjl; asdlkj  f sdf fdf  fdsf sdf lsdkafj")
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

    private fun setupQuestionListAdapter(questions: List<Question>) {
        val context = requireContext()
        val adapter = QuestionAdapter()

        adapter.setPeople(questions)
        Log.d("TAG_Q", questions.size.toString())
//
        adapter.onItemClick = { question: Question ->
            Log.d("TAG_Q", "clicked")
            forumViewModel.question = question

//            //todo delete
//            Toast.makeText(
//                requireActivity(),
//                question.title,
//                Toast.LENGTH_LONG
//            )
//                .show()
            // navigate to forum item
            view?.findNavController()?.navigate(R.id.action_navigation_forum_to_forumItemFragment2)

        }

        val questionRecycler = binding.recycleView
        questionRecycler.adapter = adapter
        questionRecycler.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)

    }
}