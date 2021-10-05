package il.co.urbangarden.ui.camera

import android.app.Activity
import android.app.Dialog
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
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.graphics.scale
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import il.co.urbangarden.R
import il.co.urbangarden.data.location.Location
import il.co.urbangarden.data.plant.Plant
import il.co.urbangarden.data.plant.PlantInstance
import il.co.urbangarden.databinding.FragmentCameraBinding
import il.co.urbangarden.ml.Model
import org.tensorflow.lite.support.image.TensorImage
import il.co.urbangarden.ui.MainViewModel
import il.co.urbangarden.ui.location.MyLocationsViewModel
import il.co.urbangarden.ui.plants.MyPlantsViewModel
import il.co.urbangarden.utils.ImageCropOption
import java.util.*


class CameraNavigationFragment : Fragment() {
    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 1
    }

    private lateinit var locationViewModel: MyLocationsViewModel
    private lateinit var plantsViewModel: MyPlantsViewModel
    private lateinit var mainViewModel: MainViewModel
    private lateinit var launcher: ActivityResultLauncher<Intent>
    private lateinit var uid: String

    private var _binding: FragmentCameraBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        locationViewModel =
            ViewModelProvider(requireActivity()).get(MyLocationsViewModel::class.java)
        plantsViewModel = ViewModelProvider(requireActivity()).get(MyPlantsViewModel::class.java)
        uid = UUID.randomUUID().toString()

        val recognizeButton: Button = view.findViewById(R.id.recognize_button)
        val newPlant: Button = view.findViewById(R.id.new_plant_button)
        val newLocation: Button = view.findViewById(R.id.new_location_button)

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
                    uid //todo put uid
                )

            }
        }

        newPlant.setOnClickListener {
            val plant = PlantInstance()
            plant.uid = uid
            plant.imgFileName = plant.uid + ".jpeg"
            plantsViewModel.plant = plant
            Log.d("amitUid", plant.uid)
            if (activity?.packageManager?.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY) == true) {
                val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                resultLauncher.launch(takePictureIntent)
            }
            view.findNavController().navigate(R.id.action_navigation_camera_to_plantInfo)
        }

        newLocation.setOnClickListener {
            val location = Location()
            location.uid = uid
            location.imgFileName = location.uid + ".jpeg"
            locationViewModel.location = location
            if (activity?.packageManager?.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY) == true) {
                val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                resultLauncher.launch(takePictureIntent)
            }
            view.findNavController().navigate(R.id.action_navigation_camera_to_locationInfo)
        }
        prepareCameraIntent()

        recognizeButton.setOnClickListener {
            openCameraForRecognitionPicture()
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

    private fun prepareCameraIntent() {
        launcher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK
                && result.data != null
                && result?.data?.extras != null
            ) {
                val imageBitmap = (result.data?.extras?.get("data") as Bitmap).scale(224, 224)

                val model = Model.newInstance(requireContext())

                // Creates inputs for reference.
                val image = TensorImage.fromBitmap(imageBitmap)

                // Runs model inference and gets result.
                val outputs = model.process(image)
                val probability = outputs.probabilityAsCategoryList
                val res = probability.apply {
                    sortByDescending { it.score }
                }.take(1)

                // Releases model resources if no longer used.
                model.close()

                mainViewModel.getPlant(res[0].label).let {
                    if (it != null) {
                        classifiedPlantDialog(it).show()
                    }
                }
            }
        }
    }

    private fun openCameraForRecognitionPicture() {
        if (activity?.packageManager?.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY) == true) {
            launcher.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE))
        }
    }


    private fun classifiedPlantDialog(plant: Plant): Dialog {
        return this.let {
            val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
            // Get the layout inflater
            val view = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_plant_classification_result, null)
            val plantImg: ImageView = view.findViewById(R.id.plant_img)
            val title: TextView = view.findViewById(R.id.title)

            mainViewModel.setImgFromPath(plant, plantImg, ImageCropOption.SQUARE)
            title.text = "Looks like ${plant.name}!"

            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            builder.setView(view)
                .setPositiveButton("More Info") { dialog, id ->
                    newPlantDialog(plant).show()
                }
                .setNegativeButton("Ok") { dialog, id ->
                    dialog.dismiss()
                }
            builder.create()
        }
    }

    private fun newPlantDialog(plant: Plant): Dialog {
        return this.let {
            val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
            // Get the layout inflater
            val view = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_plant, null)

            val imgView: ImageView = view.findViewById(R.id.plant_photo)
            val name: TextView = view.findViewById(R.id.name)
            val sun: TextView = view.findViewById(R.id.sun_text)
            val water: TextView = view.findViewById(R.id.water_text)
            val season: TextView = view.findViewById(R.id.season_text)
            val placing: TextView = view.findViewById(R.id.placinf_text)
            val information: TextView = view.findViewById(R.id.information_text)

            mainViewModel.setImgFromPath(plant, imgView)
            name.text = plant.name
            sun.text = plant.sun.toString()
            water.text = plant.watering
            season.text = plant.season
            placing.text = plant.placing
            information.text = plant.info


            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            builder.setView(view)
                .setPositiveButton("Add to My Planys") { dialog, id ->
                    val newPlant = PlantInstance()
                    newPlant.species = plant.name
                    newPlant.speciesUid = plant.uid
                    plantsViewModel.plant  = newPlant
                    view.findNavController().navigate(R.id.action_navigation_camera_to_plantInfo)

                }
                .setNegativeButton("Cancel") { dialog, id ->

                }

            builder.setCancelable(false);
            builder.create()
        }
    }

}