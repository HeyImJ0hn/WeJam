package dam.a47471.wejam.viewmodel.nearby

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import dam.a47471.wejam.model.Event
import dam.a47471.wejam.model.EventType
import dam.a47471.wejam.model.Repository
import dam.a47471.wejam.model.User
import kotlin.math.*

class NearbyViewModel : ViewModel() {

    private val repository: Repository = Repository()

    private val _user = MutableLiveData<User>()
    val user: LiveData<User>
        get() = _user

    fun createEvent(owner: String, name: String, type: EventType, location: String, lat: Double, long: Double, time: String, date: String) {
        val event = Event(owner, name, type, location, lat, long, time, date)
        repository.createEvent(event)
    }

    fun getEvents(): LiveData<List<Event>> {
        return repository.getEvents()
    }

    fun getUserFromId(id: String): LiveData<User> {
        return repository.getUserFromId(id)
    }

    fun getUserPicture(id: String): Task<Uri> {
        return repository.getProfilePicture(id)
    }

    fun loadUser(userId: String) {
        repository.getUser(userId) { user ->
            _user.value = user
        }
    }

    fun updateLocation(userId: String, lat: Double, long: Double) {
        repository.updateLocation(userId, lat, long)
    }

}