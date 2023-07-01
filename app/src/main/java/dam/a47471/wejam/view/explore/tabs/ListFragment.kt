package dam.a47471.wejam.view.explore.tabs

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import dam.a47471.wejam.activities.InternalActivity
import dam.a47471.wejam.databinding.FragmentNearbyListBinding
import dam.a47471.wejam.model.Event
import dam.a47471.wejam.model.EventType
import dam.a47471.wejam.model.User
import dam.a47471.wejam.utils.EventInfoDialog
import dam.a47471.wejam.view.explore.adapters.EventListAdapter
import dam.a47471.wejam.viewmodel.explore.NearbyViewModel

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
        viewModel.loadUser(FirebaseAuth.getInstance().currentUser!!.uid)
        (requireActivity() as InternalActivity).loadingDialog.show()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.user.observe(viewLifecycleOwner) {
            user = it

            viewModel.getEvents().observe(viewLifecycleOwner) {list ->
                events = list
                println("Events Size: ${events.size}")
                binding.nearbyListRecyclerView.adapter = EventListAdapter(list, itemClickedListener = { event ->
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
        }

        binding.searchBar.setOnClickListener {
            binding.searchBar.isIconified = false
        }

        binding.jamBtn.isChecked = true

        binding.searchBar.clearFocus()

        binding.searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                binding.searchBar.clearFocus()
                (binding.nearbyListRecyclerView.adapter as EventListAdapter).filterName(query.orEmpty())
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                (binding.nearbyListRecyclerView.adapter as EventListAdapter).filterName(newText.orEmpty())
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