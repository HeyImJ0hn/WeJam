package dam.a47471.wejam.view.explore.adapters

import android.content.Context
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dam.a47471.wejam.R
import dam.a47471.wejam.databinding.ItemListingEventBinding
import dam.a47471.wejam.model.Event
import dam.a47471.wejam.model.EventType
import dam.a47471.wejam.utils.Utils
import dam_a47471.pokedex.ui.events.OnItemClickedListener
import kotlin.math.*

class EventListAdapter(
    private val eventList: List<Event>,
    private val itemClickedListener: OnItemClickedListener? = null,
    private val context: Context
) : RecyclerView.Adapter<EventListAdapter.ViewHolder>() {

    private var filteredEventList: List<Event> = eventList
    private var originalEventList: List<Event> = eventList

    private var nameFilter: String = ""
    private var typeFilter: EventType? = null
    private var distanceFilterEnabled: Boolean = false
    private var lat: Double = 0.0
    private var long: Double = 0.0
    private var maxDistance: Double = 50.0

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemListingEventBinding.bind(itemView)

        fun bindView(eventListing: Event, itemClickedListener: OnItemClickedListener?) {
            binding.event = eventListing

            binding.dateTime.text = eventListing.date + " • " + eventListing.time
            val paint = binding.dateTime.paint
            val width = paint.measureText(binding.dateTime.text.toString())
            val textShader: Shader = LinearGradient(0f, 0f, width, binding.dateTime.textSize, intArrayOf(
                Color.parseColor("#FF1D71B8"),
                Color.parseColor("#FFE6007E")
            ), null, Shader.TileMode.REPEAT)

            binding.dateTime.paint.shader = textShader

            binding.eventType.text = eventListing.type.toString()

            itemView.setOnClickListener {
                itemClickedListener?.invoke(eventListing)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.item_listing_event, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = filteredEventList[position]
        holder.bindView(item, itemClickedListener)
    }

    override fun getItemCount(): Int {
        return filteredEventList.size
    }

    fun filterName(query: String) {
        nameFilter = query
        applyFilters()
    }

    fun filterType(eventType: EventType) {
        typeFilter = eventType
        applyFilters()
    }

    fun filterDistance(lat: Double, long: Double) {
        this.lat = lat
        this.long = long
        distanceFilterEnabled = true
        applyFilters()
    }

    private fun applyFilters() {
        filteredEventList = originalEventList.filter { event ->
            if (Utils.isCurrentDateAfter(event.date))
                return@filter false

            var include = true

            // Apply name filter
            if (nameFilter.isNotEmpty()) {
                include = event.name.contains(nameFilter, ignoreCase = true)
            }

            // Apply type filter
            if (include && typeFilter != null) {
                include = event.type == typeFilter
            }

            // Apply distance filter
            if (include && distanceFilterEnabled) {
                include = eventInRange(event, lat, long) <= maxDistance
            }

            include
        }

        notifyDataSetChanged()
    }

    fun updateList(newList: List<Event>) {
        filteredEventList = newList
        applyFilters()
    }

    private fun eventInRange(event: Event, lat: Double, long: Double): Double {
        val earthRadius = 6371 // Radius of the Earth in kilometers

        // Convert latitude and longitude values from degrees to radians
        val lat1Rad = Math.toRadians(event.lat)
        val lon1Rad = Math.toRadians(event.long)
        val lat2Rad = Math.toRadians(lat)
        val lon2Rad = Math.toRadians(long)

        // Calculate the differences between the two latitude and longitude values
        val latDiff = lat2Rad - lat1Rad
        val lonDiff = lon2Rad - lon1Rad

        // Calculate the Haversine formula
        val a = sin(latDiff / 2).pow(2) + cos(lat1Rad) * cos(lat2Rad) * sin(lonDiff / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return earthRadius * c
    }

}