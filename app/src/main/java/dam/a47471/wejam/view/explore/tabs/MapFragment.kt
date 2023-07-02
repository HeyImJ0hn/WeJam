package dam.a47471.wejam.view.explore.tabs

import android.Manifest
import android.app.Dialog
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dam.a47471.wejam.R
import dam.a47471.wejam.activities.InternalActivity
import dam.a47471.wejam.databinding.DialogCreateEventBinding
import dam.a47471.wejam.databinding.FragmentNearbyMapBinding
import dam.a47471.wejam.view.explore.dialogs.CreateEventDialog
import dam.a47471.wejam.utils.EventInfoDialog
import dam.a47471.wejam.utils.Utils
import dam.a47471.wejam.viewmodel.explore.NearbyViewModel

class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var _binding: FragmentNearbyMapBinding
    val binding: FragmentNearbyMapBinding
        get() = _binding

    private lateinit var _googleMap: GoogleMap
    val googleMap: GoogleMap
        get() = _googleMap

    private lateinit var createEventDialog: Dialog
    private lateinit var viewModel: NearbyViewModel

    private lateinit var dialogBinding: DialogCreateEventBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNearbyMapBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[NearbyViewModel::class.java]

        (requireActivity() as InternalActivity).loadingDialog.show()

        val mapFragment = SupportMapFragment.newInstance()
        requireActivity().supportFragmentManager
            .beginTransaction()
            .add(R.id.map, mapFragment)
            .commit()
        mapFragment.getMapAsync(this)

        createEventDialog = Dialog(requireContext())

        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding.addEvent.setOnClickListener {
            CreateEventDialog().show(parentFragmentManager, "CreateEvent")
        }
    }

    override fun onResume() {
        super.onResume()
        if (::_googleMap.isInitialized)
            refreshMap()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        _googleMap = googleMap
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
                viewModel.updateLocation(Firebase.auth.currentUser!!.uid, location.latitude, location.longitude)
                val userPos = LatLng(location.latitude, location.longitude)
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(userPos))

                _binding.btnMyLocation.setOnClickListener {
                    googleMap.animateCamera(CameraUpdateFactory.newLatLng(userPos))
                }
            }
        }

        refreshMap()
    }

    fun refreshMap() {
        // Clear existing markers
        googleMap.clear()

        // Re-add markers with updated data
        viewModel.getEvents().observe(viewLifecycleOwner) {events ->
            events.forEach {
                if (Utils.isCurrentDateAfter(it.date))
                    return@forEach
                val lat = it.lat
                val long = it.long
                val marker = googleMap.addMarker(MarkerOptions().position(LatLng(lat, long)).title(it.name).snippet(it.time + " - " + it.date))
                marker!!.tag = it.name
            }
            (requireActivity() as InternalActivity).loadingDialog.dismiss()
        }

        googleMap.setOnMarkerClickListener { marker ->
            googleMap.animateCamera(CameraUpdateFactory.newLatLng(marker.position))

            viewModel.getEventByName(marker.tag as String).observe(viewLifecycleOwner) {
                if (it == null) {
                    Toast.makeText(requireContext(), "Event no longer available", Toast.LENGTH_SHORT).show()
                    return@observe
                }
                val dialog = EventInfoDialog()
                dialog.setEvent(it)
                dialog.show(requireActivity().supportFragmentManager, "event_info_dialog")
            }

            true
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            2 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(requireContext(), "Permission Granted", Toast.LENGTH_SHORT).show()
                    refreshMap()
                } else {
                    Toast.makeText(requireContext(), "Permission Denied", Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                }
                return
            }
        }
    }

}