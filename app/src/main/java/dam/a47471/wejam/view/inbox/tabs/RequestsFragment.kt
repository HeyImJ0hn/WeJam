package dam.a47471.wejam.view.inbox.tabs

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.get
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dam.a47471.wejam.R
import dam.a47471.wejam.databinding.FragmentInboxRequestsBinding
import dam.a47471.wejam.model.User
import dam.a47471.wejam.view.inbox.adapter.RequestListAdapter
import dam.a47471.wejam.view.profile.ProfileOtherFragment
import dam.a47471.wejam.viewmodel.inbox.InboxViewModel

class RequestsFragment : Fragment() {

    private lateinit var binding: FragmentInboxRequestsBinding
    private lateinit var viewModel: InboxViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[InboxViewModel::class.java]
        binding = FragmentInboxRequestsBinding.inflate(inflater, container, false)
        viewModel.loadFriendRequests(Firebase.auth.currentUser!!.uid)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        viewModel.friendRequests.observe(viewLifecycleOwner) { friendRequests ->
            if (friendRequests.isEmpty()) {
                binding.tvNoRequests.visibility = View.VISIBLE
                return@observe
            }

            val requests = mutableListOf<User>()

            friendRequests.forEach { friendId ->
                viewModel.loadUser(friendId)
                viewModel.user.observe(viewLifecycleOwner) { user ->
                    requests.add(user)

                    if (requests.size == friendRequests.size) {
                        binding.recyclerView.adapter =
                            RequestListAdapter(requests, itemClickedListener = {
                                val bundle = Bundle().apply {
                                    putParcelable("user", it as User)
                                }
                                val navOptions = NavOptions.Builder()
                                    .setPopUpTo(R.id.profileOtherFragment, false)
                                    .setEnterAnim(R.anim.slide_in_right)
                                    .setExitAnim(R.anim.slide_out_left).build()
                                ProfileOtherFragment().arguments = bundle
                                findNavController().navigate(
                                    R.id.profileOtherFragment,
                                    bundle,
                                    navOptions
                                )
                            }, requireContext())
                    }

                }
            }
        }


    }

}