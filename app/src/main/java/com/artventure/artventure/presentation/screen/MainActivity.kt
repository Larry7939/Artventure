package com.artventure.artventure.presentation.screen

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Intent
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
import com.artventure.artventure.presentation.MainViewModel
import com.artventure.artventure.presentation.screen.DetailActivity.Companion.DETAIL_INTENT_KEY
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint

class MainActivity : BindingActivity<ActivityMainBinding>(R.layout.activity_main) {
    private val viewModel: MainViewModel by viewModels()
    private lateinit var splashScreen: SplashScreen
    private lateinit var navController: NavController
    private var selectedItemId = R.id.home
    override fun onCreate(savedInstanceState: Bundle?) {
        splashScreen = installSplashScreen()
        startSplash()
        super.onCreate(savedInstanceState)
        binding.vm = viewModel
        binding.lifecycleOwner = this
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        addNavigationListener()
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent != null) {
            val source = intent.getStringExtra("source")
            /**상세페이지에서의 즐겨찾기 추가 작업 -> 즐겨찾기 탭으로 이동*/
            if (source == DETAIL_INTENT_KEY) {
                handleMoveFavoriteFromDetail()
            }
        }
    }

    private fun handleMoveFavoriteFromDetail() {
        selectedItemId = R.id.favorite
        navController.navigate(R.id.action_SearchFragment_to_favoriteFragment)
        binding.bottomNav.menu.findItem(R.id.favorite).isChecked = true
        viewModel.setBottomNavVisibility(View.VISIBLE)
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

    private fun addNavigationListener() {
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