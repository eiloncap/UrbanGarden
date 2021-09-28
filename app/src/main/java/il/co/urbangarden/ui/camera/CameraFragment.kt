package il.co.urbangarden.ui.camera

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import il.co.urbangarden.R
import il.co.urbangarden.data.location.Location
import il.co.urbangarden.data.plant.PlantInstance
import il.co.urbangarden.databinding.FragmentCamera2Binding
import il.co.urbangarden.databinding.FragmentCameraBinding
import il.co.urbangarden.ui.MainViewModel
import il.co.urbangarden.ui.location.MyLocationsViewModel
import il.co.urbangarden.ui.plants.MyPlantsViewModel

class CameraFragment : Fragment() {

    private lateinit var cameraViewModel: CameraViewModel
    private lateinit var mainViewModel: MainViewModel

    private var _binding: FragmentCamera2Binding? = null

    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("amit", "what")

        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        Log.d("amit", "the")
        cameraViewModel = ViewModelProvider(this).get(CameraViewModel::class.java)
        Log.d("amit", "fuck")

        Log.d("amit1", cameraViewModel.fileName)

//        openCameraTakePictureAndUploadToDb(cameraViewModel.fileName)

        Log.d("amit", cameraViewModel.state)
        navigateFragmentByState(view)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCamera2Binding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    private fun navigateFragmentByState(view: View){
        when(cameraViewModel.state){
            "plant" -> view.findNavController().navigate(R.id.action_cameraFragment_to_plantInfo)
            "location" -> view.findNavController().navigate(R.id.action_cameraFragment_to_locationInfo)
        }
    }

    private fun openCameraTakePictureAndUploadToDb(fileName: String) {
        // TODO: init the following launcher at the creation of the fragment
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
                    fileName //todo put uid
                )

//                val imageBitmap = result.data?.extras?.get("data") as Bitmap
//                imageView.setImageBitmap(imageBitmap)
            }
        }

        // TODO: call this when you want to start the camera
        if (activity?.packageManager?.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY) == true) {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            resultLauncher.launch(takePictureIntent)
        }
    }

}