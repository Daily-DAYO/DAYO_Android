package com.daily.dayo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.daily.dayo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initBottomNavigation()
        setBottomNaviVisibility()
    }

    private fun initBottomNavigation() {
        binding.bottomNavigationMainBar.setupWithNavController(findNavController())
    }
    private fun findNavController(): NavController {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        return navHostFragment.navController
    }
    private fun setBottomNaviVisibility() {
        binding.btnBottomNavigationWrite.setOnClickListener {
            when(findNavController().currentDestination!!.id){
                R.id.HomeFragment -> findNavController().navigate(R.id.action_homeFragment_to_writeFragment)
                R.id.FeedFragment -> findNavController().navigate(R.id.action_feedFragment_to_writeFragment)
                // R.id.NotificationFragment -> findNavController().navigate(R.id.action_notificationFragment_to_writeFragment)
                R.id.MyProfileFragment -> findNavController().navigate(R.id.action_myProfileFragment_to_writeFragment)
            }
        }

        findNavController().addOnDestinationChangedListener { _, destination, _ ->
            binding.layoutBottomNavigationMain.visibility = when (destination.id) {
                R.id.HomeFragment -> View.VISIBLE
                R.id.FeedFragment -> View.VISIBLE
                R.id.MyProfileFragment -> View.VISIBLE
                else -> View.GONE
            }
            binding.btnBottomNavigationWrite.visibility = when (destination.id) {
                R.id.HomeFragment -> View.VISIBLE
                R.id.FeedFragment -> View.VISIBLE
                R.id.MyProfileFragment -> View.VISIBLE
                else -> View.GONE
            }
        }
    }
}