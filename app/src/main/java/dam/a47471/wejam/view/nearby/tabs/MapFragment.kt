package dam.a47471.wejam.view.nearby.tabs

import android.Manifest
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import dam.a47471.wejam.R
import dam.a47471.wejam.databinding.DialogCreateEventBinding
import dam.a47471.wejam.databinding.FragmentNearbyMapBinding
import dam.a47471.wejam.model.EventType
import dam.a47471.wejam.view.nearby.CreateEventDialog
import dam.a47471.wejam.view.profile.SettingsDialog
import dam.a47471.wejam.viewmodel.nearby.NearbyViewModel
import java.util.*
import kotlin.math.pow

class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var binding: FragmentNearbyMapBinding
    private lateinit var googleMap: GoogleMap
    private lateinit var createEventDialog: Dialog
    private lateinit var viewModel: NearbyViewModel

    private lateinit var dialogBinding: DialogCreateEventBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNearbyMapBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[NearbyViewModel::class.java]

        val mapFragment = SupportMapFragment.newInstance()
        requireActivity().supportFragmentManager
            .beginTransaction()
            .add(R.id.map, mapFragment)
            .commit()
        mapFragment.getMapAsync(this)

        createEventDialog = Dialog(requireContext())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addEvent.setOnClickListener {
            CreateEventDialog().show(parentFragmentManager, "CreateEvent")
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        val defaultZoomLevel = 13.0f
        val cameraUpdate = CameraUpdateFactory.zoomTo(defaultZoomLevel)

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 2)
            return
        }

        googleMap.moveCamera(cameraUpdate)
        googleMap.isMyLocationEnabled = true

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val userPos = LatLng(location.latitude, location.longitude)
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(userPos))

                binding.btnMyLocation.setOnClickListener {
                    googleMap.animateCamera(CameraUpdateFactory.newLatLng(userPos))
                }
            }
        }
    }

    private fun showTimePickerDialog() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            requireContext(),
            { view: TimePicker?, hourOfDay: Int, minute: Int ->
                // Handle the selected time
                // hourOfDay: selected hour
                // minute: selected minute
                // You can perform any desired action with the selected time
                dialogBinding.timeInput.setText("${hourOfDay}:${minute}")
            },
            hour,
            minute,
            true
        )

        timePickerDialog.show()
    }

}