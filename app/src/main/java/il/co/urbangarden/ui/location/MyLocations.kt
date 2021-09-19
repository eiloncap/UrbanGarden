package il.co.urbangarden.ui.location

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import il.co.urbangarden.R


class MyLocations : Fragment() {
    private var dataList = mutableListOf<LocationDataClass>()
    private var recyclerView: RecyclerView? = null
    private var layoutManager: RecyclerView.LayoutManager? = null
    private var locationAdapter: RecyclerView.Adapter<LocationAdapter.ViewHolder>? = null


    companion object {
        fun newInstance() = MyLocations()
    }

    private lateinit var viewModel: MyLocationsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment

        // Add the following lines to create RecyclerView

        return inflater.inflate(R.layout.my_locations_fragment, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MyLocationsViewModel::class.java)
        // TODO: Use the ViewModel
        //add data
        recyclerView = view?.findViewById<View>(R.id.recyclerViewMyLocations) as RecyclerView?
        locationAdapter = LocationAdapter()
        recyclerView?.layoutManager = GridLayoutManager(context, 2)
        recyclerView?.adapter = locationAdapter
        Log.d("recycleView", recyclerView.toString())

    }

}