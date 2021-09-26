package il.co.urbangarden.ui.plants


import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import il.co.urbangarden.R
import il.co.urbangarden.data.FirebaseViewableObject
import il.co.urbangarden.data.plant.PlantInstance
import java.util.*
import kotlin.collections.ArrayList

class PlantInstanceAdapter : RecyclerView.Adapter<PlantInstanceHolder>() {


    private var plantsList: List<PlantInstance> = ArrayList()
    var onItemClick: ((PlantInstance) -> Unit)? = null
    var setImg: ((FirebaseViewableObject, ImageView) -> Unit)? = null


    @SuppressLint("NotifyDataSetChanged")
    fun setPlantList(dataList: List<PlantInstance>?) {
        if (dataList != null) {
            this.plantsList = dataList
            notifyDataSetChanged()
        }
    }

    // Provide a direct reference to each of the views with data items


    // Usually involves inflating a layout from XML and returning the holder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlantInstanceHolder {

        // Inflate the custom layout
        var view = LayoutInflater.from(parent.context).inflate(R.layout.one_plant, parent, false)
        var holder = PlantInstanceHolder(view)

        holder.view.setOnClickListener{
            val callback = onItemClick ?: return@setOnClickListener
            val plant = plantsList[holder.bindingAdapterPosition]
            callback(plant)
        }
        return holder
    }

    // Involves populating data into the item through holder
    override fun onBindViewHolder(instanceHolder: PlantInstanceHolder, position: Int) {

        // Get the data model based on position
        val item = plantsList[position]
        // Set item views based on your views and data model
        if (item != null) {
            instanceHolder.name.text = item.name
            Log.d("setImgIsNull", setImg.toString())
            setImg?.let { it(item , instanceHolder.image) }
        }
    }

    //  total count of items in the list
    override fun getItemCount() = plantsList.size

}

