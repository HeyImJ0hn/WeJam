package dam.a47471.wejam.view.home.tabs

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import dam.a47471.wejam.activities.InternalActivity
import dam.a47471.wejam.databinding.FragmentHomeSavedBinding
import dam.a47471.wejam.model.Event
import dam.a47471.wejam.utils.EventInfoDialog
import dam.a47471.wejam.utils.GeneralEventRecyclerViewAdapter
import dam.a47471.wejam.viewmodel.home.HomeViewModel

class SavedFragment : Fragment() {

    private lateinit var binding: FragmentHomeSavedBinding
    private lateinit var viewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        binding = FragmentHomeSavedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as InternalActivity).loadingDialog.show()

        viewModel.getFavouriteEvents().observe(viewLifecycleOwner) { savedNames ->
            val saved = mutableListOf<Event>()
            savedNames?.forEach { name ->
                viewModel.getEventByName(name).observe(viewLifecycleOwner) { event ->
                    saved.add(event)
                    if (saved.size == savedNames.size) {
                        binding.recyclerView.adapter = GeneralEventRecyclerViewAdapter(saved, itemClickedListener = {
                            val dialog = EventInfoDialog()
                            dialog.setEvent(it as Event)
                            dialog.show(
                                requireActivity().supportFragmentManager, "event_info_dialog"
                            )
                        }, requireContext())
                    }
                }
            }
            (requireActivity() as InternalActivity).loadingDialog.dismiss()
        }
    }


}