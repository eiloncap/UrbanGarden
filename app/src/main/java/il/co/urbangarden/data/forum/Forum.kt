
package il.co.urbangarden.data.forum

data class Forum(
    var image: String = "",
    var title: String = "",
    var question: String = "",
    var comments: List<String> = ArrayList()
)