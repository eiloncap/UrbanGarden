package il.co.urbangarden.ui.location

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import il.co.urbangarden.R
import il.co.urbangarden.data.FirebaseViewableObject
import il.co.urbangarden.data.location.Location
import il.co.urbangarden.databinding.MyLocationsFragmentBinding
import il.co.urbangarden.ui.MainViewModel


class MyLocations : Fragment() {

    private lateinit var locationsViewModel: MyLocationsViewModel
    private lateinit var mainViewModel : MainViewModel
    private var _binding: MyLocationsFragmentBinding? = null

    private val binding get() = _binding!!

    companion object {
        fun newInstance() = MyLocations()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        locationsViewModel = ViewModelProvider(requireActivity()).get(MyLocationsViewModel::class.java)
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)

        _binding = MyLocationsFragmentBinding.inflate(inflater, container, false)
        val root: View = _binding!!.root

        val locationObserver = Observer<List<Location>> { locations ->
            setUpLocationAdapter(locations)

        }
        mainViewModel.locationsList.observe(viewLifecycleOwner, locationObserver)

        return root
    }

    private fun getListOfLocations(): List<Location>? {
        return mainViewModel.locationsList.value
    }

    private fun setUpLocationAdapter(locations: List<Location>?){
        val context = requireContext()
        val adapter = LocationAdapter()

        adapter.setLocationList(locations)

        adapter.onItemClick = { location: Location ->
            locationsViewModel.location = location
            view?.findNavController()?.navigate(R.id.action_navigation_home_to_locationInfo)
        }

        adapter.setImg = { location: FirebaseViewableObject, img: ImageView ->
            mainViewModel.setImgFromPath(location, img)
        }

        val locationsRecyclerView = binding.recyclerViewMyLocations
        locationsRecyclerView.adapter = adapter
        locationsRecyclerView.layoutManager = GridLayoutManager(context, 2)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

}