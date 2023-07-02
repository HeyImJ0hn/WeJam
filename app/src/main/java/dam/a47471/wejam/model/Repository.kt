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
import dam.a47471.wejam.utils.Utils
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

    fun writeNewUser(userId: String, username: String, realName: String, email: String, bio: String, pPicUri: String, bannerUri: String) {
        val user = User(userId, username.lowercase(), realName, email, bio, pPicUri, bannerUri, 0.0, 0.0)
        database.child("users").child(userId).setValue(user)
        firestore.collection("friends").document(userId).set(hashMapOf("friends" to listOf<String>()))
        firestore.collection("requests").document(userId).set(hashMapOf("friends" to listOf<String>()))
        firestore.collection("favourites").document(userId).set(hashMapOf("events" to listOf<String>()))
    }

    fun updateUser(userId: String, username: String, realName: String, email: String, bio: String) {

        val users = database.child("users")
        val userReference = users.child(userId)

        userReference.child("username").setValue(username.lowercase())
        userReference.child("realName").setValue(realName)
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

    fun getGoogleProfilePicture(userId: String): Task<DataSnapshot> {
        return database.child("users").child(userId).child("profilePicture").get()
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

        firestore.collection("events").get().addOnSuccessListener {
            val events = mutableListOf<Event>()
            for (document in it.documents) {
                val owner = document.getString("owner") ?: continue
                val name = document.getString("name") ?: continue
                val type = document.getString("type") ?: continue
                val locationName = document.getString("locationName") ?: continue
                val lat = document.getDouble("lat") ?: continue
                val long = document.getDouble("long") ?: continue
                val time = document.getString("time") ?: continue
                val date = document.getString("date") ?: continue
                val attendees = document.get("attendees") ?: continue

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
        }.addOnFailureListener { e ->
            Log.w(TAG, "Error getting events: ", e)
        }

        return eventsLiveData
    }

    fun getEventByName(eventName: String): LiveData<Event?> {
        val eventLiveData = MutableLiveData<Event?>()

        firestore.collection("events").whereEqualTo("name", eventName).limit(1).get()
            .addOnSuccessListener { querySnapshot: QuerySnapshot ->
                if (querySnapshot.documents.isEmpty()) {
                    eventLiveData.value = null
                    return@addOnSuccessListener
                }

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

        firestore.collection("events").whereArrayContains("attendees", attendee).addSnapshotListener { value, error ->
            if (error != null) {
                Log.w(TAG, "Error getting documents: ", error)
                return@addSnapshotListener
            }

            val events = mutableListOf<Event>()
            for (document in value!!.documents) {
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

    fun deleteEvent(event: Event) {
        val eventName = event.name

        firestore.collection("events").whereEqualTo("name", eventName).get()
            .addOnSuccessListener { querySnapshot ->
                val eventRef = querySnapshot.documents[0].reference
                eventRef.delete().addOnSuccessListener {
                    Log.w(TAG, "Event successfully deleted")
                }.addOnFailureListener { exception ->
                    Log.w(TAG, "Error deleting event", exception)
                }
            }
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
        firestore.collection("requests").document(friendId).update(
            "friends",
            FieldValue.arrayUnion(Firebase.auth.currentUser!!.uid)
        ).addOnSuccessListener {
            Log.w(TAG, "Friend request sent")
        }.addOnFailureListener { exception ->
            Log.w(TAG, "Failed sending friend request", exception)
        }
    }

    fun removeFriendRequest(userId: String, friendId: String) {
        firestore.collection("requests").document(friendId).update(
            "friends",
            FieldValue.arrayRemove(userId)
        ).addOnSuccessListener {
            Log.w(TAG, "Friend request removed")
        }.addOnFailureListener { exception ->
            Log.w(TAG, "Failed removing friend request", exception)
        }
    }

    private fun addFriend(friendId1: String, friendId2: String) {
        firestore.collection("friends").document(friendId1).update(
            "friends",
            FieldValue.arrayUnion(friendId2)
        ).addOnSuccessListener {
            Log.w(TAG, "Added to my friend list")
        }.addOnFailureListener { exception ->
            Log.w(TAG, "Failed accepting friend request", exception)
        }
    }

    fun acceptFriendRequest(friendId: String) {
        addFriend(Firebase.auth.currentUser!!.uid, friendId)
        addFriend(friendId, Firebase.auth.currentUser!!.uid)
        removeFriendRequest(friendId, Firebase.auth.currentUser!!.uid)
    }

    fun getFriendList(): LiveData<List<String>> {
        val friends = MutableLiveData<List<String>>()
        firestore.collection("friends").document(Firebase.auth.currentUser!!.uid)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.e(TAG, "Failed to get friends list: $error")
                    return@addSnapshotListener
                }
                friends.value = value?.get("friends") as List<String>
            }
        return friends
    }

    fun getFriendRequests(userId: String): LiveData<List<String>> {
        val friendRequests = MutableLiveData<List<String>>()

        firestore.collection("requests").document(userId)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.e(TAG, "Failed to get friend requests: $error")
                    return@addSnapshotListener
                }
                friendRequests.value = value?.get("friends") as List<String>
            }
        return friendRequests
    }

    private fun removeFriend(friendId1: String, friendId2: String) {
        firestore.collection("friends").document(friendId1).update(
            "friends",
            FieldValue.arrayRemove(friendId2)
        ).addOnSuccessListener {
            Log.w(TAG, "Removed from friend list")
        }.addOnFailureListener { exception ->
            Log.w(TAG, "Failed removing friend", exception)
        }
    }

    fun removeFriend(friendId: String) {
        removeFriend(Firebase.auth.currentUser!!.uid, friendId)
        removeFriend(friendId, Firebase.auth.currentUser!!.uid)
    }

    fun favouriteEvent(eventName: String) {

        firestore.collection("favourites").document(Firebase.auth.currentUser!!.uid)
            .update("events", FieldValue.arrayUnion(eventName))
            .addOnSuccessListener {
                Log.w(TAG, "Added event to favourites")
            }.addOnFailureListener { exception ->
                Log.w(TAG, "Failed adding event to favourites", exception)
            }
    }

    fun unfavouriteEvent(eventName: String) {

        firestore.collection("favourites").document(Firebase.auth.currentUser!!.uid)
            .update("events", FieldValue.arrayRemove(eventName))
            .addOnSuccessListener {
                Log.w(TAG, "Removed event from favourites")
            }.addOnFailureListener { exception ->
                Log.w(TAG, "Failed to remove event from favourites", exception)
            }
    }

    fun getFavouriteEvents(): LiveData<List<String>?> {
        val events = MutableLiveData<List<String>?>()

        firestore.collection("favourites").document(Firebase.auth.uid!!)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.e(TAG, "Failed to get favourite events: $error")
                    events.value = null
                    return@addSnapshotListener
                }

                events.value = value?.get("events") as List<String>?
            }
        return events
    }

    fun getChat(userId: String, otherUserId: String): LiveData<Chat> {
        val chatId = Utils.getChatId(userId, otherUserId)
        val chatLiveData = MutableLiveData<Chat>()

        val chatRef = firestore.collection("chats").document(chatId)

        chatRef.addSnapshotListener { value, error ->
            if (error != null) {
                Log.e(TAG, "Failed to get chat: $error")
                return@addSnapshotListener
            }
            if (value != null && value.exists()) {
                val chat = value.toObject(Chat::class.java)
                chatLiveData.value = chat!!
            } else {
                val newChat =
                    Chat(chatId, mutableListOf(userId, otherUserId), mutableListOf(), "")
                chatRef.set(newChat)
                    .addOnSuccessListener {
                        chatLiveData.value = newChat
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "Failed to create chat: $e")
                    }
            }
        }
        return chatLiveData
    }

    fun sendMessage(userId: String, otherUserId: String, message: Message) {
        val chatId = Utils.getChatId(userId, otherUserId)
        val chatRef = firestore.collection("chats").document(chatId)
        chatRef.update("messages", FieldValue.arrayUnion(message))
            .addOnSuccessListener {
                Log.w(TAG, "Message sent")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to send message: $e")
            }
    }

    fun getUserChats(userId: String): LiveData<List<Chat>> {
        val chatsLiveData = MutableLiveData<List<Chat>>()
        firestore.collection("chats").whereArrayContains("members", userId)
            .addSnapshotListener { value, error ->
                if (value != null) {
                    val chats = mutableListOf<Chat>()
                    for (document in value.documents) {
                        val chat = document.toObject(Chat::class.java)
                        if (chat != null) {
                            chats.add(chat)
                            chatsLiveData.value = chats
                        }
                    }
                    Log.w(TAG, "Chats: $chats")
                } else {
                    Log.e(TAG, "Failed to get chats: $error")
                }
            }
        return chatsLiveData
    }

}