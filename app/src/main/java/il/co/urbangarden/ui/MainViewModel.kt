package il.co.urbangarden.ui

import android.util.Log
import android.widget.ImageView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import il.co.urbangarden.GlideApp
import il.co.urbangarden.data.FirebaseObject
import il.co.urbangarden.data.FirebaseViewableObject
import il.co.urbangarden.data.location.Location
import il.co.urbangarden.data.plant.PlantInstance
import il.co.urbangarden.data.user.User
import il.co.urbangarden.utils.ImageCropOption

class MainViewModel : ViewModel() {

    private val userUid = "12345"
    val storage = FirebaseStorage.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val _user = MutableLiveData<User>()
    val user: LiveData<User> = _user
    private val _plantsList = MutableLiveData<List<PlantInstance>>()
    val plantsList: LiveData<List<PlantInstance>> = _plantsList
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
                val res = mutableListOf<PlantInstance>()
                it.forEach { plant ->
                    res.add(plant.toObject(PlantInstance::class.java))
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
                    val res = mutableListOf<PlantInstance>()
                    value.forEach { plant ->
                        res.add(plant.toObject(PlantInstance::class.java))
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

    fun uploadObject(item: FirebaseObject) {
        val collection: String = when (item) {
            is PlantInstance -> {
                USER_PLANTS_COLLECTION_TAG
            }
            is Location -> {
                USER_LOCATIONS_COLLECTION_TAG
            }
            else -> {
                throw IllegalArgumentException("item should be PlantInstance or Location")
            }
        }
        db.collection(USERS_COLLECTION_TAG)
            .document(userUid)
            .collection(collection)
            .document(item.uid).set(item)
    }

    fun setImgFromPath(
        item: FirebaseViewableObject,
        imageView: ImageView,
        crop: ImageCropOption = ImageCropOption.NONE
    ) {
        val userId = user.value?.uid
        if (userId != null) {
            // Reference to an image file in Firebase Storage
            val storageReference: StorageReference =
                storage.reference.child("${userId}/${item.imgFileName}")

            // Download directly from StorageReference using Glide
            GlideApp.with(imageView)
                .load(storageReference)
                .apply(crop.getGlideTransform())
                .into(imageView)
        }
    }

}