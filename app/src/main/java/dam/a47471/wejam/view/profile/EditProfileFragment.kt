package dam.a47471.wejam.view.profile

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.view.ViewGroup.LayoutParams
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import dam.a47471.wejam.R
import dam.a47471.wejam.activities.InternalActivity
import dam.a47471.wejam.databinding.FragmentEditProfileBinding
import dam.a47471.wejam.viewmodel.profile.EditProfileViewModel
import java.io.InputStream


class EditProfileFragment : Fragment(), MenuProvider {

    private lateinit var viewModel: EditProfileViewModel
    private lateinit var binding: FragmentEditProfileBinding
    private lateinit var pickPhotoLauncher: ActivityResultLauncher<String>
    private lateinit var activity: InternalActivity

    private val STORAGE_PERMISSION_CODE = 1

    private var isProfilePicture = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity = (requireActivity() as InternalActivity)

        pickPhotoLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            activity.loadingDialog.show()
            if (uri != null) {
                if (isProfilePicture) {
                    viewModel.uploadProfilePicture(Firebase.auth.currentUser!!.uid, uri)
                    viewModel.getPfpResult().observe(viewLifecycleOwner, Observer { data ->
                        if (data)
                            viewModel.getProfilePictureFromStorage(Firebase.auth.currentUser!!.uid)
                                .addOnCompleteListener {
                                    if (it.isSuccessful) {
                                        viewModel.updatePicture(it.result)
                                        Glide.with(requireContext()).load(it.result).into(binding.profileImage)
                                    } else
                                        Toast.makeText(requireContext(), "Failed to get image", Toast.LENGTH_SHORT).show()
                                    activity.loadingDialog.dismiss()
                                }
                    })
                } else {
                    viewModel.uploadBannerPicture(Firebase.auth.currentUser!!.uid, uri)
                    viewModel.getBannerResult().observe(viewLifecycleOwner, Observer { data ->
                        if (data)
                            viewModel.getBannerPictureFromStorage(Firebase.auth.currentUser!!.uid)
                                .addOnCompleteListener {
                                    if (it.isSuccessful) {
                                        viewModel.updateBanner(Firebase.auth.currentUser!!.uid, it.result)
                                        Glide.with(requireContext()).load(it.result).into(binding.banner)
                                    } else
                                        Toast.makeText(requireContext(), "Failed to get image", Toast.LENGTH_SHORT).show()
                                    activity.loadingDialog.dismiss()
                                }
                    })
                }
            }
            activity.loadingDialog.dismiss()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[EditProfileViewModel::class.java]
        binding = FragmentEditProfileBinding.inflate(inflater, container, false)

        (activity as MenuHost).addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity.setSupportActionBar(binding.toolbar)

        val auth = FirebaseAuth.getInstance()

        viewModel.loadUser(auth.currentUser!!.uid)

        viewModel.user.observe(viewLifecycleOwner) { user ->
            binding.usernameInput.setText(user.username)
            binding.realNameInput.setText(user.realName)
            binding.bioInput.setText(user.bio)
            if (user.banner != "")
                Glide.with(requireContext()).load(user.banner).into(binding.banner)
        }
        binding.emailInput.setText(auth.currentUser?.email)
        if (auth.currentUser!!.photoUrl != null)
            Glide.with(requireContext()).load(auth.currentUser!!.photoUrl).into(binding.profileImage)

        binding.updateBtn.setOnClickListener {
            viewModel.updateProfile(
                binding.usernameInput.text.toString(),
                binding.realNameInput.text.toString(),
                binding.emailInput.text.toString(),
                binding.bioInput.text.toString()
            )
            activity.loadingDialog.show()
            findNavController().navigate(R.id.action_editProfileFragment_to_profileFragment)
            Toast.makeText(requireContext(), "Updated Profile", Toast.LENGTH_SHORT).show()
        }

        binding.changePicture.setOnClickListener {
            if (requireContext().checkSelfPermission(READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(READ_EXTERNAL_STORAGE),
                    STORAGE_PERMISSION_CODE
                )
            } else {
                isProfilePicture = true
                pickPhotoLauncher.launch("image/*")
                activity.loadingDialog.show()
            }
        }

        binding.changeBanner.setOnClickListener {
            if (requireContext().checkSelfPermission(READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(READ_EXTERNAL_STORAGE),
                    STORAGE_PERMISSION_CODE
                )
            } else {
                isProfilePicture = false
                pickPhotoLauncher.launch("image/*")
                activity.loadingDialog.show()
            }
        }

        binding.toolbar.title = ""

        activity.binding.bottomNav.visibility = View.GONE
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.edit_action_menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.action_back -> {
                findNavController().navigate(R.id.action_editProfileFragment_to_profileFragment)
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

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            STORAGE_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(requireContext(), "Permission Granted", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Permission Denied", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }

}