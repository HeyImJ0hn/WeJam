package dam.a47471.wejam.utils

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dam.a47471.wejam.R
import dam.a47471.wejam.databinding.DialogEventInfoBinding
import dam.a47471.wejam.model.Event
import dam.a47471.wejam.model.User
import dam.a47471.wejam.view.profile.ProfileOtherFragment
import dam.a47471.wejam.viewmodel.explore.NearbyViewModel

class EventInfoDialog : BottomSheetDialogFragment() {

    private lateinit var binding: DialogEventInfoBinding
    private lateinit var viewModel: NearbyViewModel
    private lateinit var event: Event
    private lateinit var organizer: User

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[NearbyViewModel::class.java]
        binding = DialogEventInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setCanceledOnTouchOutside(true)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (::event.isInitialized) {
            bindEvent(event)
        }

        binding.joinBtn.setOnClickListener {
            if (!event.attendees.contains(Firebase.auth.currentUser!!.uid)) {
                event.attendees.add(Firebase.auth.currentUser!!.uid)
                viewModel.addAttendee(event, Firebase.auth.currentUser!!.uid)
                dismiss()
            } else {
                Toast.makeText(
                    requireContext(),
                    "You are already attending this event",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.leaveBtn.setOnClickListener {
            if (event.attendees.contains(Firebase.auth.currentUser!!.uid)) {
                event.attendees.remove(Firebase.auth.currentUser!!.uid)
                viewModel.removeAttendee(event, Firebase.auth.currentUser!!.uid)
                dismiss()
            } else {
                Toast.makeText(
                    requireContext(),
                    "You are not attending this event",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.deleteBtn.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setMessage("Are you sure you want to delete this event?")
                .setPositiveButton("Yes") { _, _ ->
                    Toast.makeText(
                        requireContext(),
                        if (viewModel.deleteEvent(event)) "Deleted Event" else "Failed to delete event",
                        Toast.LENGTH_SHORT
                    ).show()
                    dismiss()
                }.setNegativeButton("No", null).show()
        }

        binding.organizerCard.setOnClickListener {
            val bundle = Bundle().apply {
                putParcelable("user", organizer)
                putString("back", "nearby")
                putParcelable("event", event)
            }
            val navOptions = NavOptions.Builder().setPopUpTo(R.id.profileOtherFragment, false)
                .setEnterAnim(R.anim.slide_in_right).setExitAnim(R.anim.slide_out_left).build()
            ProfileOtherFragment().arguments = bundle
            findNavController().navigate(
                R.id.action_nearbyFragment_to_profileOtherFragment,
                bundle,
                navOptions
            )
            dismiss()
        }
    }

    fun setEvent(event: Event) {
        this.event = event
        if (isAdded) {
            bindEvent(event)
        }
    }

    private fun bindEvent(event: Event) {
        binding.event = event
        viewModel.loadUser(event.owner)
        viewModel.user.observe(viewLifecycleOwner) {
            organizer = it
            binding.owner.text = it.username
            viewModel.getUserPicture(event.owner).addOnSuccessListener { it2 ->
                Glide.with(requireContext())
                    .load(it2)
                    .into(binding.ownerImage)
            }

            if (event.owner == Firebase.auth.currentUser!!.uid) {
                binding.deleteBtn.visibility = View.VISIBLE
            } else {
                if (Utils.isAttendee(event, Firebase.auth.currentUser!!.uid)) {
                    binding.leaveBtn.visibility = View.VISIBLE
                } else {
                    binding.joinBtn.visibility = View.VISIBLE
                }
            }

        }
        binding.attendeeCount.text = event.attendees.size.toString()
    }

}