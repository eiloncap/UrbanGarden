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
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import il.co.urbangarden.R
import il.co.urbangarden.data.location.Location
import il.co.urbangarden.data.plant.PlantInstance
import il.co.urbangarden.databinding.FragmentCameraBinding
import il.co.urbangarden.ui.MainViewModel
import il.co.urbangarden.ui.location.MyLocationsViewModel
import il.co.urbangarden.ui.plants.MyPlantsViewModel


class CameraNavigationFragment : Fragment() {
    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 1
    }

    private lateinit var locationViewModel: MyLocationsViewModel
    private lateinit var plantsViewModel: MyPlantsViewModel
    private lateinit var cameraViewModel: CameraViewModel
    private lateinit var mainViewModel: MainViewModel

    private var _binding: FragmentCameraBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        cameraViewModel = ViewModelProvider(this).get(CameraViewModel::class.java)
        locationViewModel = ViewModelProvider(this).get(MyLocationsViewModel::class.java)
        plantsViewModel = ViewModelProvider(this).get(MyPlantsViewModel::class.java)

        val recognizeButton: Button = view.findViewById(R.id.recognize_button)
        val newPlant: Button = view.findViewById(R.id.new_plant_button)
        val newLocation: Button = view.findViewById(R.id.new_location_button)

        newPlant.setOnClickListener {
            val newPlant = PlantInstance()
            plantsViewModel.plant = newPlant
            val imgFileName = newPlant.uid
            cameraViewModel.fileName = imgFileName
            cameraViewModel.state = 2
            view.findNavController().navigate(R.id.action_navigation_camera_to_cameraFragment)
        }

        newLocation.setOnClickListener {
            val newLocation = Location()
            locationViewModel.location = newLocation
            val imgFileName = newLocation.uid
            cameraViewModel.fileName = imgFileName
            cameraViewModel.state = 3
            view.findNavController().navigate(R.id.action_navigation_camera_to_cameraFragment)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCameraBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}