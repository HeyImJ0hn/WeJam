package dam.a47471.wejam.utils

import dam.a47471.wejam.model.Event
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class Utils {
    companion object {
        fun isCurrentDateAfter(targetDateStr: String): Boolean {
            val formatter = DateTimeFormatter.ofPattern("MMM d, yyyy")
            val currentDate = LocalDate.now()
            val targetDate = LocalDate.parse(targetDateStr, formatter)
            return currentDate.isAfter(targetDate)
        }

        fun isAttendee(event: Event, userId: String): Boolean {
            return event.attendees.contains(userId)
        }
    }
}