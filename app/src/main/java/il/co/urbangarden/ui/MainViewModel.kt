package il.co.urbangarden.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.net.Uri
import android.util.Log
import android.widget.ImageView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bumptech.glide.load.engine.DiskCacheStrategy
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
import java.io.ByteArrayOutputStream
import java.io.File


class MainViewModel : ViewModel() {
    val user = Firebase.auth.currentUser
    private val userUid: String? = user?.uid
    private val storage = FirebaseStorage.getInstance()
    private val db = FirebaseFirestore.getInstance()
    val plantsLiveData: MutableLiveData<ArrayList<Plant>> = MutableLiveData()
    private val _plantsList = MutableLiveData<List<PlantInstance>>()
    val plantsList: LiveData<List<PlantInstance>> = _plantsList
    private val _locationsList = MutableLiveData<List<Location>>()
    val locationsList: LiveData<List<Location>> = _locationsList

    companion object {
        private var timesCreated = 0
        private const val USERS_COLLECTION_TAG = "Users"
        private const val USER_PLANTS_COLLECTION_TAG = "plants"
        private const val USER_LOCATIONS_COLLECTION_TAG = "locations"
    }

    fun loadDb() {
//        userUid = Firebase.auth.currentUser?.uid
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
            .document(item.uid).delete()
    }

    fun setImgFromPath(
        item: FirebaseViewableObject,
        imageView: ImageView,
        crop: ImageCropOption = ImageCropOption.NONE
    ) {
        val dir: String = when (item) {
            is Plant -> {
                "Plants"
            }
            is Question ->{
                "Forum"
            }
            else -> {
                userUid!!.toString()
            }
        }

        // Reference to an image file in Firebase Storage
        val storageReference: StorageReference =
            storage.reference.child("$dir/${item.imgFileName}")

        // Download directly from StorageReference using Glide
        GlideApp.with(imageView)
            .load(storageReference)
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .apply(crop.getGlideTransform())
            .into(imageView)
    }

    private fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
        val file = File(inContext.cacheDir, "lcl_urban_garden") // Get Access to a local file.
        file.delete() // Delete the File, just in Case, that there was still another File
        file.createNewFile()
        val fileOutputStream = file.outputStream()
        val bytes = ByteArrayOutputStream()
        inImage.compress(CompressFormat.JPEG, 100, bytes)
        val bytearray = bytes.toByteArray()
        fileOutputStream.write(bytearray)
        fileOutputStream.flush()
        fileOutputStream.close()
        bytes.close()
        return Uri.fromFile(file)
    }

    fun uploadImage(imgBitmap: Bitmap?, inContext: Context?, filename: String, dir:String=userUid.toString()) {
        if (imgBitmap != null && inContext != null) {
            val img = getImageUri(inContext, imgBitmap) ?: return // TODO: deal better
            // Defining the child of storageReference
            val ref: StorageReference = storage.reference
                .child("$dir/$filename.jpeg") // TODO: set in users directory with UID

            // adding listeners on upload
            // or failure of image
            ref.putFile(img)
                .addOnSuccessListener { // Image uploaded successfully
                    // TODO: implement
                    Log.d("eilon", "succeed uploading")
                    _locationsList.value = _locationsList.value
                }
                .addOnFailureListener { e -> // Error, Image not uploaded
                    // TODO: implement
                    Log.d("eilon", "failure uploading: $e")
                }
//                .addOnProgressListener(
//                    // TODO: implement
//                    object : OnProgressListener<UploadTask.TaskSnapshot?>() {
//                        // Progress Listener for loading
//                        // percentage on the dialog box
//                        fun onProgress(
//                            taskSnapshot: UploadTask.TaskSnapshot
//                        ) {
//                            val progress = ((100.0
//                                    * taskSnapshot.bytesTransferred
//                                    / taskSnapshot.totalByteCount))
//                            progressDialog.setMessage(
//                                ("Uploaded "
//                                        + progress.toInt() + "%")
//                            )
//                        }
//                    })

        }
    }


    fun getListOfPlants() {
        val arrayList = ArrayList<Plant>()
        val plantsCollectionRef = db.collection("Plants")
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