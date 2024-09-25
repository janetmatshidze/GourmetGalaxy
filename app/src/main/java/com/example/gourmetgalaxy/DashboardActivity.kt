package com.example.gourmetgalaxy

import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import com.example.gourmetgalaxy.databinding.ActivityDashboardBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.hostmain.toolbar)

        supportActionBar?.title = ""

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navigationView: NavigationView = binding.navView
        val bottomNavigationView: BottomNavigationView =binding.hostmain.bottomNavigation

        // Set up ActionBarDrawerToggle for opening/closing the drawer
        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Ensure drawer is initially closed when the app launches
        drawerLayout.closeDrawers()

        // Enable the action bar's home button for controlling the drawer
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val navController = findNavController(R.id.nav_host_fragment_app_bar)

        // Set up BottomNavigationView

        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> navController.navigate(R.id.nav_home)
                R.id.nav_shoppinglist -> navController.navigate(R.id.nav_shoppinglist)
                R.id.nav_favourites -> navController.navigate(R.id.nav_favourites)
                R.id.nav_notifications -> navController.navigate(R.id.nav_notifications)
                R.id.nav_recipe -> navController.navigate(R.id.nav_recipe)
            }
            true
        }

        // Handle NavigationView item clicks
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> navController.navigate(R.id.nav_home)
                R.id.nav_shoppinglist -> navController.navigate(R.id.nav_shoppinglist)
                R.id.nav_favourites -> navController.navigate(R.id.nav_favourites)
                R.id.nav_settings -> navController.navigate(R.id.nav_settings)
                R.id.nav_recipe -> navController.navigate(R.id.nav_recipe)
                R.id.nav_leaderboard -> navController.navigate(R.id.nav_leaderboard)

            }
            drawerLayout.closeDrawers()  // Close drawer after item selection
            true
        }
    }

    // Override to control the DrawerLayout with the toggle
    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
       // menuInflater.inflate(R.menu.navigation_menu, menu)
        return true
    }
}
