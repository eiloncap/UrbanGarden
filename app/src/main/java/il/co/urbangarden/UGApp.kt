package il.co.urbangarden

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import il.co.urbangarden.data.plant.Plant

class UGApp : Application(){


    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }

}