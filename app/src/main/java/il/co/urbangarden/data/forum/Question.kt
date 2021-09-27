package il.co.urbangarden.data.forum

import com.google.firebase.firestore.CollectionReference
import il.co.urbangarden.data.FirebaseViewableObject
import java.util.*
import kotlin.collections.ArrayList

data class Question(
    override val uid: String = "",
    override val imgFileName: String = "",
    val email: String = "",
    var title: String = "",
    var question: String = "",
    val date: Date = Date(),
    var answers: CollectionReference? = null,
    var numOfAnswers: Int = 0
) : FirebaseViewableObject