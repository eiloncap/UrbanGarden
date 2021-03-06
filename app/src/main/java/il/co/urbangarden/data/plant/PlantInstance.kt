package il.co.urbangarden.data.plant

import android.location.Location
import com.google.firebase.Timestamp
import il.co.urbangarden.data.FirebaseViewableObject
import java.util.*

data class PlantInstance(
    override var uid: String = UUID.randomUUID().toString(),
    override var imgFileName: String = "",
    var lastWatered: Date = Date(),
    var name: String = "",
    var notes: String = "",
    var locationUid: String = "",
    var speciesUid: String = "",
    var species: String = "",
    var wateringDays: Int = 0,
) : FirebaseViewableObject