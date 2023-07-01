package dam.a47471.wejam.view.profile

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dam.a47471.wejam.R
import dam.a47471.wejam.activities.InternalActivity
import dam.a47471.wejam.databinding.FragmentProfileOtherBinding
import dam.a47471.wejam.model.Event
import dam.a47471.wejam.model.User
import dam.a47471.wejam.utils.EventInfoDialog
import dam.a47471.wejam.view.inbox.ChatFragment
import dam.a47471.wejam.view.profile.adapters.ProfileOtherPagerAdapter
import dam.a47471.wejam.view.profile.tabs.AboutFragment
import dam.a47471.wejam.view.profile.tabs.EventsFragment
import dam.a47471.wejam.viewmodel.inbox.InboxViewModel
import dam.a47471.wejam.viewmodel.profile.ProfileViewModel


class ProfileOtherFragment : Fragment(), MenuProvider {

    private lateinit var viewModel: ProfileViewModel
    private lateinit var binding: FragmentProfileOtherBinding
    private lateinit var activity: InternalActivity
    private lateinit var user: User
    private lateinit var friends: List<String>
    private var event: Event? = null
    private var adapter: ProfileOtherPagerAdapter? = null

    private var toChat = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity = (requireActivity() as InternalActivity)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[ProfileViewModel::class.java]
        binding = FragmentProfileOtherBinding.inflate(inflater, container, false)

        (activity as MenuHost).addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity.setSupportActionBar(binding.toolbar)

