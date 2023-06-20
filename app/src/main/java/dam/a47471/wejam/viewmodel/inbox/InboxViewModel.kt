package dam.a47471.wejam.viewmodel.inbox

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dam.a47471.wejam.model.Repository
import dam.a47471.wejam.model.User

class InboxViewModel : ViewModel() {

    private val _friendRequests = MutableLiveData<List<String>>()
    val friendRequests: MutableLiveData<List<String>>
        get() = _friendRequests

    private val _user = MutableLiveData<User>()
    val user: MutableLiveData<User>
        get() = _user

    private val repository = Repository()

    fun loadFriendRequests(userId: String) {
        repository.getFriendRequests(userId) { friendRequests ->
            _friendRequests.value = friendRequests
        }
    }

    fun loadUser(userId: String) {
        repository.loadUser(userId) { user ->
            _user.value = user
        }
    }

}