package dam.a47471.wejam.view.home.tabs

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dam.a47471.wejam.R
import dam.a47471.wejam.activities.InternalActivity
import dam.a47471.wejam.databinding.FragmentHomeFriendsBinding
import dam.a47471.wejam.model.Event
import dam.a47471.wejam.utils.EventInfoDialog
import dam.a47471.wejam.utils.Utils
import dam.a47471.wejam.view.home.adapters.AttendingListAdapter
import dam.a47471.wejam.view.home.adapters.FriendsEventListAdapter
import dam.a47471.wejam.viewmodel.home.HomeViewModel

class FriendsFragment : Fragment() {

    private lateinit var binding: FragmentHomeFriendsBinding
    private lateinit var viewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        binding = FragmentHomeFriendsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as InternalActivity).loadingDialog.show()

        val events: MutableList<Event> = mutableListOf()

        viewModel.getFriends().observe(viewLifecycleOwner) { friends ->
            binding.textView.visibility = View.GONE
            if (friends.isEmpty())
                binding.textView.visibility = View.VISIBLE
            var i = 0
            friends.forEach { friend ->
                viewModel.getEventsByAttendee(friend).observe(viewLifecycleOwner) { eventList ->
                    events.addAll(eventList)
                    if (i >= friends.size) {
                        val filteredEvents = events.distinct().filter {!Utils.isCurrentDateAfter(it.date)}
                        binding.textView.visibility = View.GONE
                        if (filteredEvents.isEmpty())
                            binding.textView.visibility = View.VISIBLE
                        binding.recyclerView.adapter =
                            FriendsEventListAdapter(filteredEvents, itemClickedListener = { event ->
                                val dialog = EventInfoDialog()
                                dialog.setEvent(event as Event)
                                dialog.show(
                                    requireActivity().supportFragmentManager,
                                    "event_info_dialog"
                                )
                            }, requireContext())
                    }
                }
                i++
            }
            (requireActivity() as InternalActivity).loadingDialog.dismiss()
        }
    }
}