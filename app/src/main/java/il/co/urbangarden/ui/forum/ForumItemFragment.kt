package il.co.urbangarden.ui.forum

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import il.co.urbangarden.R
import il.co.urbangarden.data.FirebaseViewableObject
import il.co.urbangarden.data.forum.Answer
import il.co.urbangarden.databinding.FragmentForumItemBinding
import il.co.urbangarden.ui.MainViewModel
import il.co.urbangarden.utils.ImageCropOption
import java.util.*

class ForumItemFragment : Fragment() {

    private lateinit var mainViewModel: MainViewModel
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
        mainViewModel =
            ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        forumViewModel =
            ViewModelProvider(requireActivity()).get(ForumViewModel::class.java)

        _binding = FragmentForumItemBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val addAnswerButton: ExtendedFloatingActionButton = binding.addNewAnswer
        addAnswerButton.setOnClickListener {
            newAnswerDialog().show()
        }

        val questionTitle: TextView = binding.questionViewTitle
        val question: TextView = binding.questionView
        val card = binding.card
        val img: ImageView = binding.viewImage
        if (forumViewModel.currQuestion?.imgFileName ?: "" != "") {
            forumViewModel.currQuestion?.let {
                mainViewModel.setImgFromPath(
                    it,
                    img,
                    ImageCropOption.SQUARE
                )
            }
            card.visibility = View.VISIBLE
        } else {
            card.visibility = View.GONE
        }

        questionTitle.text = forumViewModel.currQuestion?.title ?: ""
        question.text = forumViewModel.currQuestion?.question ?: ""

        if (forumViewModel.currAnswers.value == null) {
//            forumViewModel.getListOfAnswers()
            forumViewModel.currAnswers.observe(requireActivity(),
                { listOfAnswers -> setupAnswerListAdapter(listOfAnswers) })
            forumViewModel.addAnswerSnapShot()
        } else {
            setupAnswerListAdapter(forumViewModel.currAnswers.value!!)
        }


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupAnswerListAdapter(answers: List<Answer>) {
        val context = requireContext()
        val adapter = AnswersAdapter()

        adapter.setAnswers(answers)
        Log.d("TAG_Q ans", answers.size.toString())

        val answersRecycler = binding.answerViewRecyclerView
        answersRecycler.adapter = adapter
        answersRecycler.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)

    }

    private fun newAnswerDialog(): Dialog {
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
                    //todo email
                    if (answer.text.toString() != "") {
                        forumViewModel.addNewAnswer(
                            Answer(email = "", answer = answer.text.toString())
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
//            builder.setCanceledOnTouchOutside(false);
//            builder.set
            builder.create()
        }
    }
}