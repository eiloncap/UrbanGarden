package il.co.urbangarden.ui.camera

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import il.co.urbangarden.databinding.FragmentCameraBinding
import il.co.urbangarden.ui.MainViewModel


class CameraFragment : Fragment() {
    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 1
    }

    private lateinit var cameraViewModel: CameraViewModel
    private var _binding: FragmentCameraBinding? = null
    private lateinit var viewModel: MainViewModel

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        cameraViewModel =
            ViewModelProvider(this).get(CameraViewModel::class.java)

        _binding = FragmentCameraBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textDashboard
        cameraViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun openCameraTakePictureAndUploadToDb() {
        val resultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK
                && result.data != null
                && result?.data?.extras != null
            ) {
                viewModel.uploadImage(
                    result.data?.extras?.get("data") as Bitmap,
                    activity?.baseContext,
                    "test" // TODO: set filename to be documents uid
                )
            }
        }

        if (activity?.packageManager?.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY) == true) {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            resultLauncher.launch(takePictureIntent)
        }
    }
}