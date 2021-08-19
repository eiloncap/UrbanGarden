
package il.co.urbangarden.data.forum

import java.util.*
import kotlin.collections.ArrayList

data class Question(
    val email: String = "",
    val date: Date = Date(),
    var image: String = "",
    var title: String = "",
    var question: String = "",
    var answers: List<String> = ArrayList()
)