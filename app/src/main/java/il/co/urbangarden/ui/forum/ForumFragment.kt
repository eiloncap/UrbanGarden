package il.co.urbangarden.ui.forum

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import il.co.urbangarden.R
import il.co.urbangarden.data.FirebaseViewableObject
import il.co.urbangarden.data.forum.Answer
import il.co.urbangarden.data.forum.Question
import il.co.urbangarden.databinding.FragmentForumBinding
import il.co.urbangarden.ui.MainViewModel
import il.co.urbangarden.utils.ImageCropOption

class ForumFragment : Fragment() {

    private lateinit var mainViewModel: MainViewModel
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
        mainViewModel =
            ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        forumViewModel =
            ViewModelProvider(requireActivity()).get(ForumViewModel::class.java)

        _binding = FragmentForumBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val addQuestionButton: ExtendedFloatingActionButton = binding.addNewQuestion
        addQuestionButton.setOnClickListener {
            view?.findNavController()
                ?.navigate(R.id.action_navigation_forum_to_forumNewQuestionFragment)
        }

        Log.d("TAG_Q ans", "yoyo")
//        forumViewModel.currAnswers.value = null

        if (forumViewModel.questionsLiveData.value == null) {
//            forumViewModel.getListOfQuestions()
            forumViewModel.questionsLiveData.observe(requireActivity(),
                { listOfQuestion -> setupQuestionListAdapter(listOfQuestion) })
            forumViewModel.addQuestionSnapShot()
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
//            forumViewModel.currAnswers.value = null
            forumViewModel.currQuestion = question
//            forumViewModel.getListOfAnswers()
            forumViewModel.currAnswers = MutableLiveData()

            // navigate to forum item
            view?.findNavController()?.navigate(R.id.action_navigation_forum_to_forumItemFragment2)
        }

        adapter.setImg = { question: FirebaseViewableObject, img: ImageView ->
            mainViewModel.setImgFromPath(question, img, ImageCropOption.SQUARE)
        }

        val questionRecycler = binding.recycleView
        questionRecycler.adapter = adapter
        questionRecycler.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)

    }

}