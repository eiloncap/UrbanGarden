package il.co.urbangarden.ui.plants

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import il.co.urbangarden.R

class MyPlants : Fragment() {

    companion object {
        fun newInstance() = MyPlants()
    }

    private lateinit var viewModel: MyPlantsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.my_plants_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MyPlantsViewModel::class.java)
        // TODO: Use the ViewModel
    }

}