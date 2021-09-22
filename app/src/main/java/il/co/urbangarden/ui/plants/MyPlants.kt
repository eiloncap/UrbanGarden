package il.co.urbangarden.ui.plants

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.storage.StorageReference
import il.co.urbangarden.GlideApp
import il.co.urbangarden.R
import il.co.urbangarden.data.plant.Plant
import il.co.urbangarden.data.plant.PlantInstance
import il.co.urbangarden.ui.MainViewModel
import il.co.urbangarden.utils.ImageCropOption


class MyPlants : Fragment() {

    companion object {
        fun newInstance() = MyPlants()
    }

    private lateinit var viewModel: MainViewModel
    private lateinit var fragViewModel: MyPlantsViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        fragViewModel = ViewModelProvider(this).get(MyPlantsViewModel::class.java)

        // todo: delete
        val imageView: ImageView = view.findViewById(R.id.img)
        val testPlant = PlantInstance(uid="1994", imgFileName = "photos_test.jpg")
        viewModel.uploadObject(testPlant)
        viewModel.setImgFromPath(testPlant, imageView, crop=ImageCropOption.SQUARE)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.my_plants_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

}