package il.co.urbangarden.ui.forum

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.navigateUp
import com.google.firebase.firestore.FirebaseFirestore
import il.co.urbangarden.data.forum.Question
import il.co.urbangarden.databinding.FragmentForumNewQuestionBinding
import java.util.*

class ForumNewQuestionFragment : Fragment() {

    private lateinit var forumViewModel: ForumViewModel
    private var _binding: FragmentForumNewQuestionBinding? = null

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

        _binding = FragmentForumNewQuestionBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val submitButton: Button = binding.addNewQuestion
        val addImage: Button = binding.addQuestionImage
        val title: EditText = binding.newQuestionTitle
        val question: EditText = binding.newQuestion

        submitButton.setOnClickListener {
            //todo add new question to firebase
            val newQuestion = Question(
                email = "user",
                title = title.text.toString(),
                question = question.text.toString(),
                date = Date()
            )
            //todo check not empty
            if (newQuestion.title == ""){
                Toast.makeText(requireContext(), "Please enter a title", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            forumViewModel.addNewQuestion(newQuestion)
            Log.d("TAG_Q new", title.text.toString())
//            parentFragmentManager.popBackStack()
//            parentFragmentManager.popBackStackImmediate()
//            view?.findNavController().navigateUp()
            val navController = Navigation.findNavController(requireView())
            navController.navigateUp()
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}