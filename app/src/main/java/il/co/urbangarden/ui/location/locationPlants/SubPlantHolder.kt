package il.co.urbangarden.ui.location.locationPlants

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import il.co.urbangarden.R

class SubPlantHolder(itemView: View): RecyclerView.ViewHolder(itemView){
    var view: View = itemView
//    var name: TextView = itemView.findViewById(R.id.title)
    var image: ImageView = itemView.findViewById(R.id.image)
}