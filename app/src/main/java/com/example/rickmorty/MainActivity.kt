package com.example.rickmorty

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuItemCompat
import androidx.core.view.MenuItemCompat.getActionView
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.rickmorty.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        navController = findNavController(R.id.nav_host_fragment)
        val appBarConfig = AppBarConfiguration(setOf(R.id.characterFragment, R.id.locationFragment, R.id.episodeFragment))
        setupActionBarWithNavController(navController, appBarConfig)

        binding.bottomNavMenu.setupWithNavController(navController)
    }

//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.action_bar_menu, menu)
//        val menuItem = menu?.findItem(R.id.search)
//        val searchView = menuItem?.actionView as SearchView
//        searchView.setOnSearchClickListener {
//            Toast.makeText(this, "hgdffg", Toast.LENGTH_LONG).show()
//        }
//        return true
//    }

    override fun onSupportNavigateUp(): Boolean {
       return navController.navigateUp()
    }



}