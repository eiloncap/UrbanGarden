package il.co.urbangarden.ui.location

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
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
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import il.co.urbangarden.R
import il.co.urbangarden.data.FirebaseViewableObject
import il.co.urbangarden.data.plant.Plant
import il.co.urbangarden.ui.MainViewModel
import il.co.urbangarden.ui.home.HomeViewModel
import il.co.urbangarden.ui.location.suggestPlants.PlantAdapter
import il.co.urbangarden.utils.ImageCropOption
import kotlinx.android.synthetic.main.location_info_fragment.*
import kotlin.math.min


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
        val locationImgView: ImageView = view.findViewById(R.id.location_photo)
        val sunHours: TextView = view.findViewById(R.id.num_hours)
        val minus: ImageView = view.findViewById(R.id.minus)
        val plus: ImageView = view.findViewById(R.id.plus)
        val name: TextView = view.findViewById(R.id.name)
        val nameEdit: EditText = view.findViewById(R.id.name_edit)
        val saveButton: ExtendedFloatingActionButton = view.findViewById(R.id.save_button)
        val shareButton: FloatingActionButton = view.findViewById(R.id.share_button)
        val getPlantsButton: FloatingActionButton = view.findViewById(R.id.get_plant_button)
        val getPlantText: TextView = view.findViewById(R.id.get_text)
        val pencil: ImageView = view.findViewById(R.id.pencil)
        val delete: ImageView = view.findViewById(R.id.delete)

        setViews(locationImgView, sunHours, minus, plus,name, nameEdit, saveButton, shareButton, pencil, getPlantText, getPlantsButton)

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
                locationImgView.setImageBitmap(imageBitmap)
            }
        }

        pencil.setOnClickListener {
            editMode(sunHours, minus, plus, name, nameEdit, saveButton, shareButton, pencil, getPlantText, getPlantsButton)
        }

        nameEdit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.toString().isEmpty()){
                    saveButton.visibility = View.GONE
                    saveButton.isClickable = false
                }
                else{
                    saveButton.visibility = View.VISIBLE
                    saveButton.isClickable = true
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        delete.setOnClickListener {
            mainViewModel.removeObject(locationViewModel.location)
            homeViewModel.tab = 1
            view.findNavController().navigate(R.id.action_locationInfo_to_navigation_home)
        }

        minus.setOnClickListener {
            if (locationViewModel.location.sunny == 10){
                plus.visibility = View.VISIBLE
            }
            if (locationViewModel.location.sunny > 1){
                locationViewModel.location.sunny -= 1
                sunHours.text = locationViewModel.location.sunny.toString()
            }
            if (locationViewModel.location.sunny == 1){
                minus.visibility = View.GONE
            }
        }

        plus.setOnClickListener {
            if (locationViewModel.location.sunny == 1){
                minus.visibility = View.VISIBLE
            }
            if (locationViewModel.location.sunny < 10){
                locationViewModel.location.sunny += 1
                sunHours.text = locationViewModel.location.sunny.toString()
            }
            if (locationViewModel.location.sunny == 10){
                plus.visibility = View.GONE
            }
        }

        saveButton.setOnClickListener {

            locationViewModel.location.name = nameEdit.text.toString()
            locationViewModel.location.sunny = sunHours.text.toString().toInt()
            mainViewModel.uploadObject(locationViewModel.location)
            homeViewModel.tab = 1
            showMode(sunHours, minus, plus, name, nameEdit, saveButton, shareButton, pencil, getPlantText, getPlantsButton)
        }

        locationImgView.setOnClickListener {
            val imgFileName: String = locationViewModel.location.uid + ".jpeg"
            locationViewModel.location.imgFileName = imgFileName
            mainViewModel.uploadObject(locationViewModel.location)
            if (activity?.packageManager?.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY) == true) {
                val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                resultLauncher.launch(takePictureIntent)
            }
        }

        getPlantsButton.setOnClickListener {
            showDialog().show()
        }
        //todo share button on click and camera on click
    }

    private fun setViews(
        locationImgView: ImageView,
        sunHours: TextView,
        minus: ImageView,
        plus: ImageView,
        name: TextView,
        nameEdit: EditText,
        saveButton: ExtendedFloatingActionButton,
        shareButton: FloatingActionButton,
        pencil: ImageView,
        getPlantText: TextView,
        getPlantsButton: FloatingActionButton
    ) {
        if (locationViewModel.location.name.isEmpty()){
            editMode(sunHours, minus, plus, name, nameEdit, saveButton, shareButton, pencil, getPlantText, getPlantsButton)
        }
        else{
            if (locationViewModel.location.imgFileName.isNotEmpty()){
                mainViewModel.setImgFromPath(locationViewModel.location, locationImgView, ImageCropOption.SQUARE)
            }
            showMode(sunHours, minus, plus, name, nameEdit, saveButton, shareButton, pencil,getPlantText, getPlantsButton)
        }

    }

    private fun showMode(
        sunHours: TextView,
        minus: ImageView,
        plus: ImageView,
        name: TextView,
        nameEdit: EditText,
        saveButton: ExtendedFloatingActionButton,
        shareButton: FloatingActionButton,
        pencil: ImageView,
        plantText: TextView,
        plantsButton: FloatingActionButton
    ) {
        minus.visibility = View.GONE
        plus.visibility = View.GONE
        nameEdit.visibility = View.GONE
        saveButton.visibility = View.GONE
        saveButton.isClickable = false

        pencil.visibility = View.VISIBLE
        name.visibility = View.VISIBLE
        shareButton.visibility = View.VISIBLE
        plantText.visibility = View.VISIBLE
        plantsButton.visibility = View.VISIBLE
        plantsButton.isClickable = true

        name.text = locationViewModel.location.name

        if (locationViewModel.location.sunny > 0){
            sunHours.text = locationViewModel.location.sunny.toString()
        }

    }

    private fun editMode(
        sunHours: TextView,
        minus: ImageView,
        plus: ImageView,
        name: TextView,
        nameEdit: EditText,
        saveButton: ExtendedFloatingActionButton,
        shareButton: FloatingActionButton,
        pencil: ImageView,
        getPlantText: TextView,
        getPlantsButton: FloatingActionButton
    ) {
        minus.visibility = View.VISIBLE
        plus.visibility = View.VISIBLE
        nameEdit.visibility = View.VISIBLE

        if (locationViewModel.location.name.isNotEmpty()){
            nameEdit.setText(locationViewModel.location.name)
            saveButton.visibility = View.VISIBLE
            saveButton.isClickable = true
        }

        pencil.visibility = View.GONE
        name.visibility = View.GONE
        shareButton.visibility = View.GONE
        getPlantText.visibility = View.GONE
        getPlantsButton.visibility = View.GONE
        getPlantsButton.isClickable = false

        if (locationViewModel.location.sunny > 0){
            sunHours.text = locationViewModel.location.sunny.toString()
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