package dam.a47471.wejam.view.home.tabs

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dam.a47471.wejam.R
import dam.a47471.wejam.databinding.FragmentHomeFriendsBinding

class FriendsFragment : Fragment() {

    private lateinit var binding: FragmentHomeFriendsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeFriendsBinding.inflate(inflater, container, false)
        return binding.root
    }
}