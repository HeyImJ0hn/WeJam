package dam.a47471.wejam.view.nearby.tabs

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import dam.a47471.wejam.R
import dam.a47471.wejam.databinding.FragmentNearbyListBinding
import dam.a47471.wejam.model.Event
import dam.a47471.wejam.viewmodel.nearby.NearbyViewModel

class ListFragment : Fragment() {

    private lateinit var binding: FragmentNearbyListBinding
    private lateinit var viewModel: NearbyViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNearbyListBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[NearbyViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getEvents().observe(viewLifecycleOwner) {
            binding.nearbyListRecyclerView.adapter = EventListAdapter(it, itemClickedListener = { event ->
                Toast.makeText(view.context, "Clicked on ${(event as Event).name}", Toast.LENGTH_SHORT).show()
            }, requireContext())
        }

    }

}