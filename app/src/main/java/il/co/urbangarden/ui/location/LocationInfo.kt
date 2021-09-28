package il.co.urbangarden.ui.location

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import il.co.urbangarden.R
import il.co.urbangarden.data.FirebaseViewableObject
import il.co.urbangarden.data.plant.Plant
import il.co.urbangarden.ui.MainViewModel
import il.co.urbangarden.ui.home.HomeViewModel
import il.co.urbangarden.ui.location.suggestPlants.PlantAdapter
import il.co.urbangarden.utils.ImageCropOption
import kotlinx.android.synthetic.main.location_info_fragment.*


class LocationInfo : Fragment() {

    companion object {
        fun newInstance() = LocationInfo()
    }

    private lateinit var mainViewModel: MainViewModel
    private lateinit var locationViewModel: MyLocationsViewModel
    private lateinit var homeViewModel: HomeViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.location_info_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        locationViewModel = ViewModelProvider(requireActivity()).get(MyLocationsViewModel::class.java)
        homeViewModel = ViewModelProvider(requireActivity()).get(HomeViewModel::class.java)


        //finds views
        val imgView: ImageView = view.findViewById(R.id.location_photo)
        val sunHours: EditText = view.findViewById(R.id.sun_hours)
        val name: EditText = view.findViewById(R.id.edit_name)
        val saveButton: Button = view.findViewById(R.id.save_button)
        val shareButton: FloatingActionButton = view.findViewById(R.id.share_button)
        val getPlantsButton: FloatingActionButton = view.findViewById(R.id.get_plant_button)

        //sets views
        mainViewModel.setImgFromPath(locationViewModel.location, imgView)
        sunHours.setText(locationViewModel.location.sunny.toString())
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
            locationViewModel.location.sunny = sunHours.text.toString().toInt()
            mainViewModel.uploadObject(locationViewModel.location)
            homeViewModel.tab = 1
            view.findNavController()
                .navigate(il.co.urbangarden.R.id.action_locationInfo_to_navigation_home)
        }
        //todo share button on click and camera on click

        cameraButton.setOnClickListener {
            if (activity?.packageManager?.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY) == true) {
                val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                resultLauncher.launch(takePictureIntent)
            }
        }

        getPlantsButton.setOnClickListener {
            showDialog().show()
        }
    }


    private fun showDialog(): Dialog {
        return this.let {
            val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
            // Get the layout inflater
            val view = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_recycler, null)

            val recyclerView: RecyclerView = view.findViewById(R.id.recycler_dialog)
            val adapter = PlantAdapter()

            var wantedList: List<Plant> = ArrayList()
            if (mainViewModel.plantsLiveData.value == null) {
                mainViewModel.plantsLiveData.observe(requireActivity(),
                    {
                        wantedList = locationViewModel.relevantPlants(
                            locationViewModel.location,
                            mainViewModel.plantsLiveData.value
                        )
                        adapter.setPlantList(wantedList)
                    })
            } else {
                wantedList = locationViewModel.relevantPlants(
                    locationViewModel.location,
                    mainViewModel.plantsLiveData.value
                )
                adapter.setPlantList(wantedList)
            }


            adapter.onItemClick = { plant: Plant ->
                newPlantDialog(plant).show()
            }

            adapter.setImg = { plant: FirebaseViewableObject, img: ImageView ->
                mainViewModel.setImgFromPath(plant, img, ImageCropOption.SQUARE)
            }

            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                false
            )

            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            builder.setView(view)
                .setNegativeButton("Cancel") { dialog, id ->
                }
            builder.setCancelable(false);
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
            val care: TextView = view.findViewById(R.id.care)

            mainViewModel.setImgFromPath(plant, imgView)
            name.text = plant.name
            care.text = plant.care

            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            builder.setView(view)
                .setNegativeButton("Cancel") { dialog, id ->
                }

            builder.setCancelable(false);
            builder.create()
        }
    }

}