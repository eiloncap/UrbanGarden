package il.co.urbangarden.ui.forum

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import il.co.urbangarden.data.forum.Question

class ForumViewModel : ViewModel() {

    val firebase: FirebaseFirestore = FirebaseFirestore.getInstance()

    val questionsLiveData: MutableLiveData<ArrayList<Question>> =
        MutableLiveData<ArrayList<Question>>()

    var question: Question? = null

}