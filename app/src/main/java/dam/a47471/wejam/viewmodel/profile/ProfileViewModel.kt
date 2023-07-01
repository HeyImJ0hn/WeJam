package dam.a47471.wejam.viewmodel.profile

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import dam.a47471.wejam.model.Event
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

    fun getGoogleUserPicture(id: String): Task<DataSnapshot> {
        return repository.getGoogleProfilePicture(id)
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

    fun getFriendRequests(userId: String): LiveData<List<String>> {
        return repository.getFriendRequests(userId)
    }

    fun getFriends(): LiveData<List<String>> {
        return repository.getFriendList()
    }

    fun acceptFriendRequest(friendId: String) {
        repository.acceptFriendRequest(friendId)
    }

    fun removeFriendRequest(userId: String, friendId: String) {
        repository.removeFriendRequest(userId, friendId)
    }

    fun removeFriend(friendId: String) {
        repository.removeFriend(friendId)
    }

}