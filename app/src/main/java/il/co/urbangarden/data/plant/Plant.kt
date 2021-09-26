package il.co.urbangarden.data.plant

import il.co.urbangarden.data.FirebaseObject
import il.co.urbangarden.data.FirebaseViewableObject


data class Plant(
    override val uid: String = "",
    override val imgFileName: String = "",
    val name: String = "",
    val care: String = "",
    val sun: Int = 5

) : FirebaseViewableObject
