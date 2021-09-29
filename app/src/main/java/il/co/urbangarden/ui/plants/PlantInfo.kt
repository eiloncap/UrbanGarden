package il.co.urbangarden.ui.plants

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.Timestamp
import il.co.urbangarden.R
import il.co.urbangarden.data.FirebaseViewableObject
import il.co.urbangarden.data.location.Location
import il.co.urbangarden.ui.MainViewModel
import il.co.urbangarden.ui.home.HomeViewModel
import il.co.urbangarden.ui.location.LocationAdapter
import il.co.urbangarden.ui.location.LocationInfo
import il.co.urbangarden.utils.ImageCropOption
import kotlinx.android.synthetic.main.one_seggest_plant.*
import kotlinx.android.synthetic.main.plant_info_fragment.*
import kotlinx.android.synthetic.main.plant_info_fragment.view.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class PlantInfo : Fragment() {

    companion object {
        fun newInstance() = LocationInfo()
    }

    private lateinit var mainViewModel: MainViewModel
    private lateinit var plantsViewModel: MyPlantsViewModel
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var adapter: LocationAdapter

    private lateinit var imgView : ImageView
    private lateinit var nameText: TextView
    private lateinit var nameEdit: EditText
    //        val speciesText: TextView = view.findViewById(R.id.species)
//        val speciesEdit: EditText = view.findViewById(R.id.species_edit)
    private lateinit var lastWatringTitle: TextView
    private lateinit var lastStamp: TextView
    private lateinit var nextWatering: TextView
    private lateinit var inputDays: EditText
    private lateinit var days: TextView
    private lateinit var dropButton: FloatingActionButton
    private lateinit var notesText: TextView
    private lateinit var notesEdit: EditText
    private lateinit var saveButton: ExtendedFloatingActionButton
    private lateinit var shareButton: FloatingActionButton
    private lateinit var pencil: ImageView
    private lateinit var delete: ImageView
    private lateinit var locationImg: ImageView
    private lateinit var locationName: TextView



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.plant_info_fragment, container, false)
    }

    @SuppressLint("SimpleDateFormat")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        plantsViewModel = ViewModelProvider(requireActivity()).get(MyPlantsViewModel::class.java)
        homeViewModel = ViewModelProvider(requireActivity()).get(HomeViewModel::class.java)
        adapter = LocationAdapter()

        //finds views
        imgView = view.findViewById(R.id.plant_photo)
        nameText = view.findViewById(R.id.name)
        nameEdit = view.findViewById(R.id.edit_name)
