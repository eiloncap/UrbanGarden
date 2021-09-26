package il.co.urbangarden.ui.location

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
import il.co.urbangarden.data.location.Location
import il.co.urbangarden.ui.MainViewModel
import il.co.urbangarden.ui.home.HomeViewModel
import kotlinx.android.synthetic.main.location_info_fragment.*
import org.w3c.dom.Text

class LocationInfo : Fragment() {

    companion object {
        fun newInstance() = LocationInfo()
    }

    private val mainViewModel: MainViewModel by lazy {
        ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
    }
    private val locationViewModel: MyLocationsViewModel by lazy {
        ViewModelProvider(requireActivity()).get(MyLocationsViewModel::class.java)
    }

    private val homeViewModel: HomeViewModel by lazy {
        ViewModelProvider(requireActivity()).get(HomeViewModel::class.java)
    }




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.location_info_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //finds views
        val imgView : ImageView = view.findViewById(R.id.location_photo)
        val sunHours: EditText = view.findViewById(R.id.sun_hours)
        val name: EditText = view.findViewById(R.id.edit_name)
        val saveButton: Button = view.findViewById(R.id.save_button)
        val shareButton: FloatingActionButton = view.findViewById(R.id.share_button)
        val cameraButton: FloatingActionButton = view.findViewById(R.id.camera_button)

        //sets views
        mainViewModel.setImgFromPath(locationViewModel.location, imgView)
        sunHours.setText(locationViewModel.location.sunHours)
        name.setText(locationViewModel.location.name)

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
                    locationViewModel.location.uid
                )

                val imageBitmap = result.data?.extras?.get("data") as Bitmap
                imgView.setImageBitmap(imageBitmap)
            }
        }


        saveButton.setOnClickListener {
            val imgFileName: String = locationViewModel.location.uid + ".jpeg"
            locationViewModel.location.name = edit_name.text.toString()
            locationViewModel.location.imgFileName = imgFileName
            locationViewModel.location.sunHours = sunHours.text.toString()
            mainViewModel.uploadObject(locationViewModel.location)
            homeViewModel.tab = 1
            view.findNavController().navigate(R.id.action_locationInfo_to_navigation_home)
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