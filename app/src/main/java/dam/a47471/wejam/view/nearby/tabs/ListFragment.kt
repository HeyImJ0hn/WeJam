package dam.a47471.wejam.view.nearby.tabs

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dam.a47471.wejam.R
import dam.a47471.wejam.databinding.FragmentNearbyListBinding

class ListFragment : Fragment() {

    private lateinit var binding: FragmentNearbyListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNearbyListBinding.inflate(inflater, container, false)
        return binding.root
    }

}