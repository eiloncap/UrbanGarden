package il.co.urbangarden.data.plant

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import il.co.urbangarden.data.FirebaseViewableObject
import java.util.*

data class PlantInstance(
    override val uid: String = UUID.randomUUID().toString(),
    override var imgFileName: String = "$uid.jpeg",
    var lastWatered: Timestamp = Timestamp(0, 0),
    var name: String = "",
    var notes: String = "",
    var locationUid: String? = null,
    val speciesUid: String? = null
) : FirebaseViewableObject