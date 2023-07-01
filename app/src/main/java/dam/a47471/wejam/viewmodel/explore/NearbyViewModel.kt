package dam.a47471.wejam.viewmodel.explore

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import dam.a47471.wejam.model.Event
import dam.a47471.wejam.model.EventType
import dam.a47471.wejam.model.Repository
import dam.a47471.wejam.model.User

class NearbyViewModel : ViewModel() {

    private val repository: Repository = Repository()

    private val _user = MutableLiveData<User>()
    val user: LiveData<User>
        get() = _user

    fun createEvent(
        owner: String,
        name: String,
        type: EventType,
        location: String,
        lat: Double,
        long: Double,
        time: String,
        date: String,
        attendees: MutableList<String> = mutableListOf()
    ) {
        val event = Event(owner, name, type, location, lat, long, time, date, attendees)
        repository.createEvent(event)
    }

    fun getEvents(): LiveData<List<Event>> {
        return repository.getEvents()
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

    fun getEventByName(name: String): LiveData<Event?> {
        return repository.getEventByName(name)
    }

    fun addAttendee(event: Event, userId: String) {
        repository.addAttendee(event, userId)
    }

    fun removeAttendee(event: Event, userId: String) {
        repository.removeAttendee(event, userId)
    }

    fun deleteEvent(event: Event) {
        repository.deleteEvent(event)
    }

    fun favouriteEvent(eventName: String) {
        repository.favouriteEvent(eventName)
    }

    fun unfavouriteEvent(eventName: String) {
        repository.unfavouriteEvent(eventName)
    }

    fun getFavouriteEvents(): LiveData<List<String>?> {
        return repository.getFavouriteEvents()
    }

}