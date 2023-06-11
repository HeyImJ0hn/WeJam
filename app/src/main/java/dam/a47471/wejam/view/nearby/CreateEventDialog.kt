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
import dam.a47471.wejam.BuildConfig
import dam.a47471.wejam.databinding.NearbyCreateEventDialogBinding
import dam.a47471.wejam.databinding.ProfileSettingsDialogBinding
import dam.a47471.wejam.model.EventType
import dam.a47471.wejam.viewmodel.nearby.NearbyViewModel
import dam.a47471.wejam.viewmodel.profile.SettingsDialogViewModel

class CreateEventDialog : BottomSheetDialogFragment() {

    private lateinit var binding: NearbyCreateEventDialogBinding
    private lateinit var viewModel: NearbyViewModel
    private var longitude: Double = 0.0
    private var latitude: Double = 0.0

    private val startAutocomplete =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                if (intent != null) {
                    val place = Autocomplete.getPlaceFromIntent(intent)
                    binding.locationEditText.setText(place.name)
                    longitude = place.latLng!!.longitude
                    latitude = place.latLng!!.latitude
                    Log.i(
                        TAG, "Place: ${place.name}, ${place.id}, ${place.latLng}"
                    )
                }
            } else if (result.resultCode == Activity.RESULT_CANCELED) {
                // The user canceled the operation.
                Log.i(TAG, "User canceled autocomplete")
            }
        }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[NearbyViewModel::class.java]
        binding = NearbyCreateEventDialogBinding.inflate(inflater, container, false)
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

        binding.eventNameEditText.setOnFocusChangeListener { _, b ->
            if (!b) {
                if (binding.eventNameEditText.text!!.isEmpty())
                    binding.eventNameEditText.error = "Event name cannot be empty"
            } else {
                binding.eventNameInput.isErrorEnabled = false
            }
        }

        binding.eventButtonGroup.addOnButtonCheckedListener { _, _, isChecked ->
            if (isChecked)
                binding.eventTypeErrorIcon.visibility = View.GONE
        }

        binding.locationEditText.setOnClickListener {
            // Set the fields to specify which types of place data to
            // return after the user has made a selection.
            val fields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)

            // Start the autocomplete intent.
            val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                .build(requireContext())
            startAutocomplete.launch(intent)

            binding.eventLocationInput.isErrorEnabled = false

            binding.locationEditText.isFocusableInTouchMode = true
            binding.locationEditText.requestFocus()
            binding.locationEditText.isFocusableInTouchMode = false
        }

        binding.dateEditText.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()
            datePicker.show(childFragmentManager, "datePicker")

            datePicker.addOnPositiveButtonClickListener {
                binding.dateInput.isErrorEnabled = false

                binding.dateEditText.setText(datePicker.headerText)
                binding.dateEditText.clearFocus()
            }

            binding.dateEditText.isFocusableInTouchMode = true
            binding.dateEditText.requestFocus()
            binding.dateEditText.isFocusableInTouchMode = false
        }

        binding.timeEditText.setOnClickListener {
            val timePicker = MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(12)
                .setMinute(0)
                .setTitleText("Select time")
                .build()
            timePicker.show(childFragmentManager, "timePicker")

            timePicker.addOnPositiveButtonClickListener {
                binding.timeInput.isErrorEnabled = false

                binding.timeEditText.setText("${timePicker.hour}:${timePicker.minute}")
                binding.timeEditText.clearFocus()
            }

            binding.timeEditText.isFocusableInTouchMode = true
            binding.timeEditText.requestFocus()
            binding.timeEditText.isFocusableInTouchMode = false
        }

        binding.createBtn.setOnClickListener {

            if (binding.eventNameEditText.text!!.isEmpty()) {
                binding.eventNameEditText.error = "Event name cannot be empty"
                return@setOnClickListener
            }

            if (!binding.jamBtn.isChecked && !binding.concertBtn.isChecked) {
                binding.eventTypeErrorIcon.visibility = View.VISIBLE
                return@setOnClickListener
            }

            if (binding.locationEditText.text!!.isEmpty()) {
                binding.locationEditText.error = "Location cannot be empty"
                return@setOnClickListener
            }

            if (binding.dateEditText.text!!.isEmpty()) {
                binding.dateEditText.error = "Date cannot be empty"
                return@setOnClickListener
            }

            if (binding.timeEditText.text!!.isEmpty()) {
                binding.timeEditText.error = "Time cannot be empty"
                return@setOnClickListener
            }

            viewModel.createEvent(
                Firebase.auth.currentUser!!.uid,
                binding.eventNameEditText.text.toString(),
                EventType.valueOf(if (binding.jamBtn.isChecked) "JAM" else "CONCERT"),
                binding.locationEditText.text.toString(),
                latitude,
                longitude,
                binding.timeEditText.text.toString(),
                binding.dateEditText.text.toString()
            )
            Toast.makeText(
                requireContext(),
                "Created event: ${binding.eventNameEditText.text.toString()}.",
                Toast.LENGTH_SHORT
            ).show()
            dismiss()
        }

    }

}