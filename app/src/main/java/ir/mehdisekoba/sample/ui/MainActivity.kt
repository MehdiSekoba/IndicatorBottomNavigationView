package ir.mehdisekoba.sample.ui

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import ir.mehdisekoba.sample.R
import ir.mehdisekoba.sample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    //Binding
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    //Other
    private lateinit var navHost: NavHostFragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //Init nav host
        navHost = supportFragmentManager.findFragmentById(R.id.navHost) as NavHostFragment
        //Bottom nav menu
        binding.bottomNav.apply {
            itemRippleColor= ColorStateList.valueOf(Color.TRANSPARENT)
            setupWithNavController(navHost.navController)
        }
        //Gone bottom menu
        navHost.navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.apply {
                when (destination.id) {
                    R.id.homeFragment -> bottomNav.isVisible = true
                    R.id.shopFragment -> bottomNav.isVisible = true
                    R.id.favoriteFragment -> bottomNav.isVisible = true
                    R.id.profileFragment -> bottomNav.isVisible = true
                    else -> bottomNav.isVisible = false
                }
            }
        }
    }

    override fun onNavigateUp(): Boolean {
        return navHost.navController.navigateUp() || super.onNavigateUp()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}