package dam.a47471.wejam.model

import android.os.Parcel
import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class Event(
    val owner: String,
    var name: String,
    val type: EventType,
    val locationName: String,
    val lat: Double,
    val long: Double,
    val time: String,
    val date: String,
    val attendees: MutableList<String> = mutableListOf()
) :
    Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        EventType.valueOf(parcel.readString()!!),
        parcel.readString()!!,
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.createStringArrayList() ?: mutableListOf(),
    )
}