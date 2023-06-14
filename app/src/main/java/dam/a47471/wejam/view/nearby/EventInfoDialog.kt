package dam.a47471.wejam.view.nearby

import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.ContentValues.TAG
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.api.Context
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import dam.a47471.wejam.BuildConfig
import dam.a47471.wejam.databinding.EventListingBottomDialogBinding
import dam.a47471.wejam.databinding.NearbyCreateEventDialogBinding
import dam.a47471.wejam.databinding.ProfileSettingsDialogBinding
import dam.a47471.wejam.model.Event
import dam.a47471.wejam.model.EventType
import dam.a47471.wejam.viewmodel.nearby.NearbyViewModel
import dam.a47471.wejam.viewmodel.profile.SettingsDialogViewModel

class EventInfoDialog : BottomSheetDialogFragment() {

    private lateinit var binding: EventListingBottomDialogBinding
    private lateinit var viewModel: NearbyViewModel
    private lateinit var event: Event

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[NearbyViewModel::class.java]
        binding = EventListingBottomDialogBinding.inflate(inflater, container, false)
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

        binding.joinBtn.setOnClickListener {
            dismiss()
        }

        if (::event.isInitialized) {
            bindEvent(event)
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
        viewModel.getUserFromId(event.owner).observe(viewLifecycleOwner) {
            binding.owner.text = it.username
            val storageRef = FirebaseStorage.getInstance().reference.child("profile/" + event.owner + "/picture")
            viewModel.getUserPicture(event.owner).addOnSuccessListener {it2 ->
                Glide.with(requireContext())
                    .load(it2)
                    .into(binding.ownerImage)
            }
        }
        binding.attendeeCount.text = event.attendees.size.toString()
    }

}