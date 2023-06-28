package dam.a47471.wejam.viewmodel.home

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import dam.a47471.wejam.model.Event
import dam.a47471.wejam.model.Repository
import dam.a47471.wejam.model.User

class HomeViewModel : ViewModel() {
    private val repository: Repository = Repository()

    private val _user = MutableLiveData<User>()
    val user: LiveData<User>
        get() = _user

    private val _friends = MutableLiveData<List<String>>()
    val friends: LiveData<List<String>>
        get() = _friends

    fun getEvents(): LiveData<List<Event>> {
        return repository.getEvents()
    }

    fun getEventsByAttendee(userId: String): LiveData<List<Event>> {
        return repository.getEventsByAttendee(userId)
    }

    fun getUserPicture(id: String): Task<Uri> {
        return repository.getProfilePicture(id)
    }

    fun loadUser(userId: String) {
        repository.loadUser(userId) { user ->
            _user.value = user
        }
    }

    fun updateLocation(userId: String, lat: Double, long: Double) {
        repository.updateLocation(userId, lat, long)
    }

    fun getEventByName(name: String): LiveData<Event> {
        return repository.getEventByName(name)
    }

    fun addAttendee(event: Event, userId: String) {
        repository.addAttendee(event, userId)
    }

    fun removeAttendee(event: Event, userId: String) {
        repository.removeAttendee(event, userId)
    }

    fun deleteEvent(event: Event) {
        return repository.deleteEvent(event)
    }

    fun loadFriends() {
        repository.getFriendList {
            _friends.value = it
        }
    }

    fun getFavouriteEvents(): LiveData<List<String>?> {
        return repository.getFavouriteEvents()
    }
}