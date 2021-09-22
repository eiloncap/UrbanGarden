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
import il.co.urbangarden.ui.MainViewModel


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
        val testPlant = Plant(imgFileName = "photos_test.jpg")
        setImgFromPath(testPlant, imageView)
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


    private fun setImgFromPath(plant: Plant, imageView: ImageView) {
        val userId = viewModel.user.value?.uid
        Log.d("eilon", "${userId}/${plant.imgFileName}")
        if (userId != null) {
            // Reference to an image file in Firebase Storage
            val storageReference: StorageReference =
                viewModel.storage.reference.child("${userId}/${plant.imgFileName}")

            // Download directly from StorageReference using Glide
            GlideApp.with(this)
                .load(storageReference)
                .centerCrop() // just makes the image cropped to square
                .into(imageView)
        }
    }

}