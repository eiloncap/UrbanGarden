package il.co.urbangarden.data.plant

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference


data class Plant(
    val uid: String = "",
    val name: String = "",
    val care: String = ""
)
