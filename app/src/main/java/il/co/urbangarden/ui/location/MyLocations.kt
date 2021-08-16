package il.co.urbangarden.ui.location

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import il.co.urbangarden.R

class MyLocations : Fragment() {

    companion object {
        fun newInstance() = MyLocations()
    }

    private lateinit var viewModel: MyLocationsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.my_locations_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MyLocationsViewModel::class.java)
        // TODO: Use the ViewModel
    }

}