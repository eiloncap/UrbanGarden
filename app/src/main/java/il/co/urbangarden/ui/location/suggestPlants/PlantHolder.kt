package il.co.urbangarden.ui.location.suggestPlants

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import il.co.urbangarden.R

class PlantHolder(itemView: View): RecyclerView.ViewHolder(itemView){
    var view: View = itemView
    var image: ImageView = itemView.findViewById(R.id.image)
    var name: TextView = itemView.findViewById(R.id.title)
}