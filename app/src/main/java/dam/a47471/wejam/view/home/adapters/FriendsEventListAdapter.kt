package dam.a47471.wejam.view.home.adapters

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

class FriendsEventListAdapter(
    private val eventList: List<Event>,
    private val itemClickedListener: OnItemClickedListener? = null,
    private val context: Context
) : RecyclerView.Adapter<FriendsEventListAdapter.ViewHolder>() {

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
        val item = eventList[position]
        holder.bindView(item, itemClickedListener)
    }

    override fun getItemCount(): Int {
        return eventList.size
    }

}