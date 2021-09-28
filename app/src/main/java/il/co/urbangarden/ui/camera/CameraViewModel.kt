package il.co.urbangarden.ui.camera

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CameraViewModel : ViewModel() {

    lateinit var fileName: String
    lateinit var state: String
}