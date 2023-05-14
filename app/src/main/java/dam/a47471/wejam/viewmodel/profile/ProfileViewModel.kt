package dam.a47471.wejam.viewmodel.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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

}