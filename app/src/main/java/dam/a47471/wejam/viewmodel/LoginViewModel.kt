package dam.a47471.wejam.viewmodel

import android.content.ContentValues
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dam.a47471.wejam.model.Repository
import dam.a47471.wejam.model.User


class LoginViewModel : ViewModel() {
    private val _isRegistrationSuccessful = MutableLiveData<Boolean>()
    val isRegistrationSuccessful: LiveData<Boolean>
        get() = _isRegistrationSuccessful

    private val _isLoginSuccessful = MutableLiveData<Boolean>()
    val isLoginSuccessful: LiveData<Boolean>
        get() = _isLoginSuccessful

    private val repository = Repository()

    fun loginUser(email: String, password: String) {
        val firebaseAuth = FirebaseAuth.getInstance()

        if (email.isEmpty() || password.isEmpty()) {
            _isLoginSuccessful.value = false
            return
        }

        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d(ContentValues.TAG, "signInWithEmail:success")
                val user = firebaseAuth.currentUser
                _isLoginSuccessful.value = true
            } else {
                Log.w(ContentValues.TAG, "signInWithEmail:failure", task.exception)
                _isLoginSuccessful.value = false
            }
        }
    }

    fun registerUser(email: String, password: String, confirmPassword: String, name: String) {
        val firebaseAuth = FirebaseAuth.getInstance()

        if (!isEmailValid(email) || !doPasswordsMatch(password, confirmPassword)) {
            _isRegistrationSuccessful.value = false
            return
        }

        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener {
            _isRegistrationSuccessful.value = true
            repository.writeNewUser(
                Firebase.auth.currentUser!!.uid,
                name,
                "",
                email,
                "",
                "https://firebasestorage.googleapis.com/v0/b/***REMOVED***.appspot.com/o/defaults%2Favatar.png?alt=media&token=da857e8d-320a-4cc8-a6c9-6ef370aa445a",
                Uri.EMPTY.toString()
            )
        }.addOnFailureListener {
            println(it)
            _isRegistrationSuccessful.value = false
        }
    }

    fun registerUserToken(idToken: String) {
        val firebaseAuth = FirebaseAuth.getInstance()

        val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(firebaseCredential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(ContentValues.TAG, "signInWithCredential:success")
                    val user = Firebase.auth.currentUser!!
                    repository.writeNewUser(
                        user.uid,
                        user.email!!.split("@")[0],
                        user.displayName!!,
                        user.email!!,
                        "",
                        user.photoUrl.toString(),
                        Uri.EMPTY.toString()
                    )
                    _isRegistrationSuccessful.value = true
                } else {
                    Log.w(ContentValues.TAG, "signInWithCredential:failure", task.exception)
                    _isRegistrationSuccessful.value = false
                }
            }
    }

    private fun isEmailValid(email: String): Boolean {
        val emailPattern = Regex("[a-zA-Z0-9._-]+@[a-zA-Z0-9._-]+\\.[a-zA-Z0-9._-]+")
        return emailPattern.matches(email)
    }

    fun searchUsers(username: String) {
        repository.searchUsers(username)
    }

    fun searchResult(): LiveData<List<User>> {
        return repository.userSearchResult
    }

    private fun doPasswordsMatch(password: String, confirmation: String): Boolean {
        return password == confirmation
    }

}