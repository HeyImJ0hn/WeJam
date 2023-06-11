package dam.a47471.wejam.model

import java.util.UUID

data class Event(val owner: String, var name: String, val type: EventType, val locationName: String, val lat: Double, val long: Double, val time: String, val date: String, val attendees: MutableList<String> = mutableListOf()) {
}