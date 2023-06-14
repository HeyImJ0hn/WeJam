package dam.a47471.wejam.view.nearby.tabs

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import dam.a47471.wejam.R
import dam.a47471.wejam.activities.InternalActivity
import dam.a47471.wejam.activities.MainActivity
import dam.a47471.wejam.databinding.FragmentNearbyListBinding
import dam.a47471.wejam.model.Event
import dam.a47471.wejam.model.EventType
import dam.a47471.wejam.model.User
import dam.a47471.wejam.view.nearby.EventInfoDialog
import dam.a47471.wejam.viewmodel.nearby.NearbyViewModel

class ListFragment : Fragment() {

    private lateinit var binding: FragmentNearbyListBinding
    private lateinit var viewModel: NearbyViewModel
    private lateinit var events: List<Event>

    private lateinit var user: User

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNearbyListBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[NearbyViewModel::class.java]

        (requireActivity() as InternalActivity).loadingDialog.show()

        viewModel.user.observe(viewLifecycleOwner) {
            user = it
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.loadUser(FirebaseAuth.getInstance().currentUser!!.uid)

        viewModel.getEvents().observe(viewLifecycleOwner) {it ->
            events = it
            binding.nearbyListRecyclerView.adapter = EventListAdapter(it, itemClickedListener = { event ->
                val e = event as Event

                val dialog = EventInfoDialog()
                dialog.setEvent(e)
                dialog.show(requireActivity().supportFragmentManager, "event_info_dialog")

            }, requireContext())

            val adapter = binding.nearbyListRecyclerView.adapter as EventListAdapter
            adapter.filterDistance(user.lat!!, user.long!!)
            adapter.filterType(EventType.JAM)
            (requireActivity() as InternalActivity).loadingDialog.dismiss()
        }

        binding.searchBar.setOnClickListener {
            binding.searchBar.isIconified = false
        }

        binding.jamBtn.isChecked = true

        binding.searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                binding.searchBar.clearFocus()
                events.forEach {
                    if (it.name.contains(query.toString())) {
                        (binding.nearbyListRecyclerView.adapter as EventListAdapter).filterName(query.toString())
                    }
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                (binding.nearbyListRecyclerView.adapter as EventListAdapter).filterName(newText.toString())
                return false
            }
        })

        binding.jamBtn.setOnClickListener {
            if (!binding.concertBtn.isChecked)
                binding.jamBtn.isChecked = true

            (binding.nearbyListRecyclerView.adapter as EventListAdapter).filterType(EventType.JAM)

            binding.concertBtn.isChecked = false
        }

        binding.concertBtn.setOnClickListener {
            if (!binding.jamBtn.isChecked)
                binding.concertBtn.isChecked = true

            (binding.nearbyListRecyclerView.adapter as EventListAdapter).filterType(EventType.CONCERT)

            binding.jamBtn.isChecked = false
        }

    }

}