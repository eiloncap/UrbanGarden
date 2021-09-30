package il.co.urbangarden.ui.forum

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import il.co.urbangarden.data.forum.Answer
import il.co.urbangarden.data.forum.Question
import il.co.urbangarden.data.forum.Topic
import java.util.*
import kotlin.collections.ArrayList

class ForumViewModel : ViewModel() {

    private val firebase: FirebaseFirestore = FirebaseFirestore.getInstance()

    val topicsLiveData: MutableLiveData<ArrayList<Topic>> = MutableLiveData()
    var questionsLiveData: MutableLiveData<ArrayList<Question>> = MutableLiveData()
    var currAnswers: MutableLiveData<ArrayList<Answer>> = MutableLiveData()

    var currTopic: Topic? = null
    var currQuestion: Question? = null

    private fun getListOfTopics() {
        val arrayList = ArrayList<Topic>()
        val topicCollectionRef = firebase.collection("Forum")
        topicCollectionRef.get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val newT = document.toObject(Topic::class.java)

                    val questionsCollectionRef: CollectionReference =
                        document.reference.collection("questions")
                    newT.questions = questionsCollectionRef

                    questionsCollectionRef.get().addOnSuccessListener { ansResult ->
                        newT.numOfQuestions = ansResult.size()
                        topicsLiveData.value = arrayList
                    }
                    arrayList.add(newT)

//                    Log.d(
//                        "TAG_Q fetch",
//                        "${document.id} => ${document.data} a:${answersCollectionRef}"
//                    )
                }
                topicsLiveData.value = arrayList
            }
            .addOnFailureListener { exception ->
                Log.d("TAG_Q fetch fail", "Error getting documents: ", exception)
            }
    }

    private fun getListOfQuestions() {
        val arrayList = ArrayList<Question>()
        if (currTopic?.questions != null) {
            val questionCollectionRef = currTopic!!.questions
            questionCollectionRef?.get()
                ?.addOnSuccessListener { result ->
                    for (document in result) {
                        val newQ = document.toObject(Question::class.java)

                        val answersCollectionRef: CollectionReference =
                            document.reference.collection("answers")
                        newQ.answers = answersCollectionRef

                        answersCollectionRef.get().addOnSuccessListener { ansResult ->
                            newQ.numOfAnswers = ansResult.size()
                            questionsLiveData.value = arrayList
                        }
                        arrayList.add(newQ)

//                    Log.d(
//                        "TAG_Q fetch",
//                        "${document.id} => ${document.data} a:${answersCollectionRef}"
//                    )
                    }
                    questionsLiveData.value = arrayList
                }
                ?.addOnFailureListener { exception ->
                    Log.d("TAG_Q fetch fail", "Error getting documents: ", exception)
                }
        }
    }

    private fun getListOfAnswers() {
        val arrayList = ArrayList<Answer>()
        if (currQuestion?.answers != null) {
            val answersCollectionRef = currQuestion!!.answers
            answersCollectionRef?.get()?.addOnSuccessListener { result ->
                for (document in result) {
                    val newA = document.toObject(Answer::class.java)
                    arrayList.add(newA)

                    Log.d(
                        "TAG_Q fetch answers",
                        "${document.id} => ${document.data}"
                    )
                }
                currAnswers.value = arrayList
            }?.addOnFailureListener { exception ->
                Log.d("TAG_Q fetch fail", "Error getting documents: ", exception)
            }
        }
    }

    fun addTopicSnapShot() {
        firebase.collection("Forum").addSnapshotListener { value, error ->
            if (value == null || error != null) {
                Log.e("TAG_Err", "error")
            } else if (!value.isEmpty) {
                Log.d("TAG_Err", "change")
                Log.d("TAG_Err", value.toString())
                getListOfTopics()
            }
        }
    }

    fun addQuestionSnapShot() {
        currTopic?.questions?.addSnapshotListener { value, error ->
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

    fun addNewTopic(topic: Topic) {
        val id = UUID.randomUUID().toString()
        Log.d("TAG_Q new ans id", id)
        val add = firebase.collection("Forum").document(id).set(topic)
        add.addOnSuccessListener {
            Log.d("TAG_Q SUCS", id)
        }
    }

    fun addNewQuestion(question: Question, id :UUID) {
        val idStr = id.toString()
        Log.d("TAG_Q new id", idStr)
        val add = currTopic!!.questions!!.document(idStr).set(question)
        add.addOnSuccessListener {
            Log.d("TAG_Q SUCS", idStr)
            currTopic!!.numOfQuestions += 1
            currTopic!!.date = Date()
        }
    }

    fun addNewAnswer(answer: Answer) {
        val id = UUID.randomUUID().toString()
        Log.d("TAG_Q new ans id", id)
        val add = currQuestion!!.answers!!.document(id).set(answer)
        add.addOnSuccessListener {
            Log.d("TAG_Q SUCS", id)
            currQuestion!!.numOfAnswers += 1
        }
    }
}