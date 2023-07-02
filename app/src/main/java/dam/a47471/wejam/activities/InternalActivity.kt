package dam.a47471.wejam.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Display
import android.view.WindowManager
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.libraries.places.api.Places
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import dam.a47471.wejam.BuildConfig
import dam.a47471.wejam.utils.LoadingDialog
import dam.a47471.wejam.R
import dam.a47471.wejam.databinding.ActivityInternalBinding
import dam.a47471.wejam.model.Repository
import kotlin.math.roundToInt

class InternalActivity : AppCompatActivity() {
    private lateinit var _binding: ActivityInternalBinding
    val binding: ActivityInternalBinding
        get() = _binding

    private lateinit var navController: NavController
    private lateinit var auth: FirebaseAuth
    private lateinit var authStateListener: FirebaseAuth.AuthStateListener
    private lateinit var storage: FirebaseStorage

    private val _repository: Repository = Repository()
    val repository: Repository
        get() = _repository

    private lateinit var _loadingDialog: LoadingDialog
    val loadingDialog: LoadingDialog
        get() = _loadingDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityInternalBinding.inflate(layoutInflater)
        setContentView(_binding.root)

        auth = Firebase.auth
        storage = Firebase.storage

        // Hide status bar
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.app_nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val currentUser = firebaseAuth.currentUser
            if (currentUser == null) {
                // User is signed out, start new activity
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        _loadingDialog = LoadingDialog(this)

        //_binding.bottomNav.selectedItemId = R.id.bottom_profile
        _binding.bottomNav.itemIconTintList = null
        _binding.bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.bottom_events -> {
                    navController.navigate(R.id.homeFragment)
                    true
                }
                R.id.bottom_explore -> {
                    navController.navigate(R.id.nearbyFragment)
                    true
                }
                R.id.bottom_search_user -> {
                    navController.navigate(R.id.searchUsersFragment)
                    true
                }
                R.id.bottom_inbox -> {
                    navController.navigate(R.id.inboxFragment)
                    true
                }
                R.id.bottom_profile -> {
                    navController.navigate(R.id.profileFragment)
                    true
                }
                else -> false
            }
        }

        Places.initialize(this, BuildConfig.MAPS_API_KEY)
    }


    override fun onStart() {
        super.onStart()
        FirebaseAuth.getInstance().addAuthStateListener(authStateListener)
    }

    override fun onStop() {
        super.onStop()
        FirebaseAuth.getInstance().removeAuthStateListener(authStateListener)
    }
}