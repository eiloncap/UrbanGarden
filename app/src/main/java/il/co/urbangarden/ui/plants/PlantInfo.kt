package il.co.urbangarden.ui.plants

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import il.co.urbangarden.R
import il.co.urbangarden.data.plant.Plant
import il.co.urbangarden.ui.MainViewModel
import il.co.urbangarden.ui.location.LocationInfo
import kotlinx.android.synthetic.main.plant_info_fragment.*

class PlantInfo : Fragment() {

    companion object {
        fun newInstance() = LocationInfo()
    }

    private val mainViewModel: MainViewModel by lazy {
        ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
    }
    private val plantsViewModel: MyPlantsViewModel by lazy {
        ViewModelProvider(requireActivity()).get(MyPlantsViewModel::class.java)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.plant_info_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //finds views
        val imgView : ImageView = view.findViewById(R.id.plant_photo)
        val care: TextView = view.findViewById(R.id.notes)
        val name: EditText = view.findViewById(R.id.edit_name)
        val lastStamp: TextView = view.findViewById(R.id.watering_text)
        val saveButton: Button = view.findViewById(R.id.save_button)
        val shareButton: FloatingActionButton = view.findViewById(R.id.share_button)
        val cameraButton: FloatingActionButton = view.findViewById(R.id.camera_button)

        //sets views
        mainViewModel.setImgFromPath(plantsViewModel.plant, imgView)
        care.setText(plantsViewModel.plant.notes)
        name.setText(plantsViewModel.plant.name)

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
                    plantsViewModel.plant.uid
                )

                val imageBitmap = result.data?.extras?.get("data") as Bitmap
                imgView.setImageBitmap(imageBitmap)
            }
        }


        saveButton.setOnClickListener {
            val imgFileName: String = plantsViewModel.plant.uid + ".jpeg"
            plantsViewModel.plant.imgFileName = imgFileName
            plantsViewModel.plant.notes = notes.text.toString()
            plantsViewModel.plant.name = name.text.toString()
            mainViewModel.uploadObject(plantsViewModel.plant)
            view.findNavController().navigate(R.id.action_plantInfo_to_navigation_home)
        }
        //todo share button on click and camera on click

        cameraButton.setOnClickListener{
            if (activity?.packageManager?.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY) == true) {
                val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                resultLauncher.launch(takePictureIntent)
            }

        }
    }
}