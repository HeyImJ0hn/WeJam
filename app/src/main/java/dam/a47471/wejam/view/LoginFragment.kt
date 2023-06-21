package dam.a47471.wejam.view

import android.app.Dialog
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import dam.a47471.wejam.R
import dam.a47471.wejam.activities.MainActivity
import dam.a47471.wejam.databinding.FragmentLoginBinding
import dam.a47471.wejam.viewmodel.LoginViewModel

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var viewModel: LoginViewModel
    lateinit var dialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[LoginViewModel::class.java]
        binding = FragmentLoginBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.signUpBtn.setOnClickListener {
            findNavController().navigate(R.id.action_login_to_register)
        }

        binding.loginBtn.setOnClickListener {
            (activity as MainActivity).loadingDialog.show()
            viewModel.loginUser(binding.emailInput.text.toString(), binding.passwordInput.text.toString())
        }

        viewModel.isLoginSuccessful.observe(viewLifecycleOwner) { isLoginSuccessful ->
            if (isLoginSuccessful) {
                Toast.makeText(context, "Logged in: " + FirebaseAuth.getInstance().currentUser!!.displayName, Toast.LENGTH_SHORT).show()
            } else {
                (activity as MainActivity).loadingDialog.dismiss()
                Toast.makeText(context, "Login failed", Toast.LENGTH_SHORT).show()
            }
        }

        binding.googleBtn.setOnClickListener {
            (requireActivity() as MainActivity).oneTapSignIn()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as MainActivity).loadingDialog.dismiss()
    }

}