package il.co.urbangarden.ui

import android.content.Context
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import il.co.urbangarden.GlideApp
import il.co.urbangarden.data.location.Location
import il.co.urbangarden.data.plant.Plant
import il.co.urbangarden.data.user.User

class MainViewModel : ViewModel() {

    private val userUid = "12345"
    val storage = FirebaseStorage.getInstance()
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
//        // Get the Firebase app and all primitives we'll use
//        val app = FirebaseApp.getInstance()
//        val database = FirebaseDatabase.getInstance(app)
//        val auth = FirebaseAuth.getInstance(app)
//        val storage = FirebaseStorage.getInstance(app)
//        storage.reference.child("photos_test.jpg").downloadUrl.addOnSuccessListener { it. }
//        // Get a reference to our chat "room" in the database
//        val databaseRef = database.getReference("chat")

        loadUser()
        loadPlantsList()
        loadLocationsList()
        listenToChanges()
    }

    private fun loadUser() {
        _user.value = User(uid = "12345")
        db.collection(USERS_COLLECTION_TAG)
            .document(userUid).get()
            .addOnSuccessListener { d: DocumentSnapshot ->
                if (d.exists()) {
                    _user.value = d.toObject(User::class.java)
                    _user.value = User(uid = "12345")
                }
            }
            .addOnFailureListener {
//                    TODO: fail case
                _user.value = User(uid = "12345")
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