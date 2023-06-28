package dam.a47471.wejam.utils

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
import dam.a47471.wejam.databinding.ItemListingUserBinding
import dam.a47471.wejam.model.Event
import dam.a47471.wejam.model.User
import dam_a47471.pokedex.ui.events.OnItemClickedListener

class GeneralUserRecyclerViewAdapter(
    private val userList: List<User>,
    private val itemClickedListener: OnItemClickedListener? = null,
    private val context: Context
) : RecyclerView.Adapter<GeneralUserRecyclerViewAdapter.ViewHolder>() {

    private var filteredUserList: List<User> = userList

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemListingUserBinding.bind(itemView)

        fun bindView(userListing: User, itemClickedListener: OnItemClickedListener?) {
            binding.user = userListing

            itemView.setOnClickListener {
                itemClickedListener?.invoke(userListing)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.item_listing_user, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = filteredUserList[position]
        holder.bindView(item, itemClickedListener)
    }

    override fun getItemCount(): Int {
        return filteredUserList.size
    }

    fun updateUserList(list: List<User>) {
        filteredUserList = list
        notifyDataSetChanged()
    }

    fun filterUsername(query: String) {
        filteredUserList = if (query.isEmpty()) {
            filteredUserList
        } else {
            filteredUserList.filter { user ->
                user.username!!.contains(query, ignoreCase = true)
            }
        }
        notifyDataSetChanged()
    }

}