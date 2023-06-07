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



        _binding.bottomNav.selectedItemId = R.id.bottom_profile
        _binding.bottomNav.itemIconTintList = null
        _binding.bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.bottom_home -> {
                    navController.navigate(R.id.homeFragment)
                    true
                }
                R.id.bottom_search -> {
                    navController.navigate(R.id.nearbyFragment)
                    true
                }
                R.id.bottom_learn -> {
                    //navController.navigate(R.id.profileFragment)
                    true
                }
                R.id.bottom_inbox -> {
                    //navController.navigate(R.id.profileFragment)
                    true
                }
                R.id.bottom_profile -> {
                    navController.navigate(R.id.profileFragment)
                    true
                }
                else -> false
            }
        }

        /*navController.addOnDestinationChangedListener { _, destination, _ ->
            when(destination.id) {
                R.id.profileFragment -> binding.bottomNav.selectedItemId = R.id.bottom_profile
            }
        }*/

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

    @SuppressLint("UseCompatLoadingForDrawables")
    fun resizeImage(imageResource: Int): Drawable { // R.drawable.large_image
        // Get device dimensions
        val display: Display = windowManager?.defaultDisplay!!
        val deviceWidth = display.width.toDouble()
        val bd = this.resources.getDrawable(
            imageResource
        ) as BitmapDrawable
        val imageHeight = bd.bitmap.height.toDouble()
        val imageWidth = bd.bitmap.width.toDouble()
        val ratio = deviceWidth / imageWidth
        val newImageHeight = (imageHeight * ratio).toInt()
        val bMap = BitmapFactory.decodeResource(resources, imageResource)
        return BitmapDrawable(
            resources,
            getResizedBitmap(bMap, newImageHeight, deviceWidth.toInt())
        )
    }

    private fun getResizedBitmap(bm: Bitmap, newHeight: Int, newWidth: Int): Bitmap? {
        val width = bm.width
        val height = bm.height
        val scaleWidth = newWidth.toFloat() / width
        val scaleHeight = newHeight.toFloat() / height

        // create a matrix for the manipulation
        val matrix = Matrix()

        // resize the bit map
        matrix.postScale(scaleWidth, scaleHeight)

        // recreate the new Bitmap
        return Bitmap.createBitmap(
            bm, 0, 0, width, height,
            matrix, false
        )
    }

    fun resizeImage(uri: Uri, context: Context, targetWidth: Int, targetHeight: Int): Bitmap? {
        try {
            // Load a smaller version of the image into memory
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
                inSampleSize = calculateInSampleSize(this, targetWidth, targetHeight)
                inJustDecodeBounds = false
            }

            // Decode the image with the specified sample size
            val parcelFileDescriptor = context.contentResolver.openFileDescriptor(uri, "r")
            val bitmap = BitmapFactory.decodeFileDescriptor(parcelFileDescriptor?.fileDescriptor, null, options)

            parcelFileDescriptor?.close()
            // Scale the bitmap to the specified width and height
            return Bitmap.createScaledBitmap(bitmap!!, targetWidth, targetHeight, false)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    // Calculate the sample size for scaling the bitmap to the target size
    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val heightRatio = (height.toFloat() / reqHeight.toFloat()).roundToInt()
            val widthRatio = (width.toFloat() / reqWidth.toFloat()).roundToInt()
            inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio
        }

        return inSampleSize
    }



}