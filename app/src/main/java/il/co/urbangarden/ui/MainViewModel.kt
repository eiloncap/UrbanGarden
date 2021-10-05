package il.co.urbangarden.ui

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.net.Uri
import android.util.Log
import android.widget.ImageView
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStore
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import il.co.urbangarden.GlideApp
import il.co.urbangarden.data.FirebaseObject
import il.co.urbangarden.data.FirebaseViewableObject
import il.co.urbangarden.data.forum.Question
import il.co.urbangarden.data.location.Location
import il.co.urbangarden.data.plant.Plant
import il.co.urbangarden.data.plant.PlantInstance
import il.co.urbangarden.utils.ImageCropOption
import il.co.urbangarden.utils.ImageUriConverter
import java.io.ByteArrayOutputStream
import java.io.File


class MainViewModel : ViewModel() {
    var user = Firebase.auth.currentUser
    private var userUid: String? = user?.uid
    private val storage = FirebaseStorage.getInstance()
    val db = FirebaseFirestore.getInstance()
    val plantsLiveData: MutableLiveData<ArrayList<Plant>> = MutableLiveData()
    private val _plantsList = MutableLiveData<List<PlantInstance>>()
    val plantsList: LiveData<List<PlantInstance>> = _plantsList
    private val _locationsList = MutableLiveData<List<Location>>()
    val locationsList: LiveData<List<Location>> = _locationsList

    companion object {
        private const val PLANTS_IMAGES_STORAGE_TAG = "Plants"
        private const val FORUM_IMAGES_STORAGE_TAG = "Forum"
        private const val USERS_COLLECTION_TAG = "Users"
        private const val USER_PLANTS_COLLECTION_TAG = "plants"
        private const val USER_LOCATIONS_COLLECTION_TAG = "locations"
    }

    fun signOut(act: FragmentActivity) {
        userUid = null
        user = null
        Firebase.auth.signOut()
        act.viewModelStore.clear()
    }

    private fun getObjectImageDirectory(obj: FirebaseViewableObject): String {
        return when (obj) {
            is Plant -> {
                PLANTS_IMAGES_STORAGE_TAG
            }
            is Question -> {
                FORUM_IMAGES_STORAGE_TAG
            }
            else -> {
                userUid!!.toString()
            }
        }
    }

    fun loadDb() {
        loadPlantsList()
        loadLocationsList()
        listenToChanges()
        getListOfPlants()
    }

    private fun loadPlantsList() {
        db.collection(USERS_COLLECTION_TAG)
            .document(userUid!!)
            .collection(USER_PLANTS_COLLECTION_TAG).get()
            .addOnSuccessListener {
                val res = mutableListOf<PlantInstance>()
                it.forEach { plant ->
                    res.add(plant.toObject(PlantInstance::class.java))
                }
                _plantsList.value = res
            }
            .addOnFailureListener {
                Log.d("failed", "fail")
            }
    }

    private fun loadLocationsList() {
        db.collection(USERS_COLLECTION_TAG)
            .document(userUid!!)
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
        val userDoc = db.collection(USERS_COLLECTION_TAG).document(userUid!!)

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
            .document(userUid!!)
            .collection(collection)
            .document(item.uid).set(item)
    }

    fun removeObject(item: FirebaseObject) {
        val collection: String = when (item) {
            is PlantInstance -> {
                deletePlantFromLocation(item)
                USER_PLANTS_COLLECTION_TAG
            }
            is Location -> {
                deleteLocationFromPlants(item)
                USER_LOCATIONS_COLLECTION_TAG
            }
            else -> {
                throw IllegalArgumentException("item should be PlantInstance or Location")
            }
        }
        db.collection(USERS_COLLECTION_TAG)
            .document(userUid!!)
            .collection(collection)
            .document(item.uid).delete()
    }

