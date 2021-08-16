package il.co.urbangarden.ui.plants

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import il.co.urbangarden.R

class PlantInfo : Fragment() {

    companion object {
        fun newInstance() = PlantInfo()
    }

    private lateinit var viewModel: PlantInfoViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.plant_info_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(PlantInfoViewModel::class.java)
        // TODO: Use the ViewModel
    }

}