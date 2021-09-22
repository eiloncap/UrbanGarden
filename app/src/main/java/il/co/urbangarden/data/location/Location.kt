package il.co.urbangarden.data.location

import il.co.urbangarden.data.FirebaseViewableObject

data class Location(
    override var uid: String = "",
    override val imgFileName: String = "",
    var name: String = ""
) : FirebaseViewableObject
