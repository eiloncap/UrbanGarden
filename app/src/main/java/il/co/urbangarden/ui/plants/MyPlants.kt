package il.co.urbangarden.ui.plants

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import il.co.urbangarden.R
import il.co.urbangarden.data.FirebaseViewableObject
import il.co.urbangarden.ui.MainViewModel
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import il.co.urbangarden.data.plant.PlantInstance
import il.co.urbangarden.databinding.MyPlantsFragmentBinding

import il.co.urbangarden.ui.dragAndDrop.SimpleItemTouchHelperCallback
import il.co.urbangarden.utils.ImageCropOption


class MyPlants : Fragment() {

    private lateinit var plantsViewModel: MyPlantsViewModel
    private lateinit var mainViewModel : MainViewModel
    private var _binding: MyPlantsFragmentBinding? = null

    private val binding get() = _binding!!

    companion object {
        fun newInstance() = MyPlants()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        plantsViewModel = ViewModelProvider(requireActivity()).get(MyPlantsViewModel::class.java)
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)

        _binding = MyPlantsFragmentBinding.inflate(inflater, container, false)
        val root: View = _binding!!.root

        val plantsObserver = Observer<List<PlantInstance>> { plants ->
            setUpPlantsAdapter(plants)

        }
        mainViewModel.plantsList.observe(viewLifecycleOwner, plantsObserver)

        return root
    }

    private fun getListOfPlants(): List<PlantInstance>? {
        return mainViewModel.plantsList.value
    }

    private fun setUpPlantsAdapter(plants: List<PlantInstance>?){
        val context = requireContext()
        val adapter = PlantAdapter()
        Log.d("setup", "locationAdapter")

        adapter.setPlantList(plants)

        adapter.onItemClick = { plant: PlantInstance->
            plantsViewModel.plant = plant
            view?.findNavController()?.navigate(R.id.action_navigation_home_to_plantInfo)
        }

        adapter.setImg = { plant: FirebaseViewableObject, img: ImageView ->

            mainViewModel.setImgFromPath(plant, img, ImageCropOption.CIRCLE)
            Log.d("setImg", "success")
        }

        val plantsRecyclerView = binding.recyclerViewMyPlants
        plantsRecyclerView.adapter = adapter
        plantsRecyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)

        val callback: ItemTouchHelper.Callback = SimpleItemTouchHelperCallback(adapter)
        val touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(plantsRecyclerView)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        plantsViewModel = ViewModelProvider(requireActivity()).get(MyPlantsViewModel::class.java)
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)

        setUpPlantsAdapter(getListOfPlants())

        val plantsObserver = Observer<List<PlantInstance>> { plants ->
            setUpPlantsAdapter(plants)

        }
        mainViewModel.plantsList.observe(viewLifecycleOwner, plantsObserver)

        var addButton: Button = view.findViewById(R.id.add_button)

        addButton.setOnClickListener {
            var newPlant = PlantInstance()
            plantsViewModel.plant = newPlant
            view.findNavController().navigate(R.id.action_navigation_home_to_plantInfo)
        }


    }

}