    fun setImgFromPath(
        item: FirebaseViewableObject,
        imageView: ImageView,
        crop: ImageCropOption = ImageCropOption.NONE
    ) {
        val dir = getObjectImageDirectory(item)

        // Reference to an image file in Firebase Storage
        val storageReference: StorageReference =
            storage.reference.child("$dir/${item.imgFileName}")

        // Download directly from StorageReference using Glide
        var load = GlideApp.with(imageView)
            .load(storageReference)
        if (item !is Plant && item !is Question) {
            load = load.skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
        }
        load.apply(crop.getGlideTransform())
            .into(imageView)
    }

    fun callbackOnImageDownloadedFromStorage(
        ctx: Context,
        item: FirebaseViewableObject,
        callback: (Uri) -> Unit
    ) {
        val dir = getObjectImageDirectory(item)

        // Reference to an image file in Firebase Storage
        val storageReference: StorageReference =
            storage.reference.child("$dir/${item.imgFileName}")
        val thread: Thread = object : Thread() {
            override fun run() {
                // Download directly from StorageReference using Glide
                val bm = GlideApp.with(ctx)
                    .asBitmap()
                    .load(storageReference)
                    .submit()
                    .get()
                callback(ImageUriConverter.getImageUri(ctx, bm))
            }
        }
        thread.start()
    }

    fun uploadImage(
        imgBitmap: Bitmap?,
        inContext: Context?,
        filename: String,
        dir: String = userUid.toString(),
        callback: (() -> Unit)? = null
    ) {
        if (imgBitmap != null && inContext != null) {
            Log.d("eilon-cam-d", "uploading $filename to $dir")
            val img = ImageUriConverter.getImageUri(inContext, imgBitmap)

            // Defining the child of storageReference
            val ref: StorageReference = storage.reference
                .child("$dir/$filename.jpeg")


            // adding listeners on upload or failure of image
            ref.putFile(img)
                .addOnSuccessListener { // Image uploaded successfully
                    // TODO: implement
                    Log.d("eilon", "succeed uploading")
//                    _locationsList.value = _locationsList.value
                    if(callback != null) {
                        callback()
                    }
                }
                .addOnFailureListener { e -> // Error, Image not uploaded
                    // TODO: implement
                    Log.d("eilon", "failure uploading: $e")
                }
        }
    }

    fun getPlant(label: String): Plant? {
        return plantsLiveData.value?.firstOrNull { it.name == label }
    }

    fun getPlantInstance(uid: String): PlantInstance? {
        return _plantsList.value?.firstOrNull { it.uid == uid }
    }

    fun getLocation(uid: String): Location? {
        return _locationsList.value?.firstOrNull { it.uid == uid }
    }

    fun getPlantsByLocation(loc: Location): ArrayList<PlantInstance> {
        val res = ArrayList<PlantInstance>()
        _plantsList.value?.forEach {
            if (it.locationUid == loc.uid) {
                res.add(it)
            }
        }
        return res
    }

    private fun getListOfPlants() {
        val arrayList = ArrayList<Plant>()
        val plantsCollectionRef = db.collection("Plants")
        plantsCollectionRef.get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val newP = document.toObject(Plant::class.java)
                    arrayList.add(newP)
                }

                Log.d("eilon-re", "loaded $arrayList")
                plantsLiveData.value = arrayList
            }
            .addOnFailureListener { exception ->
            }
    }

    fun deletePlantFromLocation(plant: PlantInstance) {
        val location = getLocation(plant.locationUid)
        val mutableList: MutableList<String> = ArrayList()
        location?.plants?.let { mutableList.addAll(it) }
        mutableList.remove(plant.uid)
        if (location != null) {
            location.plants = mutableList
            uploadObject(location)
        }
    }

    fun deleteLocationFromPlants(location: Location) {
        val plantsByLocation = getPlantsByLocation(location)
        plantsByLocation.forEach {
            it.locationUid = ""
            uploadObject(it)
        }
    }
}