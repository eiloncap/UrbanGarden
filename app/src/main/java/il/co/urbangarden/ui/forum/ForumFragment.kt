package il.co.urbangarden.ui.forum

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import il.co.urbangarden.R
import il.co.urbangarden.data.forum.Answer
import il.co.urbangarden.data.forum.Topic
import il.co.urbangarden.databinding.FragmentForumBinding
import il.co.urbangarden.ui.MainViewModel
import java.util.*

class ForumFragment : Fragment() {

    private lateinit var mainViewModel: MainViewModel
    private lateinit var forumViewModel: ForumViewModel
    private var _binding: FragmentForumBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
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


        val addTopicButton: ExtendedFloatingActionButton = binding.addNewTopic
        addTopicButton.setOnClickListener {
            newTopicDialog().show()
        }

        if (forumViewModel.topicsLiveData.value == null) {
            forumViewModel.topicsLiveData.observe(requireActivity(),
                { listOfTopic -> setupTopicListAdapter(listOfTopic) })
            forumViewModel.addTopicSnapShot()
        } else {
            setupTopicListAdapter(forumViewModel.topicsLiveData.value!!)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupTopicListAdapter(topics: List<Topic>) {
        val context = requireActivity()
        val adapter = TopicAdapter()

        adapter.setTopics(topics)

        adapter.onItemClick = { topic: Topic ->
            forumViewModel.currTopic = topic
            forumViewModel.questionsLiveData = MutableLiveData()

            // navigate to forum item
            view?.findNavController()?.navigate(R.id.action_navigation_forum_to_placeholder)
        }
        if (_binding != null) {
            val topicRecycler = _binding!!.recycleView
            topicRecycler.adapter = adapter
            topicRecycler.layoutManager =
                LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        }
    }

    private fun newTopicDialog(): Dialog {
        return this.let {
            val builder = AlertDialog.Builder(requireContext())
            // Get the layout inflater
            val view = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_new_answer, null)
            val answer: EditText = view.findViewById(R.id.newAnswer)

            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            builder.setView(view)
                // Add action buttons
                .setPositiveButton("Submit") { dialog, id ->
                    if (answer.text.toString() != "") {
                        forumViewModel.addNewTopic(
                            Topic(
                                topic = answer.text.toString()
                            )
                        )
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Please type something",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                }
                .setNegativeButton("Cancel") { dialog, id ->
                }
            builder.setCancelable(false);
            builder.create()
        }
    }

}
