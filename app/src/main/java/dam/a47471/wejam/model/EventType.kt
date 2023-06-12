package dam.a47471.wejam.model

enum class EventType {
    JAM, CONCERT;

    override fun toString(): String {
        return when (this) {
            JAM -> "Jam Session"
            CONCERT -> "Concert"
        }
    }
}