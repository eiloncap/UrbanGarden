package il.co.urbangarden

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import il.co.urbangarden.databinding.ActivityMainBinding
import il.co.urbangarden.ui.MainViewModel
import il.co.urbangarden.ui.login.LoginActivity


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val toolbar = binding.topAppBar
//        setSupportActionBar(toolbar)

//        val color = ResourcesCompat.getColor(resources, R.color.white, null)
//        toolbar.navigationIcon?.colorFilter = Color.BLACK
//        toolbar.navigationIcon?.setColorFilter(color)
        actionBar?.setDisplayShowHomeEnabled(false)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.sign_out -> {
                    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build()

                    val googleSignInClient = GoogleSignIn.getClient(this, gso)
                    Firebase.auth.signOut()
                    googleSignInClient.signOut().addOnCompleteListener(this)
                    {
                        startActivity(Intent(this, LoginActivity::class.java))
                        finish()
                    }

                    true
                }
                else -> false
            }
        }

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
//        toolbar.setupWithNavController(navController)

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_camera, R.id.navigation_forum
            )
        )
        val viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        viewModel.loadDb()
        binding.topBar
//        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }
}