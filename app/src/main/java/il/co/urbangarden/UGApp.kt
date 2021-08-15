package il.co.urbangarden

import android.app.Application
import com.google.firebase.FirebaseApp

class UGApp : Application(){
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}