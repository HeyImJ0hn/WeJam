package dam.a47471.wejam.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import dam.a47471.wejam.model.Repository


class SignUpViewModel : ViewModel() {
    private val _isRegistrationSuccessful = MutableLiveData<Boolean>()
    val isRegistrationSuccessful: LiveData<Boolean>
        get() = _isRegistrationSuccessful

    fun registerUser(email: String, password: String, confirmPassword: String, name: String) {
        val firebaseAuth = FirebaseAuth.getInstance()

        if (!isEmailValid(email) || !doPasswordsMatch(password, confirmPassword)) {
            _isRegistrationSuccessful.value = false
            return
        }

        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    Repository().writeNewUser(
                        user!!.uid,
                        name,
                        "",
                        email,
                        "",
                        "https://firebasestorage.googleapis.com/v0/b/***REMOVED***.appspot.com/o/defaults%2Favatar.png?alt=media&token=da857e8d-320a-4cc8-a6c9-6ef370aa445a",
                        Uri.EMPTY.toString()
                    )
                    _isRegistrationSuccessful.value = true
                } else {
                    _isRegistrationSuccessful.value = false
                }
            }
    }

    private fun isEmailValid(email: String): Boolean {
        val emailPattern = Regex("[a-zA-Z0-9._-]+@[a-zA-Z0-9._-]+\\.[a-zA-Z0-9._-]+")
        return emailPattern.matches(email)
    }

    private fun doPasswordsMatch(password: String, confirmation: String): Boolean {
        return password == confirmation
    }

}