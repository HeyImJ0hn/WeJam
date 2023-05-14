package dam.a47471.wejam.view.profile

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import dam.a47471.wejam.R
import dam.a47471.wejam.activities.InternalActivity
import dam.a47471.wejam.viewmodel.profile.ProfileViewModel
import dam.a47471.wejam.databinding.FragmentProfileBinding
import dam.a47471.wejam.view.profile.tabs.AboutFragment
import dam.a47471.wejam.view.profile.tabs.ProfilePagerAdapter

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var viewModel: ProfileViewModel
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[ProfileViewModel::class.java]
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.loadUser(auth.currentUser!!.uid)

        binding.settingsBtn.setOnClickListener {
            SettingsDialog().show(parentFragmentManager, "Settings")
        }

        binding.editBtn.setOnClickListener {
            (activity as InternalActivity).loadingDialog.show()
            findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment)
        }
        binding.banner.setImageDrawable((activity as InternalActivity).resizeImage(R.drawable.img_banner))
        if (auth.currentUser!!.photoUrl != null)
            Glide.with(requireContext()).load(auth.currentUser!!.photoUrl).into(binding.profileImage)

        binding.viewPager.adapter =
            ProfilePagerAdapter(requireActivity().supportFragmentManager, lifecycle)
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "About"
                1 -> "Media"
                else -> "Events"
            }
        }.attach()

        viewModel.user.observe(viewLifecycleOwner) { user ->
            binding.username.text = user.username
            binding.realName.text = user.realName

            val currentFragment =
                requireActivity().supportFragmentManager.findFragmentByTag("f${binding.viewPager.currentItem}")
            if (currentFragment is AboutFragment)
                currentFragment.binding.bio.text = user.bio
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as InternalActivity).loadingDialog.dismiss()
    }

}