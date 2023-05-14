package dam.a47471.wejam.view.profile.tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dam.a47471.wejam.databinding.FragmentProfileAboutBinding

class AboutFragment : Fragment() {

    private lateinit var _binding: FragmentProfileAboutBinding
    val binding: FragmentProfileAboutBinding
        get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileAboutBinding.inflate(inflater, container, false)
        return _binding.root
    }

}