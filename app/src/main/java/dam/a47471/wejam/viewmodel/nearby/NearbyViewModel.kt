package dam.a47471.wejam.viewmodel.nearby

import androidx.lifecycle.ViewModel
import dam.a47471.wejam.model.Event
import dam.a47471.wejam.model.EventType
import dam.a47471.wejam.model.Repository
import java.util.*

class NearbyViewModel : ViewModel() {

    val repository: Repository = Repository()

    fun createEvent(owner: UUID, name: String, type: EventType, time: String, date: String) {
        val event = Event(owner, name, type, time, date)
        // Upload to DB
    }

}