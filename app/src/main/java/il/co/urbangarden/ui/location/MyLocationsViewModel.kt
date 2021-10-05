package il.co.urbangarden.ui.location

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import il.co.urbangarden.data.location.Location
import il.co.urbangarden.data.plant.Plant

class MyLocationsViewModel : ViewModel() {

    lateinit var location: Location
    var locationImg: Bitmap? = null

    //should do observe
    fun relevantPlants(location: Location, plantList:List<Plant>?): List<Plant> {
        return ArrayList(plantList)
            .filter { getSun(it) <= location.sunny }
            .sortedByDescending { getSun(it) }
    }

    fun getSun(plant: Plant): Int{
        return 5
    }

}