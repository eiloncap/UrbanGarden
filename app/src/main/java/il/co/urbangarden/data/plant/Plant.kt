package il.co.urbangarden.data.plant

import il.co.urbangarden.data.FirebaseObject
import il.co.urbangarden.data.FirebaseViewableObject
import java.util.*


data class Plant(
    override val uid: String = UUID.randomUUID().toString(),
    override val imgFileName: String = "$uid.jpeg",
    val name: String = "",
    val care: String = "",
    val sun: Int = 5

) : FirebaseViewableObject
