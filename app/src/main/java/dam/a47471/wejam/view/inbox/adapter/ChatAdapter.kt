package dam.a47471.wejam.view.inbox.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dam.a47471.wejam.databinding.ChatReceiveBinding
import dam.a47471.wejam.databinding.ChatSendBinding
import dam.a47471.wejam.model.Message

class ChatAdapter(private var messages: List<Message>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_RECEIVED = 1
    private val VIEW_TYPE_SENT = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_RECEIVED -> {
                val binding = ChatReceiveBinding.inflate(inflater, parent, false)
                ReceivedMessageViewHolder(binding)
            }
            VIEW_TYPE_SENT -> {
                val binding = ChatSendBinding.inflate(inflater, parent, false)
                SentMessageViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        when (holder) {
            is ReceivedMessageViewHolder -> {
                holder.bind(message)
            }
            is SentMessageViewHolder -> {
                holder.bind(message)
            }
        }
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].sender == Firebase.auth.currentUser!!.uid) VIEW_TYPE_SENT else VIEW_TYPE_RECEIVED
    }

    inner class ReceivedMessageViewHolder(private val binding: ChatReceiveBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(message: Message) {
            binding.messageText.text = message.message
        }
    }

    inner class SentMessageViewHolder(private val binding: ChatSendBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(message: Message) {
            binding.messageText.text = message.message
        }
    }

    fun updateMessages(newMessages: List<Message>) {
        messages = newMessages
        notifyDataSetChanged()
    }
}
