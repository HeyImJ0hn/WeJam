package dam.a47471.wejam.viewmodel.profile

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import dam.a47471.wejam.model.Repository
import dam.a47471.wejam.model.User

class EditProfileViewModel : ViewModel() {
    private val _user = MutableLiveData<User>()
    val user: LiveData<User>
        get() = _user

    private var repository: Repository = Repository()

    private fun updateEmail(email: String) {
        if (!isEmailValid(email))
            return

        FirebaseAuth.getInstance().currentUser?.updateEmail(email)
    }

    private fun isEmailValid(email: String): Boolean {
        val emailPattern = Regex("[a-zA-Z0-9._-]+@[a-zA-Z0-9._-]+\\.[a-zA-Z0-9._-]+")
        return emailPattern.matches(email)
    }

    fun updateProfile(username: String, realName: String, email: String, bio: String) {
        updateEmail(email)
        repository.updateUser(
            FirebaseAuth.getInstance().currentUser!!.uid,
            username, realName, email, bio
        )
    }

    fun updatePicture(uri: Uri) {
        repository.updatePicture(uri)
    }

    fun uploadProfilePicture(userId: String, uri: Uri) {
        repository.uploadProfilePicture(userId, uri)
    }

    fun getProfilePictureFromStorage(userId: String): Task<Uri> {
        return repository.getProfilePicture(userId)
    }

    fun getPfpResult(): MutableLiveData<Boolean> {
        return repository.pfpResult
    }

    fun loadUser(userId: String) {
        repository.getUser(userId) { user ->
            _user.value = user
        }
    }

}