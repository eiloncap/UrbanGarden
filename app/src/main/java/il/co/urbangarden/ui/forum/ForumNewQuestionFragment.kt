package il.co.urbangarden.ui.forum

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.navigateUp
import com.google.firebase.firestore.FirebaseFirestore
import il.co.urbangarden.data.forum.Question
import il.co.urbangarden.databinding.FragmentForumNewQuestionBinding
import il.co.urbangarden.ui.MainViewModel
import kotlinx.android.synthetic.main.one_plant.*
import java.util.*

class ForumNewQuestionFragment : Fragment() {

    private lateinit var mainViewModel: MainViewModel
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
        mainViewModel =
            ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        forumViewModel =
            ViewModelProvider(requireActivity()).get(ForumViewModel::class.java)

        _binding = FragmentForumNewQuestionBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val submitButton: Button = binding.addNewQuestion
        val addImage: Button = binding.addQuestionImage
        val title: EditText = binding.newQuestionTitle
        val question: EditText = binding.newQuestion
        val imgView: ImageView = binding.imageView

        var imgPath: String = ""

        val id = UUID.randomUUID()

        submitButton.setOnClickListener {
            //add new question to firebase
            val newQuestion = Question(
                uid = id.toString(),
                imgFileName = imgPath,
                email = mainViewModel.user?.email.toString(),
                userName = mainViewModel.user?.displayName.toString(),
                uri = mainViewModel.user?.photoUrl.toString(),
                title = title.text.toString(),
                question = question.text.toString(),
            )

            Log.d( "Tag_q_user","user: ${mainViewModel.user?.displayName}\n" +
                    "photo: ${mainViewModel.user?.photoUrl}")

            if (newQuestion.title == "") {
                Toast.makeText(requireContext(), "Please enter a title", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            forumViewModel.addNewQuestion(newQuestion, id)
            Log.d("TAG_Q new", title.text.toString())
            val navController = Navigation.findNavController(requireView())
            navController.navigateUp()
        }

        //init the camera launcher
        val resultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK
                && result.data != null
                && result?.data?.extras != null
            ) {
                mainViewModel.uploadImage(
                    result.data?.extras?.get("data") as Bitmap,
                    activity?.baseContext,
                    dir = "Forum",
                    filename = id.toString()
                )

                val imageBitmap = result.data?.extras?.get("data") as Bitmap
                imgView.setImageBitmap(imageBitmap)
                imgPath = "$id.jpeg"
            }
        }

        addImage.setOnClickListener {
            if (activity?.packageManager?.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY) == true) {
                val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                resultLauncher.launch(takePictureIntent)
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}