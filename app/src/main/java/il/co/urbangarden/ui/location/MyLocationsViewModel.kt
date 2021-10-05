package il.co.urbangarden.ui.location

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import il.co.urbangarden.data.location.Location
import il.co.urbangarden.data.plant.Plant
import kotlin.math.abs
import kotlin.math.min

class MyLocationsViewModel : ViewModel() {

    lateinit var location: Location
    var locationImg: Bitmap? = null

    //should do observe
    fun relevantPlants(location: Location, plantList: List<Plant>?): List<Plant> {
        return ArrayList(plantList)
            .filter { filter(it, location.sunny) }
            .sortedByDescending { sort(it, location.sunny) }
    }

    private fun getSun(plant: Plant): Pair<Int, Int> {
        val (bottom, top) = plant.sun.split("-")
        return Pair(bottom.toInt(), top.toInt())
    }

    private fun filter(plant: Plant, sun: Int): Boolean {
        val (bottom, top) = getSun(plant)
        return sun in bottom..top
    }

    private fun sort(plant: Plant, sun: Int): Int {
        val (bottom, top) = getSun(plant)
        return min(abs(sun - bottom), abs(top - sun))
    }
}