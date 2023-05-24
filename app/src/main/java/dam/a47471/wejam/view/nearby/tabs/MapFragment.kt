package dam.a47471.wejam.view.nearby.tabs

import android.Manifest
import android.app.Dialog
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import dam.a47471.wejam.R
import dam.a47471.wejam.databinding.DialogCreateEventBinding
import dam.a47471.wejam.databinding.FragmentNearbyMapBinding
import kotlin.math.pow

class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var binding: FragmentNearbyMapBinding
    private lateinit var googleMap: GoogleMap

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNearbyMapBinding.inflate(inflater, container, false)

        val mapFragment = SupportMapFragment.newInstance()
        requireActivity().supportFragmentManager
            .beginTransaction()
            .add(R.id.map, mapFragment)
            .commit()
        mapFragment.getMapAsync(this)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addEvent.setOnClickListener {
            val createEventDialog = Dialog(requireContext())
            createEventDialog.setContentView(R.layout.dialog_create_event)
            createEventDialog.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            createEventDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            createEventDialog.setCancelable(true)
            createEventDialog.show()

            val dialogBinding: DialogCreateEventBinding = DialogCreateEventBinding.inflate(layoutInflater)
            createEventDialog.setContentView(dialogBinding.root)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        val defaultZoomLevel = 13.0f
        val cameraUpdate = CameraUpdateFactory.zoomTo(defaultZoomLevel)
        googleMap.moveCamera(cameraUpdate)
        googleMap.isMyLocationEnabled = true

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
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

}