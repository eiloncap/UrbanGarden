
package il.co.urbangarden.data.forum

import java.util.*
import kotlin.collections.ArrayList

data class Answer(
    val email: String = "",
    val userName: String = "",
    val uri: String = "",
    var answer: String = "",
    val date: Date = Date(),
)