//        val speciesText: TextView = view.findViewById(R.id.species)
//        val speciesEdit: EditText = view.findViewById(R.id.species_edit)
        lastWatringTitle = view.findViewById(R.id.last_watering)
        lastStamp = view.findViewById(R.id.time)
        dropButton = view.findViewById(R.id.watering_button)
        nextWatering = view.findViewById(R.id.next_watering)
        inputDays = view.findViewById(R.id.input_days)
        days = view.findViewById(R.id.days)
        notesText = view.findViewById(R.id.notes_text)
        notesEdit = view.findViewById(R.id.notes_edit)
        saveButton = view.findViewById(R.id.save_button)
        shareButton = view.findViewById(R.id.share_button)
        pencil = view.findViewById(R.id.pencil)
        delete = view.findViewById(R.id.delete)
        locationImg = view.findViewById(R.id.location_photo)
        locationName = view.findViewById(R.id.location_name)


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

        setViews()

        pencil.setOnClickListener {
            editMode()
        }

        nameEdit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                plantsViewModel.plant.name = s.toString()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        inputDays.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                plantsViewModel.plant.watering = s.toString().toInt()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        saveButton.setOnClickListener {
            if (plantsViewModel.plant.name.isEmpty()){
                Toast.makeText(context, "Please Enter Plant Name", Toast.LENGTH_SHORT).show()
            }
            else if (plantsViewModel.plant.watering == 0){
                Toast.makeText(context, "Please enter ", Toast.LENGTH_SHORT).show()
            }
            else {
                plantsViewModel.plant.watering = inputDays.text.toString().toInt()
                plantsViewModel.plant.name = nameEdit.text.toString()
//            plantsViewModel.plant.species = speciesEdit.text.toString()
                plantsViewModel.plant.notes = notesEdit.text.toString()
                showMode()
                mainViewModel.uploadObject(plantsViewModel.plant)
            }
        }

        delete.setOnClickListener {
            mainViewModel.removeObject(plantsViewModel.plant)
            homeViewModel.tab = 0
            view.findNavController().navigate(R.id.action_plantInfo_to_navigation_home)
        }

        imgView.setOnClickListener{
            val imgFileName: String = plantsViewModel.plant.uid + ".jpeg"
            plantsViewModel.plant.imgFileName = imgFileName

            if (activity?.packageManager?.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY) == true) {
                val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                resultLauncher.launch(takePictureIntent)
            }

        }

        dropButton.setOnClickListener {
            plantsViewModel.plant.lastWatered = Date()
            val dateFormat: DateFormat = SimpleDateFormat("dd-MM-yy  hh:mm")
            lastStamp.text = dateFormat.format(plantsViewModel.plant.lastWatered).toString()
        }

        locationImg.setOnClickListener {
            showDialog().show()
        }

        //todo share button on click
    }

    private fun setViews(){
        val dateFormat: DateFormat = SimpleDateFormat("dd-MM-yy  hh:mm")
        lastStamp.text = dateFormat.format(plantsViewModel.plant.lastWatered).toString()

        if (plantsViewModel.plant.locationUid.isNotEmpty()){
            val location: Location? = mainViewModel.getLocation(plantsViewModel.plant.locationUid)
            location.let{ mainViewModel.setImgFromPath(location!!, locationImg)
            locationName.text = location.name
            }
        }

        if(plantsViewModel.plant.imgFileName.isNotEmpty()){
            mainViewModel.setImgFromPath(plantsViewModel.plant, imgView)
        }
        if (plantsViewModel.plant.name.isEmpty()){
            editMode()
        }
        else {
            showMode()

        }
    }

    private fun editMode(){
        nameText.visibility = View.GONE
//        speciesText.visibility = View.GONE
        notesText.visibility = View.GONE
        pencil.visibility = View.GONE
        dropButton.visibility = View.GONE
        dropButton.isClickable = false
        lastStamp.visibility = View.GONE
        lastWatringTitle.visibility = View.GONE
        saveButton.visibility = View.VISIBLE
        saveButton.isClickable = true


        nextWatering.visibility = View.VISIBLE
        inputDays.visibility = View.VISIBLE
        days.visibility = View.VISIBLE
        nameEdit.visibility = View.VISIBLE
//        editSpecies.visibility = View.VISIBLE
        notesEdit.visibility = View.VISIBLE

        nameEdit.setText(plantsViewModel.plant.name)
//        editSpecies.setText(plantsViewModel.plant.species)
        notesEdit.setText(plantsViewModel.plant.notes)

    }

    private fun showMode(){
        nameText.visibility = View.VISIBLE
//        speciesText.visibility = View.VISIBLE
        notesText.visibility = View.VISIBLE
        pencil.visibility = View.VISIBLE
        dropButton.visibility = View.VISIBLE
        dropButton.isClickable = true
        lastStamp.visibility = View.VISIBLE
        lastWatringTitle.visibility = View.VISIBLE

        nameEdit.visibility = View.GONE
//        editSpecies.visibility = View.GONE
        notesEdit.visibility = View.GONE
        saveButton.visibility = View.GONE
        saveButton.isClickable = false
        nextWatering.visibility = View.GONE
        inputDays.visibility = View.GONE
        days.visibility = View.GONE

        nameText.text = plantsViewModel.plant.name
//        speciesText.text = plantsViewModel.plant.species
        notesText.text = plantsViewModel.plant.notes
    }


    private fun showDialog(): Dialog {
        return this.let {
            val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
            // Get the layout inflater
            val view = LayoutInflater.from(requireContext())
                .inflate(R.layout.location_dialog, null)

            val recyclerView: RecyclerView = view.findViewById(R.id.recycler_dialog)


            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            builder.setView(view)
                .setNegativeButton("Cancel") { dialog, id ->
                    dialog.dismiss()
                }
            builder.setCancelable(false);
            val dialog = builder.create()

            adapter.setLocationList(mainViewModel.locationsList.value)



            adapter.setImg = { plant: FirebaseViewableObject, img: ImageView ->
                mainViewModel.setImgFromPath(plant, img, ImageCropOption.SQUARE)
            }
            adapter.onItemClick = { location: Location ->
                plantsViewModel.plant.locationUid = location.uid
                dialog.dismiss()
                setViews()
            }

            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                false
            )

            dialog
        }
    }
}