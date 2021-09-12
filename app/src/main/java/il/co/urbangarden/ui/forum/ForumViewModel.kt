package il.co.urbangarden.ui.forum

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import il.co.urbangarden.data.forum.Question

class ForumViewModel : ViewModel() {

    var question: Question? = null
}