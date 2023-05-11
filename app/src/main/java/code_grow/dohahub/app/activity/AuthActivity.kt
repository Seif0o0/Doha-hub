package code_grow.dohahub.app.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import code_grow.dohahub.app.R

class AuthActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
    }

    override fun onBackPressed() {
        val navigationController = findNavController(R.id.nav_host_fragment)
        if (navigationController.currentDestination?.id == R.id.loginFragment) {
            super.onBackPressed()
        } else{
            navigationController.popBackStack()
        }
    }
}