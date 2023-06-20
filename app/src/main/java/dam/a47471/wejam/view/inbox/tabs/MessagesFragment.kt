package dam.a47471.wejam.view.inbox.tabs

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import dam.a47471.wejam.R
import dam.a47471.wejam.databinding.FragmentInboxMessagesBinding
import dam.a47471.wejam.viewmodel.inbox.InboxViewModel

class MessagesFragment : Fragment() {

    private lateinit var binding: FragmentInboxMessagesBinding
    private lateinit var viewModel: InboxViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[InboxViewModel::class.java]
        binding = FragmentInboxMessagesBinding.inflate(inflater, container, false)
        return binding.root
    }

}