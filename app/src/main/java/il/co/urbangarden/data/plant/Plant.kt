package il.co.urbangarden.data.plant

import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.storage.StorageReference
import il.co.urbangarden.GlideApp
import il.co.urbangarden.ui.MainViewModel


data class Plant(
    val uid: String = "",
    val name: String = "",
    val imgFileName: String = "",
    val care: String = ""
)
