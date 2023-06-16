package dam.a47471.wejam.view.profile

import android.os.Bundle
import android.provider.ContactsContract.Profile
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import dam.a47471.wejam.R
import dam.a47471.wejam.activities.InternalActivity
import dam.a47471.wejam.databinding.FragmentProfileOtherBinding
import dam.a47471.wejam.model.Event
import dam.a47471.wejam.model.User
import dam.a47471.wejam.utils.EventInfoDialog
import dam.a47471.wejam.view.profile.tabs.AboutFragment
import dam.a47471.wejam.viewmodel.profile.EditProfileViewModel
import dam.a47471.wejam.viewmodel.profile.ProfileViewModel


class ProfileOtherFragment : Fragment(), MenuProvider {

    private lateinit var viewModel: ProfileViewModel
    private lateinit var binding: FragmentProfileOtherBinding
    private lateinit var activity: InternalActivity
    private lateinit var user: User
    private var event: Event? = null

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

        if (arguments != null) {
            user = arguments!!.getParcelable("user")!!
            bindUser(user)

            if (arguments!!.getParcelable<Event>("event") != null)
                event = arguments!!.getParcelable("event")!!
        }

        binding.toolbar.title = ""
        activity.binding.bottomNav.visibility = View.GONE

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
        (activity as MenuHost).removeMenuProvider(this)
        super.onDestroyView()
        activity.loadingDialog.dismiss()
        activity.binding.bottomNav.visibility = View.VISIBLE
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
        }

        if (user.banner != "")
            Glide.with(requireContext()).load(user.banner).into(binding.banner)

        val currentFragment =
            requireActivity().supportFragmentManager.findFragmentByTag("f${binding.viewPager.currentItem}")

        if (currentFragment is AboutFragment)
            currentFragment.binding.bio.text = user.bio
    }
}