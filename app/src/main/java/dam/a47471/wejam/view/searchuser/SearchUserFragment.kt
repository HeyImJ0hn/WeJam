package dam.a47471.wejam.view.searchuser

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import dam.a47471.wejam.R
import dam.a47471.wejam.activities.InternalActivity
import dam.a47471.wejam.viewmodel.searchuser.SearchUserViewModel
import dam.a47471.wejam.databinding.FragmentSearchUserBinding
import dam.a47471.wejam.model.User
import dam.a47471.wejam.view.profile.ProfileOtherFragment
import dam.a47471.wejam.view.searchuser.adapters.UserListAdapter

class SearchUserFragment : Fragment() {

    private lateinit var viewModel: SearchUserViewModel
    private lateinit var binding: FragmentSearchUserBinding
    private var adapter: UserListAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this)[SearchUserViewModel::class.java]
        binding = FragmentSearchUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = UserListAdapter(emptyList(), itemClickedListener = {
            val user = it as User
            val bundle = Bundle().apply {
                putParcelable("user", user)
            }
            val navOptions = NavOptions.Builder().setPopUpTo(R.id.profileOtherFragment, false)
                .setEnterAnim(R.anim.slide_in_right).setExitAnim(R.anim.slide_out_left).build()
            ProfileOtherFragment().arguments = bundle
            findNavController().navigate(
                R.id.action_searchUsersFragment_to_profileOtherFragment,
                bundle,
                navOptions
            )
        }, requireContext())
        binding.recyclerView.adapter = adapter

        viewModel.searchResults.observe(viewLifecycleOwner) {users ->
            adapter?.updateUserList(users ?: emptyList())
        }

        binding.searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                binding.searchBar.clearFocus()
                viewModel.searchUsers(query.orEmpty())
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.searchUsers(newText.orEmpty())
                return false
            }
        })

        binding.searchBar.setOnClickListener {
            binding.searchBar.isIconified = false
        }

    }

}