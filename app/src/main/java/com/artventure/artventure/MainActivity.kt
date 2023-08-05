package com.artventure.artventure

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.artventure.artventure.binding.BindingActivity
import com.artventure.artventure.databinding.ActivityMainBinding
import timber.log.Timber

class MainActivity : BindingActivity<ActivityMainBinding>(R.layout.activity_main) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        addNavigationListener(navController)
    }


    private fun addNavigationListener(navController: NavController) {
        var selectedItemId = R.id.home
        binding.bottomNav.setOnItemSelectedListener { menu ->
            if (selectedItemId == menu.itemId) {
                return@setOnItemSelectedListener false
            }
            selectedItemId = menu.itemId
            when (menu.itemId) {
                R.id.home -> {
                    navController.navigate(R.id.action_favoriteFragment_to_homeFragment)
                    true
                }
                R.id.favorite -> {
                    navController.navigate(R.id.action_homeFragment_to_favoriteFragment)
                    true
                }
                else -> {
                    throw IllegalArgumentException("${this@MainActivity::class.java.simpleName} Not found menu item id")
                }
            }
        }
    }
}