package il.co.urbangarden.ui.plants

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import il.co.urbangarden.data.plant.PlantInstance

class MyPlantsViewModel : ViewModel() {
    lateinit var plant: PlantInstance
    var plantImg: Bitmap? = null
}