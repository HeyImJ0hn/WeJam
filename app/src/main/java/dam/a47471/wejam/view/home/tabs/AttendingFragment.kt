package dam.a47471.wejam.view.home.tabs

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dam.a47471.wejam.databinding.FragmentHomeAttendingBinding

class AttendingFragment : Fragment() {

    private lateinit var binding: FragmentHomeAttendingBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeAttendingBinding.inflate(inflater, container, false)
        return binding.root
    }

}