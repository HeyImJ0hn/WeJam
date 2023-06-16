package dam.a47471.wejam.viewmodel.profile

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import dam.a47471.wejam.model.Repository
import dam.a47471.wejam.model.User

class ProfileViewModel : ViewModel() {
    private val _user = MutableLiveData<User>()
    val user: LiveData<User>
        get() = _user

    private val repository: Repository = Repository()

    fun loadUser(userId: String) {
        repository.loadUser(userId) { user ->
            _user.value = user
        }
    }

    fun getUserPicture(id: String): Task<Uri> {
        return repository.getProfilePicture(id)
    }

}