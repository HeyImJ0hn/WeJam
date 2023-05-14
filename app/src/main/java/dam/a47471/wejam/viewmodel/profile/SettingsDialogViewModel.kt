package dam.a47471.wejam.viewmodel.profile

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class SettingsDialogViewModel : ViewModel() {

    fun logout() {
        val auth = FirebaseAuth.getInstance()
        auth.signOut()
    }

}