package dam.a47471.wejam.view.profile.tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dam.a47471.wejam.databinding.FragmentProfileEventsBinding

class EventsFragment : Fragment() {

    private lateinit var binding: FragmentProfileEventsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileEventsBinding.inflate(inflater, container, false)
        return binding.root
    }

}