package il.co.urbangarden.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import il.co.urbangarden.data.location.Location
import il.co.urbangarden.data.plant.Plant
import il.co.urbangarden.data.user.User

class MainViewModel : ViewModel() {

    private val userUid = "12345"
    private val db = FirebaseFirestore.getInstance()
    private val _user = MutableLiveData<User>()
    val user: LiveData<User> = _user
    private val _plantsList = MutableLiveData<List<Plant>>()
    val plantsList: LiveData<List<Plant>> = _plantsList
    private val _locationsList = MutableLiveData<List<Location>>()
    val locationsList: LiveData<List<Location>> = _locationsList

    companion object {
        private const val USERS_COLLECTION_TAG = "Users"
        private const val USER_PLANTS_COLLECTION_TAG = "plants"
        private const val USER_LOCATIONS_COLLECTION_TAG = "locations"
    }

    init {
        loadUser()
        loadPlantsList()
        loadLocationsList()
        listenToChanges()
    }

    private fun loadUser() {
        db.collection(USERS_COLLECTION_TAG)
            .document(userUid).get()
            .addOnSuccessListener { d: DocumentSnapshot ->
                if (d.exists()) {
                    _user.value = d.toObject(User::class.java)
                }
            }
            .addOnFailureListener {
//                    TODO: fail case
            }
    }

    private fun loadPlantsList() {
        db.collection(USERS_COLLECTION_TAG)
            .document(userUid)
            .collection(USER_PLANTS_COLLECTION_TAG).get()
            .addOnSuccessListener {
                val res = mutableListOf<Plant>()
                it.forEach { plant ->
                    res.add(plant.toObject(Plant::class.java))
                }
                _plantsList.value = res
            }
            .addOnFailureListener {
//                    TODO: fail case
            }
    }

    private fun loadLocationsList() {
        db.collection(USERS_COLLECTION_TAG)
            .document(userUid)
            .collection(USER_LOCATIONS_COLLECTION_TAG).get()
            .addOnSuccessListener {
                val res = mutableListOf<Location>()
                it.forEach { loc ->
                    res.add(loc.toObject(Location::class.java))
                }
                _locationsList.value = res
            }
            .addOnFailureListener {
//                    TODO: fail case
            }
    }

    private fun listenToChanges() {
        val userDoc = db.collection(USERS_COLLECTION_TAG).document(userUid)

        userDoc.addSnapshotListener { value, error ->
            if (error == null && value != null && value.exists()) {
                _user.value = value.toObject(User::class.java)
            }
        }

        userDoc.collection(USER_PLANTS_COLLECTION_TAG)
            .addSnapshotListener { value, error ->
                if (error == null && value != null) {
                    val res = mutableListOf<Plant>()
                    value.forEach { plant ->
                        res.add(plant.toObject(Plant::class.java))
                    }
                    _plantsList.value = res
                }
            }

        userDoc.collection(USER_LOCATIONS_COLLECTION_TAG)
            .addSnapshotListener { value, error ->
                if (error == null && value != null) {
                    val res = mutableListOf<Location>()
                    value.forEach { loc ->
                        res.add(loc.toObject(Location::class.java))
                    }
                    _locationsList.value = res
                }
            }
    }

    fun uploadUpdatePlant(plant: Plant) = uploadSomething(plant, plant.uid, USER_PLANTS_COLLECTION_TAG)

    fun uploadUpdateLocation(loc: Location) = uploadSomething(loc, loc.uid, USER_LOCATIONS_COLLECTION_TAG)

    private fun uploadSomething(item: Any, uid: String, collection: String) {
        db.collection(USERS_COLLECTION_TAG)
            .document(userUid)
            .collection(collection)
            .document(uid).set(item)
    }

}