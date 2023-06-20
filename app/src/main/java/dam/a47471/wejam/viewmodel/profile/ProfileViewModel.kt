package dam.a47471.wejam.viewmodel.profile

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import dam.a47471.wejam.model.Event
import dam.a47471.wejam.model.Repository
import dam.a47471.wejam.model.User

class ProfileViewModel : ViewModel() {
    private val _user = MutableLiveData<User>()
    val user: LiveData<User>
        get() = _user

    private val _friends = MutableLiveData<List<String>>()
    val friends: LiveData<List<String>>
        get() = _friends

    private val _friendRequests = MutableLiveData<List<String>>()
    val friendRequests: LiveData<List<String>>
        get() = _friendRequests

    private val repository: Repository = Repository()

    fun loadUser(userId: String) {
        repository.loadUser(userId) { user ->
            _user.value = user
        }
    }

    fun getUserPicture(id: String): Task<Uri> {
        return repository.getProfilePicture(id)
    }

    fun getDefaultAvatar(): Task<Uri> {
        return repository.getDefaultAvatar()
    }

    fun getEventsByOwner(userId: String): LiveData<List<Event>> {
        return repository.getEventsByOwner(userId)
    }

    fun sendFriendRequest(friendId: String) {
        repository.sendFriendRequest(friendId)
    }

    fun loadFriends() {
        repository.getFriendList { friends ->
            _friends.value = friends
        }
    }

    fun loadFriendRequests(userId: String) {
        repository.getFriendRequests(userId) { friendRequests ->
            _friendRequests.value = friendRequests
        }
    }

    fun acceptFriendRequest(friendId: String) {
        repository.acceptFriendRequest(friendId)
    }

    fun removeFriendRequest(friendId: String) {
        repository.removeFriendRequest(friendId)
    }

    fun removeFriend(friendId: String) {
        repository.removeFriend(friendId)
    }

}