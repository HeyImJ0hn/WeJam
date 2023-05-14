package dam.a47471.wejam.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class User(val username: String? = null, val realName: String? = null, val email: String? = null, val bio: String? = null) {
    // Null default values create a no-argument default constructor, which is needed
    // for deserialization from a DataSnapshot.
}
