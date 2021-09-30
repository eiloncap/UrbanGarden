package il.co.urbangarden.data.forum

import android.net.Uri
import com.google.firebase.firestore.CollectionReference
import il.co.urbangarden.data.FirebaseViewableObject
import java.util.*

data class Topic(
    val uid: String = "",
    var topic: String = "",
    var date: Date? = null,
    var questions: CollectionReference? = null,
    var numOfQuestions: Int = 0
)