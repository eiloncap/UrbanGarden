package il.co.urbangarden.ui.forum

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
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

        val addQuestionButton: FloatingActionButton = binding.floatingActionButton
        addQuestionButton.setOnClickListener {
            view?.findNavController()
                ?.navigate(R.id.action_navigation_forum_to_forumNewQuestionFragment)
        }

        if (forumViewModel.questionsLiveData.value == null) {
            getListOfQuestions()
            forumViewModel.questionsLiveData.observe(requireActivity(),
                { listOfQuestion -> setupQuestionListAdapter(listOfQuestion) })
            addSnapShot()
        } else {
            setupQuestionListAdapter(forumViewModel.questionsLiveData.value!!)
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupQuestionListAdapter(questions: List<Question>) {
        val context = requireContext()
        val adapter = QuestionAdapter()

        adapter.setQuestions(questions)
        Log.d("TAG_Q", questions.size.toString())
//
        adapter.onItemClick = { question: Question ->
            Log.d("TAG_Q", "clicked")
            forumViewModel.question = question

            // navigate to forum item
            view?.findNavController()?.navigate(R.id.action_navigation_forum_to_forumItemFragment2)
        }

        val questionRecycler = binding.recycleView
        questionRecycler.adapter = adapter
        questionRecycler.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)

    }

    private fun getListOfQuestions() {
        val arrayList = ArrayList<Question>()
        forumViewModel.firebase.collection("Forum")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    arrayList.add(document.toObject(Question::class.java))
                    Log.d("TAG_Q fetch", "${document.id} => ${document.data}")
                }
                forumViewModel.questionsLiveData.value = arrayList
            }
            .addOnFailureListener { exception ->
                Log.d("TAG_Q fetch fail", "Error getting documents: ", exception)
            }

//        return listOf(
//            Question(image = " ", title = " yoyoyoy yo"),
//            Question(
//                title = "what is wronng with my fucking plant the hel",
//                answers = listOf(
//                    "1",
//                    "2",
//                    "3",
//                    "asdfjs;alkdjflk alsdfjl; asdlkj  f sdf fdf  fdsf sdf lsdkafj"
//                )
//            ),
//            Question(),
//            Question(),
//            Question(),
//            Question()
//        )
    }

    private fun addSnapShot() {
        forumViewModel.firebase.collection("Forum").addSnapshotListener { value, error ->
            if (value == null || error != null) {
                Log.e("TAG_Err", "error")
            } else if (!value.isEmpty) {
//                setupQuestionListAdapter(forumViewModel.questionsLiveData.value!!)
                Log.d("TAG_Err", "change")
                Log.d("TAG_Err", value.toString())
                getListOfQuestions()
            }
        }
    }
}