package il.co.urbangarden.data.plant

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import il.co.urbangarden.data.FirebaseViewableObject

data class PlantInstance(
    override val uid: String = "",
    override val imgFileName: String = "",
    val lastWatered: Timestamp = Timestamp(0, 0),
    val name: String = "",
    val notes: String = "",
    val species: DocumentReference? = null
) : FirebaseViewableObject