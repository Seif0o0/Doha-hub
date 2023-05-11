package code_grow.dohahub.app.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import code_grow.dohahub.app.R
import code_grow.dohahub.app.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

    }

    override fun onBackPressed() {
        val navigationController = findNavController(R.id.nav_host_fragment)
        if (navigationController.currentDestination?.id == R.id.homeFragment) {
            super.onBackPressed()
        } else{
            navigationController.popBackStack()
        }
    }
}