package dam.a47471.wejam.viewmodel.profile

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.core.Repo
import dam.a47471.wejam.model.Repository
import dam.a47471.wejam.model.User

class ProfileViewModel : ViewModel() {
    private val _user = MutableLiveData<User>()
    val user: LiveData<User>
        get() = _user

    private val repository: Repository = Repository()

    fun loadUser(userId: String) {
        repository.getUser(userId) { user ->
            _user.value = user
        }
    }

    fun getProfilePictureFromStorage(userId: String): Task<Uri> {
        return repository.getProfilePicture(userId)
    }

}