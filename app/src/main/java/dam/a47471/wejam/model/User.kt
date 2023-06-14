package dam.a47471.wejam.model

import android.net.Uri
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class User(val username: String? = null, val realName: String? = null, val email: String? = null, val bio: String? = null, val banner: String? = null, val lat: Double? = null, val long: Double? = null) {
    // Null default values create a no-argument default constructor, which is needed
    // for deserialization from a DataSnapshot.
}
