package dam.a47471.wejam.view.inbox

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.provider.Telephony.Mms.Inbox
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.tabs.TabLayoutMediator
import dam.a47471.wejam.databinding.FragmentInboxBinding
import dam.a47471.wejam.view.home.adapters.HomePagerAdapter
import dam.a47471.wejam.view.inbox.adapter.InboxPagerAdapter
import dam.a47471.wejam.viewmodel.inbox.InboxViewModel

class InboxFragment : Fragment() {

    private lateinit var binding: FragmentInboxBinding
    private lateinit var viewModel: InboxViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[InboxViewModel::class.java]
        binding = FragmentInboxBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewPager.adapter =
            InboxPagerAdapter(requireActivity().supportFragmentManager, lifecycle)
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Messages"
                1 -> "Friends"
                else -> "Requests"
            }
        }.attach()
    }

}