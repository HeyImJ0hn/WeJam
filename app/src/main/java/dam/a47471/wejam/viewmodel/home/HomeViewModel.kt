package dam.a47471.wejam.viewmodel.home

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import dam.a47471.wejam.model.Event
import dam.a47471.wejam.model.Repository
import dam.a47471.wejam.model.User

class HomeViewModel : ViewModel() {
    private val repository: Repository = Repository()

    private val _user = MutableLiveData<User>()
    val user: LiveData<User>
        get() = _user

    fun getEventsByAttendee(userId: String): LiveData<List<Event>> {
        return repository.getEventsByAttendee(userId)
    }

    fun loadUser(userId: String) {
        repository.loadUser(userId) { user ->
            _user.value = user
        }
    }

    fun getEventByName(name: String): LiveData<Event?> {
        return repository.getEventByName(name)
    }

    fun getFriends(): LiveData<List<String>> {
        return repository.getFriendList()
    }

    fun getFavouriteEvents(): LiveData<List<String>?> {
        return repository.getFavouriteEvents()
    }
}