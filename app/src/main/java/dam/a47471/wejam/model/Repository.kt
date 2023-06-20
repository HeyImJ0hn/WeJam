package dam.a47471.wejam.model

import android.content.ContentValues.TAG
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.util.concurrent.CompletableFuture

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

    private val _userSearchResult = MutableLiveData<List<User>>();
    val userSearchResult: LiveData<List<User>>
        get() = _userSearchResult

    fun writeNewUser(
        userId: String,
        username: String,
        realName: String,
        email: String,
        bio: String,
        pPicUri: String,
        bannerUri: String
    ) {
        val user = User(userId, username, realName, email, bio, pPicUri, bannerUri, 0.0, 0.0)
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
        val users = database.child("users")
        val userReference = users.child(FirebaseAuth.getInstance().currentUser!!.uid)

        userReference.child("profilePicture").setValue(uri.toString())

        FirebaseAuth.getInstance().currentUser?.updateProfile(userProfileChangeRequest {
            photoUri = uri
        })
    }

    fun updateBanner(userId: String, uri: Uri?) {
        val users = database.child("users")
        val userReference = users.child(userId)

        userReference.child("banner").setValue(uri.toString())
    }

    fun loadUser(userId: String, callback: (User?) -> Unit) {
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
        storage.reference.child("profile").child(userId).child("picture").putFile(uri)
            .addOnCompleteListener {
                _pfpResult.value = it.isSuccessful
            }
    }

    fun getProfilePicture(userId: String): Task<Uri> {
        return storage.reference.child("profile").child(userId).child("picture").downloadUrl
    }

    fun uploadBannerPicture(userId: String, uri: Uri) {
        storage.reference.child("profile").child(userId).child("banner").putFile(uri)
            .addOnCompleteListener {
                _bannerResult.value = it.isSuccessful
            }
    }

    fun getBannerPicture(userId: String): Task<Uri> {
        return storage.reference.child("profile").child(userId).child("banner").downloadUrl
    }

    fun getDefaultAvatar(): Task<Uri> {
        return storage.reference.child("defaults").child("avatar.png").downloadUrl
    }

    fun createEvent(event: Event) {
        firestore.collection("events").add(event).addOnSuccessListener { documentReference ->
            Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
        }.addOnFailureListener { e ->
            Log.w(TAG, "Error adding document", e)
        }
    }

    fun getEvents(): LiveData<List<Event>> {
        val eventsLiveData = MutableLiveData<List<Event>>()

        firestore.collection("events").get().addOnSuccessListener { querySnapshot: QuerySnapshot ->
            val events = mutableListOf<Event>()
            for (document in querySnapshot.documents) {
                val owner = document.getString("owner")!!
                val name = document.getString("name")!!
                val type = document.getString("type")!!
                val locationName = document.getString("locationName")!!
                val lat = document.getDouble("lat")!!
                val long = document.getDouble("long")!!
                val time = document.getString("time")!!
                val date = document.getString("date")!!
                val attendees = document.get("attendees")!!

                events.add(
                    Event(
                        owner,
                        name,
                        EventType.valueOf(type),
                        locationName,
                        lat,
                        long,
                        time,
                        date,
                        attendees as MutableList<String>
                    )
                )
            }
            eventsLiveData.value = events
        }.addOnFailureListener { exception ->
            Log.w(TAG, "Error getting documents: ", exception)
        }
        return eventsLiveData
    }

    fun getEventByName(eventName: String): LiveData<Event> {
        val eventLiveData = MutableLiveData<Event>()

        firestore.collection("events").whereEqualTo("name", eventName).limit(1).get()
            .addOnSuccessListener { querySnapshot: QuerySnapshot ->
                val document = querySnapshot.documents[0]

                val owner = document.getString("owner")!!
                val name = document.getString("name")!!
                val type = document.getString("type")!!
                val locationName = document.getString("locationName")!!
                val lat = document.getDouble("lat")!!
                val long = document.getDouble("long")!!
                val time = document.getString("time")!!
                val date = document.getString("date")!!
                val attendees = document.get("attendees")!!

                eventLiveData.value = Event(
                    owner,
                    name,
                    EventType.valueOf(type),
                    locationName,
                    lat,
                    long,
                    time,
                    date,
                    attendees as MutableList<String>
                )
            }.addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }

        return eventLiveData
    }

    fun getEventsByAttendee(attendee: String): LiveData<List<Event>> {
        val eventsLiveData = MutableLiveData<List<Event>>()

        firestore.collection("events").whereArrayContains("attendees", attendee)
            .get().addOnSuccessListener { querySnapshot: QuerySnapshot ->
                val events = mutableListOf<Event>()
                for (document in querySnapshot.documents) {
                    val owner = document.getString("owner")!!
                    val name = document.getString("name")!!
                    val type = document.getString("type")!!
                    val locationName = document.getString("locationName")!!
                    val lat = document.getDouble("lat")!!
                    val long = document.getDouble("long")!!
                    val time = document.getString("time")!!
                    val date = document.getString("date")!!
                    val attendees = document.get("attendees")!!

                    events.add(
                        Event(
                            owner,
                            name,
                            EventType.valueOf(type),
                            locationName,
                            lat,
                            long,
                            time,
                            date,
                            attendees as MutableList<String>
                        )
                    )
                }
                eventsLiveData.value = events
            }.addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }
        return eventsLiveData
    }

    fun getEventsByOwner(userId: String): LiveData<List<Event>> {
        val eventsLiveData = MutableLiveData<List<Event>>()

        firestore.collection("events").whereEqualTo("owner", userId)
            .get().addOnSuccessListener { querySnapshot: QuerySnapshot ->
                val events = mutableListOf<Event>()
                for (document in querySnapshot.documents) {
                    val owner = document.getString("owner")!!
                    val name = document.getString("name")!!
                    val type = document.getString("type")!!
                    val locationName = document.getString("locationName")!!
                    val lat = document.getDouble("lat")!!
                    val long = document.getDouble("long")!!
                    val time = document.getString("time")!!
                    val date = document.getString("date")!!
                    val attendees = document.get("attendees")!!

                    events.add(
                        Event(
                            owner,
                            name,
                            EventType.valueOf(type),
                            locationName,
                            lat,
                            long,
                            time,
                            date,
                            attendees as MutableList<String>
                        )
                    )
                }
                eventsLiveData.value = events
            }.addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }
        return eventsLiveData
    }

    fun addAttendee(event: Event, userId: String) {
        val eventName = event.name

        firestore.collection("events").whereEqualTo("name", eventName).limit(1).get()
            .addOnSuccessListener { querySnapshot ->
                val eventRef = querySnapshot.documents[0].reference
                eventRef.update(
                    "attendees",
                    FieldValue.arrayUnion(userId) //FieldValue.arrayUnion() allows the addiction of an element to an array without overwriting
                ).addOnSuccessListener {
                    Log.w(TAG, "Successfully added attendee")
                }.addOnFailureListener { exception ->
                    Log.w(TAG, "Failed adding attendee", exception)
                }
            }.addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }
    }

    fun removeAttendee(event: Event, userId: String) {
        val eventName = event.name

        firestore.collection("events").whereEqualTo("name", eventName).limit(1).get()
            .addOnSuccessListener { querySnapshot ->
                val eventRef = querySnapshot.documents[0].reference
                eventRef.update(
                    "attendees",
                    FieldValue.arrayRemove(userId) //FieldValue.arrayRemove() same as Union but for removing
                ).addOnSuccessListener {
                    Log.w(TAG, "Successfully removed attendee")
                }.addOnFailureListener { exception ->
                    Log.w(TAG, "Failed removing attendee", exception)
                }
            }.addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }
    }

    fun deleteEvent(event: Event): Boolean {
        var deleted = false
        val eventName = event.name

        firestore.collection("events").whereEqualTo("name", eventName).get()
            .addOnSuccessListener { querySnapshot ->
                val eventRef = querySnapshot.documents[0].reference
                eventRef.delete().addOnSuccessListener {
                    deleted = true
                    Log.w(TAG, "Event successfully deleted")
                }.addOnFailureListener { exception ->
                    Log.w(TAG, "Error deleting event", exception)
                }
            }
        return deleted
    }

    fun searchUsers(query: String) {
        val dbQuery = Firebase.database.getReference("users").orderByChild("username")
            .startAt(query.lowercase())
            .endAt(query.lowercase() + "\uf8ff")

        if (query.isEmpty()) {
            _userSearchResult.value = emptyList()
            return
        }

        dbQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val userList = mutableListOf<User>()
                for (userSnapshot in dataSnapshot.children) {
                    val user = userSnapshot.getValue(User::class.java)
                    user?.let { userList.add(it) }
                }
                _userSearchResult.value = userList
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(TAG, "Failed to search users: $databaseError")
                _userSearchResult.value = listOf()
            }
        })

        val dbQueryByName =
            Firebase.database.getReference("users").orderByChild("realName")
                .startAt(query.lowercase()).endAt(query + "\uf8ff")
        dbQueryByName.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val userList = mutableListOf<User>()
                for (userSnapshot in dataSnapshot.children) {
                    val user = userSnapshot.getValue(User::class.java)
                    user?.let { userList.add(it) }
                }
                _userSearchResult.value =
                    _userSearchResult.value.orEmpty().plus(userList).distinct()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(TAG, "Failed to search users by name: $databaseError")
            }
        })
    }

    fun sendFriendRequest(friendId: String) {
        val ref = database.child("friends").child("requests").child(friendId)
        ref.push().setValue(Firebase.auth.currentUser!!.uid).addOnSuccessListener {
            Log.w(TAG, "Friend request sent")
        }.addOnFailureListener { exception ->
            Log.w(TAG, "Failed sending friend request", exception)
        }
    }

    fun removeFriendRequest(friendId: String) {
        val ref = database.child("friends").child("requests").child(Firebase.auth.currentUser!!.uid)

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (requestSnapshot in dataSnapshot.children) {
                    val request = requestSnapshot.getValue(String::class.java)
                    if (request == friendId) {
                        requestSnapshot.ref.removeValue()
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(TAG, "Failed to remove friend request: $databaseError")
            }
        })
    }

    fun acceptFriendRequest(friendId: String) {
        val requestsRef =
            database.child("friends").child("requests").child(Firebase.auth.currentUser!!.uid)
        var ref = database.child("friends").child("list").child(Firebase.auth.currentUser!!.uid)
        ref.push().setValue(friendId).addOnSuccessListener {
            Log.w(TAG, "Added to my friend list")
        }.addOnFailureListener { exception ->
            Log.w(TAG, "Failed accepting friend request", exception)
        }

        ref = database.child("friends").child("list").child(friendId)
        ref.push().setValue(Firebase.auth.currentUser!!.uid).addOnSuccessListener {
            Log.w(TAG, "Added to friend's friend list")
        }.addOnFailureListener { exception ->
            Log.w(TAG, "Failed accepting friend request", exception)
        }

        requestsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (requestSnapshot in dataSnapshot.children) {
                    val request = requestSnapshot.getValue(String::class.java)
                    if (request == friendId) {
                        requestSnapshot.ref.removeValue()
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(TAG, "Failed to remove friend request: $databaseError")
            }
        })
    }

    fun getFriendList(callback: (List<String>) -> Unit) {
        val ref = database.child("friends").child("list").child(Firebase.auth.currentUser!!.uid)

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val friends = mutableListOf<String>()
                for (childSnapshot in snapshot.children) {
                    val friendId = childSnapshot.value
                    friendId.let { friends.add(it.toString()) }
                }
                callback(friends)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Failed to get friends list: $error")
                callback(emptyList())
            }
        })
    }

    fun getFriendRequests(userId: String, callback: (List<String>) -> Unit) {
        val ref = database.child("friends").child("requests").child(userId)

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val friendRequests = mutableListOf<String>()
                for (childSnapshot in snapshot.children) {
                    val friendId = childSnapshot.value
                    friendId.let { friendRequests.add(it.toString()) }
                }
                callback(friendRequests)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Failed to get friend requests: $error")
                callback(emptyList())
            }
        })
    }

    fun removeFriend(friendId: String) {
        val ref = database.child("friends").child("list").child(Firebase.auth.currentUser!!.uid)

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (friendSnapshot in dataSnapshot.children) {
                    val friend = friendSnapshot.getValue(String::class.java)
                    if (friend == friendId) {
                        friendSnapshot.ref.removeValue()
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(TAG, "Failed to remove friend: $databaseError")
            }
        })

        val ref2 = database.child("friends").child("list").child(friendId)

        ref2.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (friendSnapshot in dataSnapshot.children) {
                    val friend = friendSnapshot.getValue(String::class.java)
                    if (friend == Firebase.auth.currentUser!!.uid) {
                        friendSnapshot.ref.removeValue()
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(TAG, "Failed to remove friend: $databaseError")
            }
        })
    }

    fun favouriteEvent(eventName: String) {
        val ref = database.child("users").child(Firebase.auth.currentUser!!.uid).child("favourites")

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val fav = mutableListOf<String>()
                val favourites = dataSnapshot.getValue(object : GenericTypeIndicator<List<String>>() {})
                favourites?.let { fav.addAll(it) }

                fav.add(eventName)

                ref.setValue(fav).addOnSuccessListener {
                    Log.w(TAG, "Added event to favourites")
                }.addOnFailureListener { exception ->
                    Log.w(TAG, "Failed adding event to favourites", exception)
                }

            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(TAG, "Failed to add event to favourites: $databaseError")
            }
        })
    }

    fun unfavouriteEvent(eventName: String) {
        val ref = database.child("users").child(Firebase.auth.currentUser!!.uid).child("favourites")

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val fav = mutableListOf<String>()
                val favourites = dataSnapshot.getValue(object : GenericTypeIndicator<List<String>>() {})
                favourites?.let { fav.addAll(it) }

                fav.remove(eventName)

                ref.setValue(fav).addOnSuccessListener {
                    Log.w(TAG, "Removed event from favourites")
                }.addOnFailureListener { exception ->
                    Log.w(TAG, "Failed to remove event from favourites", exception)
                }

            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(TAG, "Failed to remove event from favourites: $databaseError")
            }
        })
    }

    fun getFavouriteEvents(): LiveData<List<String>?> {
        val events = MutableLiveData<List<String>?>()
        val ref = database.child("users").child(Firebase.auth.currentUser!!.uid).child("favourites")

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val fav = mutableListOf<String>()
                val favourites = dataSnapshot.getValue(object : GenericTypeIndicator<List<String>>() {})
                events.value = favourites
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(TAG, "Failed to remove event from favourites: $databaseError")
            }
        })
        return events
    }

}