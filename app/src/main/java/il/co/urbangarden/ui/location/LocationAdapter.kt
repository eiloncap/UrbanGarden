package il.co.urbangarden.ui.location

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import il.co.urbangarden.R

class LocationAdapter() : RecyclerView.Adapter<LocationAdapter.ViewHolder>() {
    var dataList = listOf(LocationDataClass("Title",R.drawable.location),
        LocationDataClass("Title",R.drawable.location),
        LocationDataClass("Title",R.drawable.location),
        LocationDataClass("Title",R.drawable.location),
        LocationDataClass("Title",R.drawable.location),
        LocationDataClass("Title",R.drawable.location),
        LocationDataClass("Title",R.drawable.location),
        LocationDataClass("Title",R.drawable.location),
        LocationDataClass("Title",R.drawable.location))

    internal fun setDataList(dataList: List<LocationDataClass>) {
        this.dataList = dataList
    }

    // Provide a direct reference to each of the views with data items

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var image: ImageView
        var title: TextView

        init {
            image = itemView.findViewById(R.id.image)
            title = itemView.findViewById(R.id.title)
        }

    }

    // Usually involves inflating a layout from XML and returning the holder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationAdapter.ViewHolder {

        // Inflate the custom layout
        var view = LayoutInflater.from(parent.context).inflate(R.layout.one_location, parent, false)
        return ViewHolder(view)
    }

    // Involves populating data into the item through holder
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        // Get the data model based on position
        var data = dataList[position]

        // Set item views based on your views and data model
        holder.title.text = data.title
        holder.image.setImageResource(data.image)
    }

    //  total count of items in the list
    override fun getItemCount() = dataList.size
}

