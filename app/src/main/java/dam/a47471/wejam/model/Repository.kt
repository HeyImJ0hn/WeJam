package dam.a47471.wejam.model

import android.content.ContentValues.TAG
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await

class Repository {

    private var database: DatabaseReference = Firebase.database.reference
    private var storage: FirebaseStorage = Firebase.storage
    private val firestore: FirebaseFirestore = Firebase.firestore

    private val _pfpResult = MutableLiveData<Boolean>();
    val pfpResult: LiveData<Boolean>
        get() = _pfpResult

    private val _bannerResult = MutableLiveData<Boolean>();
    val bannerResult: LiveData<Boolean>
        get() = _bannerResult

    fun writeNewUser(
        userId: String,
        username: String,
        realName: String,
        email: String,
        bio: String,
        uri: String
    ) {
        val user = User(username, realName, email, bio, uri)
        database.child("users").child(userId).setValue(user)
    }

    fun updateUser(userId: String, username: String, realName: String, email: String, bio: String) {

        val users = database.child("users")
        val userReference = users.child(userId)

        userReference.child("username").setValue(username)
        userReference.child("realName").setValue(realName)
        userReference.child("email").setValue(email)
        userReference.child("bio").setValue(bio)
    }

    fun updateLocation(userId: String, lat: Double, long: Double) {
        val users = database.child("users")
        val userReference = users.child(userId)

        userReference.child("lat").setValue(lat)
        userReference.child("long").setValue(long)
    }

    fun updatePicture(uri: Uri?) {
        FirebaseAuth.getInstance().currentUser?.updateProfile(
        userProfileChangeRequest {
            photoUri = uri
        })
    }

    fun updateBanner(userId: String, uri: Uri?) {
        val users = database.child("users")
        val userReference = users.child(userId)

        userReference.child("banner").setValue(uri.toString())
    }

    fun getUser(userId: String, callback: (User?) -> Unit) {
        val userRef = database.child("users").child(userId)
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                callback(user)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Failed to get user: $error")
                callback(null)
            }
        })
    }

    fun uploadProfilePicture(userId: String, uri: Uri) {
        storage.reference.child("profile").child(userId).child("picture").putFile(uri).addOnCompleteListener {
            _pfpResult.value = it.isSuccessful
        }
    }

    fun getProfilePicture(userId: String): Task<Uri> {
        return storage.reference.child("profile").child(userId).child("picture").downloadUrl
    }

    fun uploadBannerPicture(userId: String, uri: Uri) {
        storage.reference.child("profile").child(userId).child("banner").putFile(uri).addOnCompleteListener {
            _bannerResult.value = it.isSuccessful
        }
    }

    fun getBannerPicture(userId: String): Task<Uri> {
        return storage.reference.child("profile").child(userId).child("banner").downloadUrl
    }

    fun createEvent(event: Event) {
        firestore.collection("events")
            .add(event)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
    }

    fun getUserFromId(userId: String): LiveData<User> {
        val user = MutableLiveData<User>()
        database.child("users").child(userId).get().addOnSuccessListener {
            user.value = it.getValue(User::class.java)
        }.addOnFailureListener {
            println("Error getting data")
        }
        return user
    }

    fun getEvents(): LiveData<List<Event>> {
        val eventsLiveData = MutableLiveData<List<Event>>()

        firestore.collection("events")
            .get()
            .addOnSuccessListener { querySnapshot: QuerySnapshot ->
                val events = mutableListOf<Event>()
                for (document in querySnapshot.documents) {
                    println(document.id + " => " + document.data)
                    val owner = document.getString("owner")!!
                    val name = document.getString("name")!!
                    val type = document.getString("type")!!
                    val locationName = document.getString("locationName")!!
                    val lat = document.getDouble("lat")!!
                    val long = document.getDouble("long")!!
                    val time = document.getString("time")!!
                    val date = document.getString("date")!!
                    val attendees = document.get("attendees")!!

                    events.add(Event(owner, name, EventType.valueOf(type), locationName, lat, long, time, date, attendees as MutableList<String>))
                }
                eventsLiveData.value = events
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }
        return eventsLiveData
    }
}