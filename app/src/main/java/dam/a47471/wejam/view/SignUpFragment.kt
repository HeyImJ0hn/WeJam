package dam.a47471.wejam.view

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import dam.a47471.wejam.R
import dam.a47471.wejam.activities.InternalActivity
import dam.a47471.wejam.activities.MainActivity
import dam.a47471.wejam.databinding.FragmentSignUpBinding
import dam.a47471.wejam.viewmodel.LoginViewModel

class SignUpFragment : Fragment() {

    private lateinit var binding: FragmentSignUpBinding
    private lateinit var viewModel: LoginViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[LoginViewModel::class.java]
        binding = FragmentSignUpBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backBtn.setOnClickListener {
            findNavController().navigate(R.id.action_register_to_login)
        }

        binding.signUpBtn.setOnClickListener {
            (activity as MainActivity).loadingDialog.show()
            viewModel.registerUser(binding.emailInput.text.toString(), binding.passwordInput.text.toString(), binding.confirmPasswordInput.text.toString(), binding.usernameInput.text.toString())
        }

        viewModel.isRegistrationSuccessful.observe(viewLifecycleOwner, Observer<Boolean> { isRegistrationSuccessful ->
            if (isRegistrationSuccessful) {
                Toast.makeText(context, "Registered user", Toast.LENGTH_SHORT).show()
            } else {
                (activity as InternalActivity).loadingDialog.dismiss()
                Toast.makeText(context, "Failed to register user", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as MainActivity).loadingDialog.dismiss()
    }

}