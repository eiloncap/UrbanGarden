package il.co.urbangarden.ui.location

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import il.co.urbangarden.R
import il.co.urbangarden.data.FirebaseViewableObject
import il.co.urbangarden.data.location.Location
import il.co.urbangarden.ui.MainViewModel
import java.util.*

class LocationAdapter : RecyclerView.Adapter<LocationHolder>() {


    private var locationList: List<Location> = Vector()
    var onItemClick: ((Location) -> Unit)? = null
    var setImg: ((FirebaseViewableObject, ImageView) -> Unit)? = null


    fun setLocationList(dataList: List<Location>?) {
        if (dataList != null) {
            this.locationList = dataList
        }
    }

    // Provide a direct reference to each of the views with data items


    // Usually involves inflating a layout from XML and returning the holder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationHolder {

        // Inflate the custom layout
        var view = LayoutInflater.from(parent.context).inflate(R.layout.one_location, parent, false)
        var holder = LocationHolder(view)
        
        holder.view.setOnClickListener{
            val callback = onItemClick ?: return@setOnClickListener
            val location = locationList[holder.bindingAdapterPosition]
            callback(location)
        }
        return holder
    }

    // Involves populating data into the item through holder
    override fun onBindViewHolder(holder: LocationHolder, position: Int) {

        // Get the data model based on position
        val item = locationList[position]

        // Set item views based on your views and data model
        if (item != null) {
            holder.name.text = item.name
            if (setImg != null){
                setImg?.let { it(item, holder.image) }
            }
        }
    }

    //  total count of items in the list
    override fun getItemCount() = locationList.size
}

