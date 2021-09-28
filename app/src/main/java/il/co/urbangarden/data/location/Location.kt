package il.co.urbangarden.data.location

import il.co.urbangarden.data.FirebaseViewableObject
import java.util.*

data class Location(
    override var uid: String = UUID.randomUUID().toString(),
    override var imgFileName: String = "$uid.jpeg",
    var name: String = "",
    var sunHours: String = "",
    var sunny: Int = 1, // should be 1-10. only plants that are below (or the exact) number can be here
    var plants: List<String> = emptyList()

) : FirebaseViewableObject