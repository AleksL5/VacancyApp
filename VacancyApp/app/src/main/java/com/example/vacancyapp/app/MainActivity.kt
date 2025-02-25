package com.example.vacancyapp.app

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.vacancyapp.core_ui.BadgeDrawable
import com.example.vacancyapp.R
import com.example.vacancyapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), MenuStateUpdater {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Инициализация ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(binding.navHostFragment.id) as NavHostFragment
        val navController = navHostFragment.navController

        // Настройка BottomNavigationView с NavController
        binding.bottomNavigationView.setupWithNavController(navController)

        // Скрываем BottomNavigationView для loginFragment
        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.bottomNavigationView.visibility =
                if (destination.id == R.id.loginFragment || destination.id == R.id.passwordFragment)
                    View.GONE
                else
                    View.VISIBLE
        }
    }

    override fun updateFavoritesMenuState(isFragmentActive: Boolean, favoritesCount: Int) {
        val menuItem = binding.bottomNavigationView.menu.findItem(R.id.favoritesFragment)

        val drawableRes = when {
            isFragmentActive -> R.drawable.ic_favorites_active
            favoritesCount > 0 -> R.drawable.ic_favorites_inactive_with_badge
            else -> R.drawable.ic_favorites_inactive
        }

        val badgeDrawable = if (favoritesCount > 0) {
            BadgeDrawable(this, drawableRes, favoritesCount)
        } else {
            ContextCompat.getDrawable(this, drawableRes)
        }

        menuItem.icon = badgeDrawable
    }
}

interface MenuStateUpdater {
    fun updateFavoritesMenuState(isFragmentActive: Boolean, favoritesCount: Int)
}