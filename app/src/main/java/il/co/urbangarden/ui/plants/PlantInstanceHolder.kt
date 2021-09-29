package il.co.urbangarden.ui.plants

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import il.co.urbangarden.R

class PlantInstanceHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var view: View
    var image: ImageView
    var name: TextView
    var lastWatering: TextView
    var dropButton: FloatingActionButton



    init {
        view = itemView
        image = itemView.findViewById(R.id.image)
        name = itemView.findViewById(R.id.title)
        lastWatering = itemView.findViewById(R.id.time)
        dropButton = itemView.findViewById(R.id.watering_button)
    }
}
