package dam.a47471.wejam.model

import java.util.UUID

data class Event(val owner: UUID, var name: String, val type: EventType, val time: String, val date: String) {

}