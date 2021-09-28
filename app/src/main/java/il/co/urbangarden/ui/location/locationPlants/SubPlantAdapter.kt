package il.co.urbangarden.ui.location.locationPlants

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import il.co.urbangarden.R
import il.co.urbangarden.data.FirebaseViewableObject
import il.co.urbangarden.data.plant.Plant
import il.co.urbangarden.data.plant.PlantInstance


class SubPlantAdapter : RecyclerView.Adapter<SubPlantHolder>() {

    private var plantsList: List<PlantInstance> = ArrayList()
    var onItemClick: ((PlantInstance) -> Unit)? = null
    var setImg: ((FirebaseViewableObject, ImageView) -> Unit)? = null

    fun setPlantList(dataList: List<PlantInstance>?) {
        if (dataList != null) {
            this.plantsList = dataList
            notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubPlantHolder {
        // Inflate the custom layout
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.one_sub_plant, parent, false)
        val holder = SubPlantHolder(view)

        holder.view.setOnClickListener {
            val callback = onItemClick ?: return@setOnClickListener
            val plant = plantsList[holder.bindingAdapterPosition]
            callback(plant)
        }
        return holder
    }

    override fun onBindViewHolder(holder: SubPlantHolder, position: Int) {
        // Get the data model based on position
        val item = plantsList[position]
        // Set item views based on your views and data model
        holder.name.text = item.name
    }

    override fun getItemCount(): Int {
        return plantsList.size
    }


}