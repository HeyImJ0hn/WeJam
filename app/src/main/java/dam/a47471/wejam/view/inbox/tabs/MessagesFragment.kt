package dam.a47471.wejam.view.inbox.tabs

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dam.a47471.wejam.R
import dam.a47471.wejam.databinding.FragmentInboxMessagesBinding
import dam.a47471.wejam.model.User
import dam.a47471.wejam.utils.GeneralUserRecyclerViewAdapter
import dam.a47471.wejam.view.inbox.ChatFragment
import dam.a47471.wejam.view.inbox.adapter.MessagesListAdapter
import dam.a47471.wejam.viewmodel.inbox.InboxViewModel

class MessagesFragment : Fragment() {

    private lateinit var binding: FragmentInboxMessagesBinding
    private lateinit var viewModel: InboxViewModel

    private lateinit var adapter: MessagesListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[InboxViewModel::class.java]
        binding = FragmentInboxMessagesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        adapter = MessagesListAdapter(emptyList(), itemClickedListener = {
            val bundle = Bundle().apply {
                putParcelable("user", it as User)
            }
            val navOptions = NavOptions.Builder().setPopUpTo(R.id.chatFragment, false)
                .setEnterAnim(R.anim.slide_in_right).setExitAnim(R.anim.slide_out_left).build()
            ChatFragment().arguments = bundle
            findNavController().navigate(
                R.id.chatFragment,
                bundle,
                navOptions
            )
        }, requireContext())

        viewModel.getUserChats(Firebase.auth.currentUser!!.uid)
            .observe(viewLifecycleOwner) { chats ->
                val users = mutableListOf<User>()
                if (chats.isEmpty()) {
                    binding.tvNoChats.visibility = View.VISIBLE
                    return@observe
                }

                chats.forEach { chat ->
                    viewModel.loadUser(chat.members!!.filter { it != Firebase.auth.currentUser!!.uid }[0])
                    viewModel.user.observe(viewLifecycleOwner) { user ->
                        users.add(user)
                        adapter.updateChatList(users.distinct())
                        binding.recyclerView.adapter = adapter
                    }
                }
            }

    }

}