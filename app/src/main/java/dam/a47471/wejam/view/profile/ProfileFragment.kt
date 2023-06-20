package dam.a47471.wejam.view.profile

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import dam.a47471.wejam.R
import dam.a47471.wejam.activities.InternalActivity
import dam.a47471.wejam.viewmodel.profile.ProfileViewModel
import dam.a47471.wejam.databinding.FragmentProfileBinding
import dam.a47471.wejam.view.profile.tabs.AboutFragment
import dam.a47471.wejam.view.profile.adapters.ProfilePagerAdapter
import dam.a47471.wejam.view.profile.dialogs.SettingsDialog

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var viewModel: ProfileViewModel
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var activity: InternalActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[ProfileViewModel::class.java]
        viewModel.loadUser(auth.currentUser!!.uid)
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity = requireActivity() as InternalActivity

        binding.settingsBtn.setOnClickListener {
            SettingsDialog().show(parentFragmentManager, "Settings")
        }

        binding.editBtn.setOnClickListener {
            activity.loadingDialog.show()
            findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment)
        }

        binding.banner.setImageDrawable(activity.resizeImage(R.drawable.img_banner))
        if (auth.currentUser!!.photoUrl != null)
            Glide.with(requireContext()).load(auth.currentUser!!.photoUrl)
                .into(binding.profileImage)

        binding.viewPager.adapter =
            ProfilePagerAdapter(requireActivity().supportFragmentManager, lifecycle)
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "About"
                else -> "Events"
            }
        }.attach()


        viewModel.user.observe(viewLifecycleOwner) { user ->
            binding.username.text = user.username
            binding.realName.text = user.realName

            if (user.banner != "")
                Glide.with(requireContext()).load(user.banner).into(binding.banner)

            val currentFragment =
                requireActivity().supportFragmentManager.findFragmentByTag("f${binding.viewPager.currentItem}")

            if (currentFragment is AboutFragment)
                currentFragment.binding.bio.text = user.bio
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        activity.loadingDialog.dismiss()
    }

}