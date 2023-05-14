package dam.a47471.wejam.view.nearby.tabs

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import dam.a47471.wejam.databinding.FragmentNearbyMapBinding

class MapFragment : Fragment() {

    private lateinit var binding: FragmentNearbyMapBinding
    private lateinit var mapView: MapView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNearbyMapBinding.inflate(inflater, container, false)

        mapView = binding.mapView
        mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS)
        return binding.root
    }
}