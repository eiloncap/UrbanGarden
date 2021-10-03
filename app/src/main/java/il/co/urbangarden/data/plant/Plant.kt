package il.co.urbangarden.data.plant

import il.co.urbangarden.data.FirebaseObject
import il.co.urbangarden.data.FirebaseViewableObject
import java.util.*


data class Plant(
    override val uid: String = UUID.randomUUID().toString(),
    override val imgFileName: String = "$uid.jpeg",
    val name: String = "",
    val watering: String = "",
    val season: String ="",
    val info: String ="",
    val placing: String="",
    val sun: Int = 0,
    val daysWatering: Int = 0

) : FirebaseViewableObject
