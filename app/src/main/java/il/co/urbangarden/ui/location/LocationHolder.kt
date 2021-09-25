package il.co.urbangarden.ui.location

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import il.co.urbangarden.R

class LocationHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var view: View
    var image: ImageView
    var name: TextView


    init {
        view = itemView
        image = itemView.findViewById(R.id.image)
        name = itemView.findViewById(R.id.title)
    }
}