        adapter = ProfileOtherPagerAdapter(requireActivity().supportFragmentManager, lifecycle)
        binding.viewPager.adapter = adapter!!

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "About"
                else -> "Events"
            }
        }.attach()

        binding.toolbar.title = ""
        activity.binding.bottomNav.visibility = View.GONE

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    backAction()
                }
            })

        if (arguments != null) {
            user = requireArguments().getParcelable("user")!!
            bindUser(user)

            if (requireArguments().getParcelable<Event>("event") != null)
                event = requireArguments().getParcelable("event")!!
        }

        viewModel.getFriends().observe(viewLifecycleOwner) { list ->
            if (list.contains(user.userId!!)) {
                binding.friendBtn.visibility = View.GONE
                binding.removeBtn.visibility = View.VISIBLE
            }
        }

        viewModel.getFriendRequests(Firebase.auth.currentUser!!.uid).observe(viewLifecycleOwner) { list ->
            if (list.contains(user.userId!!)) {
                binding.friendBtn.visibility = View.GONE
                binding.messageBtn.visibility = View.GONE
                binding.acceptFriendBtn.visibility = View.VISIBLE
                binding.denyBtn.visibility = View.VISIBLE
            }
        }

        viewModel.getFriendRequests(user.userId!!).observe(viewLifecycleOwner) { list ->
            if (list.contains(Firebase.auth.currentUser!!.uid)) {
                binding.friendBtn.visibility = View.GONE
                binding.requestedBtn.visibility = View.VISIBLE
            }
        }

        binding.requestedBtn.setOnClickListener {
            viewModel.removeFriendRequest(user.userId!!, Firebase.auth.currentUser!!.uid)
            Toast.makeText(
                requireContext(),
                "Cancelled request",
                Toast.LENGTH_SHORT
            ).show()
            binding.requestedBtn.visibility = View.GONE
            binding.friendBtn.visibility = View.VISIBLE
        }

        binding.friendBtn.setOnClickListener {
            if (user.userId!! == Firebase.auth.currentUser!!.uid) {
                Toast.makeText(
                    requireContext(),
                    "You can't add yourself as a friend",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            viewModel.sendFriendRequest(user.userId!!)
            Toast.makeText(
                requireContext(),
                "Friend request sent to ${user.username}",
                Toast.LENGTH_SHORT
            ).show()
            binding.friendBtn.visibility = View.GONE
            binding.requestedBtn.visibility = View.VISIBLE
        }

        binding.acceptFriendBtn.setOnClickListener {
            viewModel.acceptFriendRequest(user.userId!!)
            Toast.makeText(
                requireContext(),
                "Accepted friend request",
                Toast.LENGTH_SHORT
            ).show()
            binding.acceptFriendBtn.visibility = View.GONE
            binding.removeBtn.visibility = View.VISIBLE
            binding.denyBtn.visibility = View.GONE
            binding.messageBtn.visibility = View.VISIBLE
        }

        binding.removeBtn.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Remove friend")
                .setMessage("Are you sure you want to remove ${user.username} from your friends?")
                .setPositiveButton("Yes") { _, _ ->
                    viewModel.removeFriend(user.userId!!)
                    binding.removeBtn.visibility = View.GONE
                    binding.friendBtn.visibility = View.VISIBLE
                    Toast.makeText(
                        requireContext(),
                        "Removed friend",
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.removeBtn.visibility = View.GONE
                    binding.friendBtn.visibility = View.VISIBLE
                }
                .setNegativeButton("No") { _, _ -> }
                .show()
        }

        binding.denyBtn.setOnClickListener {
            viewModel.removeFriendRequest(Firebase.auth.currentUser!!.uid, user.userId!!)
            Toast.makeText(
                requireContext(),
                "Denied friend request",
                Toast.LENGTH_SHORT
            ).show()
            binding.denyBtn.visibility = View.GONE
            binding.messageBtn.visibility = View.VISIBLE
            binding.acceptFriendBtn.visibility = View.GONE
            binding.friendBtn.visibility = View.VISIBLE
        }

        binding.messageBtn.setOnClickListener {
            if (user.userId!! == Firebase.auth.currentUser!!.uid) {
                Toast.makeText(
                    requireContext(),
                    "You can't message yourself",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val bundle = Bundle().apply {
                putParcelable("user", user)
                putString("previous", "profileOther")
            }
            toChat = true
            val navOptions = NavOptions.Builder().setPopUpTo(R.id.chatFragment, false)
                .setEnterAnim(R.anim.slide_in_right).setExitAnim(R.anim.slide_out_left).build()
            ChatFragment().arguments = bundle
            findNavController().navigate(
                R.id.chatFragment,
                bundle,
                navOptions
            )
        }

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
        activity.loadingDialog.dismiss()
        if (!toChat) {
            (activity as MenuHost).removeMenuProvider(this)
            activity.binding.bottomNav.visibility = View.VISIBLE
        }
        toChat = false
    }

    private fun backAction() {
        findNavController().navigateUp()
        if (event != null) {
            val dialog = EventInfoDialog()
            dialog.setEvent(event!!)
            dialog.show(requireActivity().supportFragmentManager, "event_info_dialog")
        }
    }

    private fun bindUser(user: User) {
        binding.user = user

        viewModel.getUserPicture(user.userId!!).addOnSuccessListener {
            Glide.with(requireContext())
                .load(it)
                .into(binding.profileImage)
        }.addOnFailureListener {
            println("Error loading user picture, checking for google image.")
            viewModel.getGoogleUserPicture(user.userId).addOnSuccessListener {
                println("Got google image: ${it.value}")
                Glide.with(requireContext())
                    .load(it.value)
                    .into(binding.profileImage)
            }.addOnFailureListener {
                println("Error loading google user picture, loading default avatar.")
                viewModel.getDefaultAvatar().addOnSuccessListener {
                    Glide.with(requireContext())
                        .load(it)
                        .into(binding.profileImage)
                }
            }
        }

        if (user.banner != "")
            Glide.with(requireContext()).load(user.banner).into(binding.banner)

        val aFragment = AboutFragment()
        aFragment.arguments = bundleOf().apply {
            putString("bio", user.bio)
        }
        adapter!!.aboutFragment = aFragment

        val eFragment = EventsFragment()
        eFragment.arguments = bundleOf().apply {
            putString("userId", user.userId)
        }
        adapter!!.eventsFragment = eFragment
    }
}