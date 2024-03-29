package dam.a47471.wejam.view.explore

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.tabs.TabLayoutMediator
import dam.a47471.wejam.activities.InternalActivity
import dam.a47471.wejam.databinding.FragmentNearbyBinding
import dam.a47471.wejam.view.explore.adapters.NearbyPagerAdapter
import dam.a47471.wejam.viewmodel.explore.NearbyViewModel

class NearbyFragment : Fragment() {

    private lateinit var viewModel: NearbyViewModel
    private lateinit var binding: FragmentNearbyBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[NearbyViewModel::class.java]
        binding = FragmentNearbyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewPager.isUserInputEnabled = false
        binding.viewPager.adapter =
            NearbyPagerAdapter(requireActivity().supportFragmentManager, lifecycle)
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Map"
                else -> "Nearby"
            }
        }.attach()
    }

}