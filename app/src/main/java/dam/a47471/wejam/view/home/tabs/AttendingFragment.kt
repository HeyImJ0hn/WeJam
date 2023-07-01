package dam.a47471.wejam.view.home.tabs

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dam.a47471.wejam.activities.InternalActivity
import dam.a47471.wejam.databinding.FragmentHomeAttendingBinding
import dam.a47471.wejam.model.Event
import dam.a47471.wejam.utils.EventInfoDialog
import dam.a47471.wejam.utils.Utils
import dam.a47471.wejam.view.home.adapters.AttendingListAdapter
import dam.a47471.wejam.viewmodel.home.HomeViewModel

class AttendingFragment : Fragment() {

    private lateinit var binding: FragmentHomeAttendingBinding
    private lateinit var viewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        binding = FragmentHomeAttendingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as InternalActivity).loadingDialog.show()

        viewModel.getEventsByAttendee(Firebase.auth.currentUser!!.uid)
            .observe(viewLifecycleOwner) {list ->
                val events = list.filter {!Utils.isCurrentDateAfter(it.date)}
                if (events.isEmpty())
                    binding.textView.visibility = View.VISIBLE
                binding.attendingRecyclerView.adapter = AttendingListAdapter(events, itemClickedListener = { event ->
                    val dialog = EventInfoDialog()
                    dialog.setEvent(event as Event)
                    dialog.show(requireActivity().supportFragmentManager, "event_info_dialog")
                }, requireContext())
                (requireActivity() as InternalActivity).loadingDialog.dismiss()
            }

        binding.searchBar.setOnClickListener {
            binding.searchBar.isIconified = false
        }

        binding.searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                binding.searchBar.clearFocus()
                (binding.attendingRecyclerView.adapter as AttendingListAdapter).filterName(query.orEmpty())
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                (binding.attendingRecyclerView.adapter as AttendingListAdapter).filterName(newText.orEmpty())
                return true
            }
        })

    }

}