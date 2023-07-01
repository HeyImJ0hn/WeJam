package dam.a47471.wejam.model

import android.os.Parcel
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val userId: String? = null,
    val username: String? = null,
    val realName: String? = null,
    val email: String? = null,
    val bio: String? = null,
    val profilePicture: String? = null,
    val banner: String? = null,
    val lat: Double? = null,
    val long: Double? = null
) :
    Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readDouble(),
        parcel.readDouble()
    )

}
