
package il.co.urbangarden.data.forum

import com.google.firebase.firestore.CollectionReference
import java.util.*
import kotlin.collections.ArrayList

data class Question(
    val email: String = "",
    var title: String = "",
    var question: String = "",
    var image: String = "",
    val date: Date = Date(),
    var answers: CollectionReference? = null,
    var numOfAnswers: Int = 0
)