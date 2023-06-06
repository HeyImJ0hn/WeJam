package dam.a47471.wejam.viewmodel.nearby

import androidx.lifecycle.ViewModel
import dam.a47471.wejam.model.Event
import dam.a47471.wejam.model.EventType
import dam.a47471.wejam.model.Repository
import java.util.*

class NearbyViewModel : ViewModel() {

    private val repository: Repository = Repository()

    fun createEvent(owner: String, name: String, type: EventType, time: String, date: String) {
        repository.createEvent(Event(owner, name, type, time, date))
    }

}