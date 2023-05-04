package dam.a47471.wejam.view

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dam.a47471.wejam.R
import dam.a47471.wejam.databinding.FragmentSignUpBinding
import dam.a47471.wejam.viewmodel.SignUpViewModel

class SignUpFragment : Fragment() {

    private lateinit var binding: FragmentSignUpBinding
    private lateinit var viewModel: SignUpViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[SignUpViewModel::class.java]
        binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

}