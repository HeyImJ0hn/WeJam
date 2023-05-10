package dam.a47471.wejam.view

import android.content.ContentValues.TAG
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backBtn.setOnClickListener {
            findNavController().navigate(R.id.action_register_to_login)
        }

        binding.signUpBtn.setOnClickListener {
            if (isEmailValid(binding.emailInput.text.toString())) {
                registerUser(binding.emailInput.text.toString(), binding.passwordInput.text.toString())
            }
        }

    }

    private fun registerUser(email: String, password: String) {
        val auth = Firebase.auth
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(requireActivity()) { task: Task<AuthResult> ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
                    Toast.makeText(
                        activity?.baseContext,
                        "User registered",
                        Toast.LENGTH_SHORT,
                    ).show()
                    // change activity
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        activity?.baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                    //updateUI(null)
                }
            }
    }

    private fun isEmailValid(email: String): Boolean {
        val emailPattern = Regex("[a-zA-Z0-9._-]+@[a-zA-Z0-9._-]+\\.[a-zA-Z0-9._-]+")
        return emailPattern.matches(email)
    }


}