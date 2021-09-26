package il.co.urbangarden.ui.forum

import android.app.Application
import android.app.Dialog
import android.util.Log
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import il.co.urbangarden.data.forum.Answer
import il.co.urbangarden.data.forum.Question
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.collections.ArrayList

class ForumViewModel : ViewModel() {

    private val firebase: FirebaseFirestore = FirebaseFirestore.getInstance()

    val questionsLiveData: MutableLiveData<ArrayList<Question>> = MutableLiveData()

    var currQuestion: Question? = null
    var answerNum: MutableLiveData<Int> = MutableLiveData()
    var currAnswers: MutableLiveData<ArrayList<Answer>> = MutableLiveData()

    fun getListOfQuestions() {
        val arrayList = ArrayList<Question>()
        val questionCollectionRef = firebase.collection("Forum")
        questionCollectionRef.get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val newQ = document.toObject(Question::class.java)

                    val answersCollectionRef: CollectionReference =
                        document.reference.collection("answers")
                    newQ.answers = answersCollectionRef

                    answersCollectionRef.get().addOnSuccessListener { ansResult ->
                        newQ.numOfAnswers = ansResult.size()
                        questionsLiveData.value = arrayList
                    }
//                    answerNum.observe(
//                        { num ->  })

                    arrayList.add(newQ)

                    Log.d(
                        "TAG_Q fetch",
                        "${document.id} => ${document.data} a:${answersCollectionRef}"
                    )
                }
                questionsLiveData.value = arrayList
            }
            .addOnFailureListener { exception ->
                Log.d("TAG_Q fetch fail", "Error getting documents: ", exception)
            }

//        return listOf(
//            Question(image = " ", title = " yoyoyoy yo"),
//            Question(
//                title = "what is wronng with my fucking plant the hel",
//                answers = listOf(
//                    "1",
//                    "2",
//                    "3",
//                    "asdfjs;alkdjflk alsdfjl; asdlkj  f sdf fdf  fdsf sdf lsdkafj"
//                )
//            ),
//            Question(),
//            Question(),
//            Question(),
//            Question()
//        )
    }

    fun getListOfAnswers() {
        val arrayList = ArrayList<Answer>()
        if (currQuestion?.answers != null) {
            val answersCollectionRef = currQuestion!!.answers
            answersCollectionRef?.get()?.addOnSuccessListener { result ->
                for (document in result) {
                    val newA = document.toObject(Answer::class.java)
                    arrayList.add(newA)

                    Log.d(
                        "TAG_Q fetch",
                        "${document.id} => ${document.data}"
                    )
                }
                currAnswers.value = arrayList
            }?.addOnFailureListener { exception ->
                Log.d("TAG_Q fetch fail", "Error getting documents: ", exception)
            }
        }
    }

    fun addQuestionSnapShot() {
        firebase.collection("Forum").addSnapshotListener { value, error ->
            if (value == null || error != null) {
                Log.e("TAG_Err", "error")
            } else if (!value.isEmpty) {
                Log.d("TAG_Err", "change")
                Log.d("TAG_Err", value.toString())
                getListOfQuestions()
            }
        }
    }

    fun addAnswerSnapShot() {
        currQuestion?.answers?.addSnapshotListener { value, error ->
            if (value == null || error != null) {
                Log.e("TAG_Err", "error")
            } else if (!value.isEmpty) {
                Log.d("TAG_Err", "change")
                Log.d("TAG_Err", value.toString())
                getListOfAnswers()
            }
        }
    }

    fun addNewQuestion(question: Question) {
        val id = UUID.randomUUID().toString()
//        orderLiveData.value = order
        Log.d("TAG_Q new id", id)
//        val add = firebase.collection("Forum").add(question)
        val add = firebase.collection("Forum").document(id).set(question)
        add.addOnSuccessListener {
            Log.d("TAG_Q SUCS", id)
        }
    }

    fun addNewAnswer(answer: Answer) {
        val id = UUID.randomUUID().toString()
//        orderLiveData.value = order
        Log.d("TAG_Q new ans id", id)
//        val add = currQuestion!!.answers!!.add(answer)
        val add = currQuestion!!.answers!!.document(id).set(answer)
        add.addOnSuccessListener {
            Log.d("TAG_Q SUCS", id)
            currQuestion!!.numOfAnswers += 1
        }
    }
}