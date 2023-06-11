package dam.a47471.wejam.viewmodel.nearby

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dam.a47471.wejam.model.Event
import dam.a47471.wejam.model.EventType
import dam.a47471.wejam.model.Repository
import java.util.*

class NearbyViewModel : ViewModel() {

    private val repository: Repository = Repository()

    fun createEvent(owner: String, name: String, type: EventType, location: String, lat: Double, long: Double, time: String, date: String) {
        val event = Event(owner, name, type, location, lat, long, time, date)
        repository.createEvent(event)
    }

    fun getEvents(): LiveData<List<Event>> {
        return repository.getEvents()
    }

}