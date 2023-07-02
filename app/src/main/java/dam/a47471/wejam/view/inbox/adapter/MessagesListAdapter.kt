package dam.a47471.wejam.view.inbox.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dam.a47471.wejam.R
import dam.a47471.wejam.databinding.ItemListingUserBinding
import dam.a47471.wejam.databinding.ItemListingUserMessagesBinding
import dam.a47471.wejam.databinding.ItemListingUserRequestBinding
import dam.a47471.wejam.model.User
import dam_a47471.pokedex.ui.events.OnItemClickedListener

class MessagesListAdapter(
    private var userList: List<User>,
    private val itemClickedListener: OnItemClickedListener? = null,
    private val context: Context
) : RecyclerView.Adapter<MessagesListAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemListingUserMessagesBinding.bind(itemView)

        fun bindView(userListing: User, itemClickedListener: OnItemClickedListener?) {
            binding.user = userListing

            itemView.setOnClickListener {
                itemClickedListener?.invoke(userListing)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.item_listing_user_messages, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = userList[position]
        holder.bindView(item, itemClickedListener)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    fun updateChatList(newList: List<User>) {
        userList = newList
        notifyDataSetChanged()
    }


}