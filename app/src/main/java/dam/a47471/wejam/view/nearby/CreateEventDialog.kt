package dam.a47471.wejam.view.nearby

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dam.a47471.wejam.databinding.NearbyCreateEventDialogBinding
import dam.a47471.wejam.databinding.ProfileSettingsDialogBinding
import dam.a47471.wejam.viewmodel.nearby.NearbyViewModel
import dam.a47471.wejam.viewmodel.profile.SettingsDialogViewModel

class CreateEventDialog : BottomSheetDialogFragment() {

    private lateinit var binding: NearbyCreateEventDialogBinding
    private lateinit var viewModel: NearbyViewModel

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

        binding.logoutBtn.setOnClickListener {
            dismiss()
        }

    }

}