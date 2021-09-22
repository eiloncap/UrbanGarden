package il.co.urbangarden.data.plant

import il.co.urbangarden.data.FirebaseObject


data class Plant(
    override val uid: String = "",
    val name: String = "",
    val care: String = ""
): FirebaseObject
