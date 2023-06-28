package dam.a47471.wejam.view.inbox.tabs

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dam.a47471.wejam.R
import dam.a47471.wejam.databinding.FragmentInboxFriendsBinding
import dam.a47471.wejam.databinding.FragmentInboxRequestsBinding
import dam.a47471.wejam.model.User
import dam.a47471.wejam.utils.GeneralUserRecyclerViewAdapter
import dam.a47471.wejam.view.inbox.adapter.RequestListAdapter
import dam.a47471.wejam.view.profile.ProfileOtherFragment
import dam.a47471.wejam.view.searchuser.adapters.UserListAdapter
import dam.a47471.wejam.viewmodel.inbox.InboxViewModel

class FriendsFragment : Fragment() {

    private lateinit var binding: FragmentInboxFriendsBinding
    private lateinit var viewModel: InboxViewModel
    private var adapter: GeneralUserRecyclerViewAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[InboxViewModel::class.java]
        viewModel.loadFriends()
        binding = FragmentInboxFriendsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = GeneralUserRecyclerViewAdapter(emptyList(), itemClickedListener = {
            val user = it as User
            val bundle = Bundle().apply {
                putParcelable("user", user)
            }
            val navOptions = NavOptions.Builder().setPopUpTo(R.id.profileOtherFragment, false)
                .setEnterAnim(R.anim.slide_in_right).setExitAnim(R.anim.slide_out_left).build()
            ProfileOtherFragment().arguments = bundle
            findNavController().navigate(
                R.id.action_inboxFragment_to_profileOtherFragment,
                bundle,
                navOptions
            )
        }, requireContext())
        binding.recyclerView.adapter = adapter

        binding.searchBar.clearFocus()

        viewModel.friends.observe(viewLifecycleOwner) { ids ->
            if (ids.isEmpty()) {
                binding.tvNoFriends.visibility = View.VISIBLE
                return@observe
            }

            val friends = mutableListOf<User>()
            ids.forEach { id ->
                viewModel.loadUser(id)
                viewModel.user.observe(viewLifecycleOwner) { user ->
                    friends.add(user)
                    adapter?.updateUserList(friends)
                }
            }
        }

        binding.searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                binding.searchBar.clearFocus()
                adapter?.filterUsername(query.orEmpty())
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter?.filterUsername(newText.orEmpty())
                return false
            }
        })

        binding.searchBar.setOnClickListener {
            binding.searchBar.isIconified = false
        }

    }
}