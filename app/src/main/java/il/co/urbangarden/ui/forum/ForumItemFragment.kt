package il.co.urbangarden.ui.forum

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import il.co.urbangarden.R
import il.co.urbangarden.data.forum.Answer
import il.co.urbangarden.data.forum.Question
import il.co.urbangarden.databinding.FragmentForumItemBinding
import il.co.urbangarden.ui.MainViewModel
import il.co.urbangarden.utils.ImageCropOption
import il.co.urbangarden.utils.ShareExecutor
import java.text.DateFormat
import java.text.SimpleDateFormat


//todo enlage photo on press
//todo share forum and plant


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

        val currQuestion = forumViewModel.currQuestion

        binding.answers2.text = currQuestion?.numOfAnswers.toString()
        val dateFormat: DateFormat = SimpleDateFormat("dd-MMM-yy  HH:mm")
        binding.date2.text = dateFormat.format(currQuestion?.date).toString()

        val questionTitle: TextView = binding.questionViewTitle
        val question: TextView = binding.questionView
        val card = binding.card
        val img: ImageView = binding.viewImage
        if (currQuestion?.imgFileName ?: "" != "") {
            currQuestion?.let {
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

        questionTitle.text = currQuestion?.title ?: ""
        if (currQuestion?.question == "") {
            question.visibility = View.GONE
        } else {
            if (currQuestion != null) {
                question.text = currQuestion.question
            }
        }
        binding.userName.text = currQuestion?.userName ?: ""
        Glide.with(requireContext())
            .load(currQuestion?.uri)
            .circleCrop()
            .into(binding.avatarIcon2)

        val deleteButton = binding.delete
        if (currQuestion!!.email != mainViewModel.user!!.email) {
            deleteButton.visibility = View.GONE
        } else {
            deleteButton.setOnClickListener {
                Log.d("Tag_q delete", "deleted")
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("Are you sure you want to delete the question?")
                    .setPositiveButton("Delete") { dialog, id ->
                        //delete from firebase
                        forumViewModel.currTopic!!.questions!!.document(currQuestion.uid).delete()
                        val navController = Navigation.findNavController(requireView())
                        forumViewModel.currTopic!!.numOfQuestions -= 1
                        navController.navigateUp()
                    }
                    .setNegativeButton("Cancel") { dialog, id ->
                    }.show()
            }
        }


        card.setOnClickListener {
            bigPhotoDialog(currQuestion).show()
        }


        // set answers
        if (forumViewModel.currAnswers.value == null) {
//            forumViewModel.getListOfAnswers()
            forumViewModel.currAnswers.observe(requireActivity(),
                { listOfAnswers -> setupAnswerListAdapter(listOfAnswers) })
            forumViewModel.addAnswerSnapShot()
        } else {
            setupAnswerListAdapter(forumViewModel.currAnswers.value!!)
        }

        setShareButton(currQuestion)

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupAnswerListAdapter(answers: List<Answer>) {
        val context = requireActivity()
        val adapter = AnswersAdapter()

        adapter.setAnswers(answers)
//        Log.d("TAG_Q ans", answers.size.toString())

        adapter.setAvatar = { imageView, uri ->
            Glide.with(context)
                .load(uri)
                .circleCrop()
                .into(imageView)
        }
        if (_binding != null) {
            val answersRecycler = binding.answerViewRecyclerView
            answersRecycler.adapter = adapter
            val manager = object : LinearLayoutManager(context, RecyclerView.VERTICAL, false) {
                override fun canScrollVertically(): Boolean {
                    return false
                }
            }

//        answersRecycler.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            answersRecycler.layoutManager = manager
        }
    }

    private fun setShareButton(currQuestion: Question) {
        if (forumViewModel.currQuestion != null) {
            binding.share.setOnClickListener {
                mainViewModel.callbackOnImageDownloadedFromStorage(
                    requireContext(),
                    currQuestion
                ) { uri ->
                    ShareExecutor.shareContent(
                        requireContext(),
                        "check this question by ${currQuestion.userName}:\n\n" +
                                "${currQuestion.title}\n" +
                                currQuestion.question, uri
                    )
                }
            }
        }
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
                    if (answer.text.toString() != "") {
                        forumViewModel.addNewAnswer(
                            Answer(
                                email = mainViewModel.user?.email.toString(),
                                userName = mainViewModel.user?.displayName.toString(),
                                uri = mainViewModel.user?.photoUrl.toString(),
                                answer = answer.text.toString()
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
            builder.setCancelable(false)
//            builder.setCanceledOnTouchOutside(false);
//            builder.set
            builder.create()
        }
    }

    private fun bigPhotoDialog(currQuestion: Question?): Dialog {
        return this.let {
            val builder = AlertDialog.Builder(requireContext())
            // Get the layout inflater
            val view = LayoutInflater.from(requireContext())
                .inflate(R.layout.preview_image, null)
            val ivPreview: ImageView = view.findViewById(R.id.iv_preview_image)

            currQuestion?.let {
                mainViewModel.setImgFromPath(
                    it,
                    ivPreview
                )
            }

            builder.setView(view)
            val ad = builder.create()
            ivPreview.setOnClickListener {
                ad.dismiss()
            }
            ad.getWindow()!!.setLayout(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
            ad
        }
    }
}