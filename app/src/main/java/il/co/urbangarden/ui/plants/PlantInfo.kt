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
        val imgView : ImageView = view.findViewById(R.id.plant_photo)
        val nameText: TextView = view.findViewById(R.id.name)
        val nameEdit: EditText = view.findViewById(R.id.edit_name)
//        val speciesText: TextView = view.findViewById(R.id.species)
//        val speciesEdit: EditText = view.findViewById(R.id.species_edit)
        val lastStamp: TextView = view.findViewById(R.id.time)
        val dropButton: FloatingActionButton = view.findViewById(R.id.watering_button)
        val notesText: TextView = view.findViewById(R.id.notes_text)
        val notesEdit: EditText = view.findViewById(R.id.notes_edit)
        val saveButton: ExtendedFloatingActionButton = view.findViewById(R.id.save_button)
        val shareButton: FloatingActionButton = view.findViewById(R.id.share_button)
        val pencil: ImageView = view.findViewById(R.id.pencil)
        val delete: ImageView = view.findViewById(R.id.delete)
        val locationImg: ImageView = view.findViewById(R.id.location_photo)
        val locationName: TextView = view.findViewById(R.id.location_name)


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

        setViews(nameText, nameEdit, notesText, notesEdit, saveButton, pencil, imgView, locationImg, lastStamp, locationName)

        pencil.setOnClickListener {
            editMode(nameText, nameEdit, notesText, notesEdit, saveButton, pencil)
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

        saveButton.setOnClickListener {
            plantsViewModel.plant.name = nameEdit.text.toString()
//            plantsViewModel.plant.species = speciesEdit.text.toString()
            plantsViewModel.plant.notes = notesEdit.text.toString()
            mainViewModel.uploadObject(plantsViewModel.plant)
            showMode(nameText, nameEdit, notesText, notesEdit, saveButton, pencil)
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

    private fun setViews(
        nameText: TextView,
        nameEdit: EditText,
        notesText: TextView,
        notesEdit: EditText,
        saveButton: ExtendedFloatingActionButton,
        pencil: ImageView,
        imgView: ImageView,
        locationImgView: ImageView,
        lastStamp: TextView,
        locationName: TextView
    ){
        val dateFormat: DateFormat = SimpleDateFormat("dd-MM-yy  hh:mm")
        lastStamp.text = dateFormat.format(plantsViewModel.plant.lastWatered).toString()

        if (plantsViewModel.plant.locationUid.isNotEmpty()){
            val location: Location? = mainViewModel.getLocation(plantsViewModel.plant.locationUid)
            location.let{ mainViewModel.setImgFromPath(location!!, locationImgView)
            locationName.text = location.name
            }
        }

        if(plantsViewModel.plant.imgFileName.isNotEmpty()){
            mainViewModel.setImgFromPath(plantsViewModel.plant, imgView)
        }
        if (plantsViewModel.plant.name.isEmpty()){
            saveButton.visibility = View.GONE
            saveButton.isClickable = false
            editMode(nameText, nameEdit, notesText, notesEdit, saveButton, pencil)
        }
        else {
            showMode(nameText, nameEdit,notesText, notesEdit, saveButton, pencil)

        }
    }

    private fun editMode(
        nameText: TextView,
        editName: EditText,
        notesText: TextView,
        editNotes: EditText,
        saveButton: ExtendedFloatingActionButton,
        pencil: ImageView
    ){
        nameText.visibility = View.GONE
//        speciesText.visibility = View.GONE
        notesText.visibility = View.GONE
        pencil.visibility = View.GONE

        if (plantsViewModel.plant.name.isNotEmpty()){
            saveButton.visibility = View.VISIBLE
            saveButton.isClickable = true
        }

        editName.visibility = View.VISIBLE
//        editSpecies.visibility = View.VISIBLE
        editNotes.visibility = View.VISIBLE

        editName.setText(plantsViewModel.plant.name)
//        editSpecies.setText(plantsViewModel.plant.species)
        editNotes.setText(plantsViewModel.plant.notes)

    }

    private fun showMode(nameText: TextView, editName: EditText, notesText: TextView, editNotes: EditText,
                         saveButton: ExtendedFloatingActionButton, pencil: ImageView){
        nameText.visibility = View.VISIBLE
//        speciesText.visibility = View.VISIBLE
        notesText.visibility = View.VISIBLE
        pencil.visibility = View.VISIBLE

        editName.visibility = View.GONE
//        editSpecies.visibility = View.GONE
        editNotes.visibility = View.GONE
        saveButton.visibility = View.GONE
        saveButton.isClickable = false

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