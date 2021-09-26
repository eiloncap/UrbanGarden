package il.co.urbangarden.ui.location

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import il.co.urbangarden.data.location.Location
import il.co.urbangarden.data.plant.Plant

class MyLocationsViewModel : ViewModel() {

    private val firebase: FirebaseFirestore = FirebaseFirestore.getInstance()

    val plantsLiveData: MutableLiveData<ArrayList<Plant>> = MutableLiveData()

    lateinit var location: Location

    //should do observe

    fun relevantPlants(location: Location): List<Plant> {
//val currySorted = spices.filter { it.contains("curry", true) }.sortedBy { it.length }
        return ArrayList(plantsLiveData.value)
            .filter { it.sun <= location.sunny }
            .sortedByDescending { it.sun }
    }

    fun getListOfPlants() {
        val arrayList = ArrayList<Plant>()
        val plantsCollectionRef = firebase.collection("Plants")
        plantsCollectionRef.get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val newP = document.toObject(Plant::class.java)

                    arrayList.add(newP)

                }
                plantsLiveData.value = arrayList
            }
            .addOnFailureListener { exception ->
            }

    }
}