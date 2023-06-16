package dam.a47471.wejam.viewmodel.searchuser

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dam.a47471.wejam.model.Repository
import dam.a47471.wejam.model.User

class SearchUserViewModel : ViewModel() {
    private val repository = Repository()

    private val _searchResults = MutableLiveData<List<User>>()
    val searchResults: LiveData<List<User>>
        get() = _searchResults

    init {
        repository.userSearchResult.observeForever() {
            _searchResults.value = it
        }
    }

    fun searchUsers(query: String) {
        repository.searchUsers(query)
    }
}