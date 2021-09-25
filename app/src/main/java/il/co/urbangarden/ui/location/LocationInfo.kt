package il.co.urbangarden.ui.location

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import il.co.urbangarden.R
import il.co.urbangarden.data.location.Location
import il.co.urbangarden.databinding.LocationInfoFragmentBinding
import il.co.urbangarden.databinding.MyLocationsFragmentBinding
import il.co.urbangarden.ui.MainViewModel
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


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.location_info_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val imgView : ImageView = view.findViewById(R.id.location_photo)
        val sunHours: EditText = view.findViewById(R.id.sun_hours)
        val name: EditText = view.findViewById(R.id.edit_name)
        val saveButton: Button = view.findViewById(R.id.save_button)
        val shareButton: FloatingActionButton = view.findViewById(R.id.share_button)
        val cameraButton: FloatingActionButton = view.findViewById(R.id.camera_button)

        mainViewModel.setImgFromPath(locationViewModel.location, imgView)
        sunHours.setText(locationViewModel.location.sunHours)
        name.setText(locationViewModel.location.name)

        saveButton.setOnClickListener {
            locationViewModel.location.name = name.editableText.toString()
            locationViewModel.location.sunHours = sunHours.editableText.toString()
            mainViewModel.uploadObject(locationViewModel.location)
            view.findNavController().navigate(R.id.action_locationInfo_to_navigation_home)
        }
        //todo share button on click and camera on click

    }


}