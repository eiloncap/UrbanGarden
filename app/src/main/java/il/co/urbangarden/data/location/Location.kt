package il.co.urbangarden.data.location

import il.co.urbangarden.data.FirebaseViewableObject
import java.util.*

data class Location(
    override var uid: String = UUID.randomUUID().toString(),
    override var imgFileName: String = "NoImageAvailable.jpg",
    var name: String = "",
    var sunHours: String = ""

) : FirebaseViewableObject
