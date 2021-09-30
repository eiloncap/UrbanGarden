package il.co.urbangarden.ui.forum

import android.os.Bundle
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
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import il.co.urbangarden.R
import il.co.urbangarden.data.FirebaseViewableObject
import il.co.urbangarden.data.forum.Question
import il.co.urbangarden.databinding.FragmentForumTopicBinding
import il.co.urbangarden.ui.MainViewModel
import il.co.urbangarden.utils.ImageCropOption

class ForumFragmentTopic : Fragment() {

    private lateinit var mainViewModel: MainViewModel
    private lateinit var forumViewModel: ForumViewModel
    private var _binding: FragmentForumTopicBinding? = null

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

//        val _bind = inflater.inflate(R.layout.fragment_forum_topic, container, false);

        _binding = FragmentForumTopicBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.topicTitle.text = forumViewModel.currTopic?.topic ?: ""

        val addQuestionButton: ExtendedFloatingActionButton = binding.addNewQuestion
        addQuestionButton.setOnClickListener {
            view?.findNavController()
                ?.navigate(R.id.action_topicFragment_to_forumNewQuestionFragment)
        }

        if (forumViewModel.questionsLiveData.value == null) {
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
        val context = requireActivity()
        val adapter = QuestionAdapter()

        adapter.setQuestions(questions)

        adapter.onItemClick = { question: Question ->
            forumViewModel.currQuestion = question
            forumViewModel.currAnswers = MutableLiveData()

            // navigate to forum item
            view?.findNavController()?.navigate(R.id.action_topicFragment_to_forumItemFragment2)
        }

        adapter.setImg = { question: FirebaseViewableObject, img: ImageView ->
            mainViewModel.setImgFromPath(question, img, ImageCropOption.SQUARE)
        }

        adapter.setAvatar = { imageView, uri ->
            Glide.with(context)
                .load(uri)
                .circleCrop()
                .into(imageView)
        }

        val questionRecycler = _binding!!.recycleView
        questionRecycler.adapter = adapter
        questionRecycler.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)
    }
}
