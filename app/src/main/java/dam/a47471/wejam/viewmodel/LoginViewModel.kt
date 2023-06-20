package dam.a47471.wejam.viewmodel

import android.content.ContentValues.TAG
import android.util.Log
import android.widget.Toast
import androidx.core.content.contentValuesOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class LoginViewModel : ViewModel() {
    private val _isLoginSuccessful = MutableLiveData<Boolean>()
    val isLoginSuccessful: LiveData<Boolean>
        get() = _isLoginSuccessful

    fun loginUser(email: String, password: String) {
        val firebaseAuth = FirebaseAuth.getInstance()

        if (email.isEmpty() || password.isEmpty()) {
            _isLoginSuccessful.value = false
            return
        }

        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d(TAG, "signInWithEmail:success")
                val user = firebaseAuth.currentUser
                _isLoginSuccessful.value = true
            } else {
                Log.w(TAG, "signInWithEmail:failure", task.exception)
                _isLoginSuccessful.value = false
            }
        }
    }
}