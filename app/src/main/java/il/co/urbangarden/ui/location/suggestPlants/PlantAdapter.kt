package il.co.urbangarden.ui.location.suggestPlants

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import il.co.urbangarden.R
import il.co.urbangarden.data.FirebaseViewableObject
import il.co.urbangarden.data.location.Location
import il.co.urbangarden.data.plant.Plant
import il.co.urbangarden.data.plant.PlantInstance
import il.co.urbangarden.ui.location.LocationHolder
import il.co.urbangarden.ui.plants.PlantInstanceHolder


class PlantAdapter: RecyclerView.Adapter<PlantHolder>() {

    private var plantsList: List<Plant> = ArrayList()
    var onItemClick: ((Plant) -> Unit)? = null
    var setImg: ((FirebaseViewableObject, ImageView) -> Unit)? = null

    @SuppressLint("NotifyDataSetChanged")
    fun setPlantList(dataList: List<Plant>?) {
        if (dataList != null) {
            this.plantsList = dataList
            notifyDataSetChanged()
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlantHolder {
        // Inflate the custom layout
        var view = LayoutInflater.from(parent.context).inflate(R.layout.one_seggest_plant, parent, false)
        var holder = PlantHolder(view)

        holder.view.setOnClickListener{
            val callback = onItemClick ?: return@setOnClickListener
            val plant = plantsList[holder.bindingAdapterPosition]
            callback(plant)
        }
        return holder
    }

    override fun onBindViewHolder(holder: PlantHolder, position: Int) {
        // Get the data model based on position
        val item = plantsList[position]
        // Set item views based on your views and data model
        if (item != null) {
            holder.name.text = item.name
            setImg?.let { it(item , holder.image) }
        }

    }

    override fun getItemCount(): Int {
       return plantsList.size
    }


}