package il.co.urbangarden.data.user

import il.co.urbangarden.data.FirebaseObject

data class User(
    override var uid: String = "",
    var name: String = ""
) : FirebaseObject
