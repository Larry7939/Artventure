package com.artventure.artventure.presentation.screen

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.os.Bundle
import android.view.View
import android.view.animation.AnticipateInterpolator
import androidx.activity.viewModels
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.artventure.artventure.R
import com.artventure.artventure.binding.BindingActivity
import com.artventure.artventure.databinding.ActivityMainBinding

class MainActivity : BindingActivity<ActivityMainBinding>(R.layout.activity_main) {
    private val viewModel: MainViewModel by viewModels()
    private lateinit var splashScreen: SplashScreen
    override fun onCreate(savedInstanceState: Bundle?) {
        splashScreen = installSplashScreen()
        startSplash()
        super.onCreate(savedInstanceState)
        binding.vm = viewModel
        binding.lifecycleOwner = this
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        addNavigationListener(navController)
    }

    private fun startSplash() {
        splashScreen.setOnExitAnimationListener { splashScreenView ->
            val scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1f, 2.5f,1f)
            val scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f, 2.5f,1f)
            ObjectAnimator.ofPropertyValuesHolder(splashScreenView.iconView, scaleX, scaleY).run {
                interpolator = AnticipateInterpolator()
                duration = SPLASH_DURATION
                doOnEnd {
                    splashScreenView.remove()
                }
                start()
            }
        }
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


    companion object{
        const val SPLASH_DURATION =1500L
    }
}