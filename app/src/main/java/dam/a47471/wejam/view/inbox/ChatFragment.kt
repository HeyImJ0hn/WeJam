package dam.a47471.wejam.view.inbox

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.activity.OnBackPressedCallback
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dam.a47471.wejam.R
import dam.a47471.wejam.activities.InternalActivity
import dam.a47471.wejam.databinding.FragmentInboxChatBinding
import dam.a47471.wejam.model.Chat
import dam.a47471.wejam.model.Event
import dam.a47471.wejam.model.Message
import dam.a47471.wejam.model.User
import dam.a47471.wejam.utils.Utils
import dam.a47471.wejam.view.inbox.adapter.ChatAdapter
import dam.a47471.wejam.viewmodel.inbox.InboxViewModel


class ChatFragment : Fragment(), MenuProvider {

    private lateinit var viewModel: InboxViewModel
    private lateinit var binding: FragmentInboxChatBinding
    private lateinit var adapter: ChatAdapter
    private lateinit var otherUser: User
    private lateinit var chat: Chat

    private var previous: String? = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[InboxViewModel::class.java]
        binding = FragmentInboxChatBinding.inflate(inflater, container, false)
        (activity as MenuHost).addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as InternalActivity).binding.bottomNav.visibility = View.GONE

        if (arguments == null) {
            Toast.makeText(requireContext(), "Error getting messages", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
        }

        otherUser = arguments!!.getParcelable("user")!!
        previous = arguments!!.getString("previous")


        binding.toolbar.title = ""
        binding.user = otherUser

        adapter = ChatAdapter(emptyList())
        binding.chatRecyclerView.adapter = adapter

        viewModel.getChat(Firebase.auth.currentUser!!.uid, otherUser.userId!!)
            .observe(viewLifecycleOwner) {
                if (it != null) {
                    chat = it
                    adapter.updateMessages(it.messages!!.sortedByDescending { message -> message.timestamp })
                    binding.chatRecyclerView.scrollToPosition(adapter.itemCount - 1)
                } else {
                    Toast.makeText(requireContext(), "Error getting messages", Toast.LENGTH_SHORT)
                        .show()
                }
            }

        binding.sendBtn.setOnClickListener {
            val message = binding.messageInput.text.toString()
            if (message.isNotEmpty()) {
                val newMessage = Message(
                    Firebase.auth.currentUser!!.uid,
                    message,
                    Timestamp.now().seconds
                )
                viewModel.sendMessage(Firebase.auth.currentUser!!.uid, otherUser.userId!!, newMessage)
                binding.messageInput.text.clear()
            }
        }

        binding.backBtn.setOnClickListener {
            backAction()
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    backAction()
                }
            })
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.edit_action_menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.action_back -> {
                backAction()
                true
            }
            else -> false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (previous != "profileOther") {
            (activity as MenuHost).removeMenuProvider(this)
            (activity as InternalActivity).binding.bottomNav.visibility = View.VISIBLE
        }
    }

    private fun backAction() {
        findNavController().navigateUp()
    }

    private fun updateRecyclerViewWithNewMessage(message: Message) {
        // Add the new message to the existing list
        val updatedMessages = chat.messages!!.toMutableList()
        updatedMessages.add(message)

        // Update the RecyclerView with the updated list
        adapter.updateMessages(updatedMessages)

        // Scroll to the bottom of the RecyclerView to show the new message
        binding.chatRecyclerView.scrollToPosition(updatedMessages.size - 1)
    }

}