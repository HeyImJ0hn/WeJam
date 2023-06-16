package dam.a47471.wejam.view.profile.tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dam.a47471.wejam.activities.InternalActivity
import dam.a47471.wejam.databinding.FragmentProfileEventsBinding
import dam.a47471.wejam.model.Event
import dam.a47471.wejam.model.EventType
import dam.a47471.wejam.utils.EventInfoDialog
import dam.a47471.wejam.view.explore.adapters.EventListAdapter
import dam.a47471.wejam.view.profile.adapters.ProfileEventsListAdapter
import dam.a47471.wejam.viewmodel.profile.ProfileViewModel

class EventsFragment : Fragment() {

    private lateinit var viewModel: ProfileViewModel
    private lateinit var binding: FragmentProfileEventsBinding
    private var events: List<Event> = listOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[ProfileViewModel::class.java]
        binding = FragmentProfileEventsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var userId = Firebase.auth.currentUser!!.uid

        if (arguments != null)
            userId = arguments?.getString("userId")!!

        viewModel.getEventsByOwner(userId).observe(viewLifecycleOwner) {
            println(it)
            events = it
            binding.recyclerView.adapter = ProfileEventsListAdapter(it, itemClickedListener = { event ->
                val e = event as Event

                val dialog = EventInfoDialog()
                dialog.setEvent(e)
                dialog.show(requireActivity().supportFragmentManager, "event_info_dialog")
            }, requireContext())
            (requireActivity() as InternalActivity).loadingDialog.dismiss()
        }
    }

}