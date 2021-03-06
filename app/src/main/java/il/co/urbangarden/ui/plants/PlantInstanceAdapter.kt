package il.co.urbangarden.ui.plants


import android.annotation.SuppressLint
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import il.co.urbangarden.R
import il.co.urbangarden.data.FirebaseViewableObject
import il.co.urbangarden.data.plant.PlantInstance
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class PlantInstanceAdapter : RecyclerView.Adapter<PlantInstanceHolder>() {


    private var plantsList: MutableList<PlantInstance> = ArrayList()
    var onItemClick: ((PlantInstance) -> Unit)? = null
    var onDropClick: ((PlantInstance) -> Unit)? = null
    var setImg: ((FirebaseViewableObject, ImageView) -> Unit)? = null


    @SuppressLint("NotifyDataSetChanged")
    fun setPlantList(dataList: List<PlantInstance>?) {
        if (dataList != null) {
            plantsList.clear()
            plantsList.addAll(dataList)
            plantsList.sortBy {
                it.wateringDays - TimeUnit.DAYS.convert(
                    Date().time - it.lastWatered.time, TimeUnit.MILLISECONDS
                )
            }
            notifyDataSetChanged()
        }
    }

// Provide a direct reference to each of the views with data items


    // Usually involves inflating a layout from XML and returning the holder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlantInstanceHolder {

        // Inflate the custom layout
        var view = LayoutInflater.from(parent.context).inflate(R.layout.one_plant, parent, false)
        var holder = PlantInstanceHolder(view)

        holder.view.setOnClickListener {
            val callback = onItemClick ?: return@setOnClickListener
            val plant = plantsList[holder.bindingAdapterPosition]
            callback(plant)
        }

        holder.dropButton.setOnClickListener {
            val callback = onDropClick ?: return@setOnClickListener
            val plant = plantsList[holder.bindingAdapterPosition]
            callback(plant)
            plant.lastWatered = Date()
            holder.nextWatering.text = (plant.wateringDays - TimeUnit.DAYS.convert(
                Date().time - plant.lastWatered.time, TimeUnit.MILLISECONDS
            )).toString()
        }
        return holder
    }

    // Involves populating data into the item through holder
    override fun onBindViewHolder(instanceHolder: PlantInstanceHolder, position: Int) {

        // Get the data model based on position
        val item = plantsList[position]
        // Set item views based on your views and data model
        instanceHolder.name.text = item.name
        val dateFormat: DateFormat = SimpleDateFormat("dd-MM-yy  hh:mm")
        val days = (item.wateringDays - TimeUnit.DAYS.convert(
            Date().time - item.lastWatered.time, TimeUnit.MILLISECONDS
        )).toString()
        if (days == "0"){
            instanceHolder.nextWatering.text = "today"
        }
        else{
            val str = "in " + days + " days"
            instanceHolder.nextWatering.text = str
        }

        setImg?.let { it(item, instanceHolder.image) }
    }

    //  total count of items in the list
    override fun getItemCount() = plantsList.size

}

