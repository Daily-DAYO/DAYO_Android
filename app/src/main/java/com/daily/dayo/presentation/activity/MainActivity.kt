package com.daily.dayo.presentation.activity

import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.forEach
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.daily.dayo.R
import com.daily.dayo.databinding.ActivityMainBinding
import com.daily.dayo.presentation.fragment.home.HomeFragmentDirections
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initBottomNavigation()
        setBottomNaviVisibility()
        disableBottomNaviTooltip()
        getNotificationData()
    }

    private fun getNotificationData(){
        val extraFragment = intent.getStringExtra("ExtraFragment")
        if(extraFragment!=null && extraFragment == "Notification"){
            val postId = intent.getStringExtra("PostId")?.toInt()
            val memberId = intent.getStringExtra("MemberId")
            if(postId!=null) findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToPostFragment(postId = postId))
            else if(memberId!=null) findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToProfileFragment(memberId = memberId))
            else findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToNotificationFragment())
        }
    }

    private fun initBottomNavigation() {
        binding.bottomNavigationMainBar.setupWithNavController(findNavController())
    }

    private fun findNavController(): NavController {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        return navHostFragment.navController
    }

    private fun setBottomNaviVisibility() {
        binding.bottomNavigationMainBar.itemIconTintList = null
        findNavController().addOnDestinationChangedListener { _, destination, _ ->
            binding.layoutBottomNavigationMain.visibility = when (destination.id) {
                R.id.HomeFragment -> View.VISIBLE
                R.id.FeedFragment -> View.VISIBLE
                R.id.NotificationFragment -> View.VISIBLE
                R.id.ProfileFragment -> View.VISIBLE
                else -> View.GONE
            }
        }
        // WriteFragment
        binding.bottomNavigationMainBar.setItemOnTouchListener( R.id.WriteFragment,
            object : View.OnTouchListener {
                var rect = Rect()
                var isInside = true
                override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                    when (event?.action) {
                        MotionEvent.ACTION_DOWN -> {
                            binding.bottomNavigationMainBar.menu.findItem(R.id.WriteFragment)
                                .setIcon(R.drawable.ic_write_filled)
                            rect = Rect(v!!.left, v.top, v.right, v.bottom)
                            isInside = true
                            return true
                        }
                        MotionEvent.ACTION_MOVE -> {
                            isInside = rect.contains(v!!.left + event.x.toInt(), v.top + event.y.toInt())
                            binding.bottomNavigationMainBar.clearFocus()
                            return false
                        }
                        MotionEvent.ACTION_UP -> {
                            binding.bottomNavigationMainBar.menu.findItem(R.id.WriteFragment)
                                .setIcon(R.drawable.ic_write)
                            if (isInside) {
                                when (findNavController().currentDestination!!.id) {
                                    R.id.HomeFragment -> findNavController().navigate(R.id.action_homeFragment_to_writeFragment)
                                    R.id.FeedFragment -> findNavController().navigate(R.id.action_feedFragment_to_writeFragment)
                                    R.id.NotificationFragment -> findNavController().navigate(R.id.action_notificationFragment_to_writeFragment)
                                    R.id.ProfileFragment -> findNavController().navigate(R.id.action_profileFragment_to_writeFragment)
                                }
                            }
                            return true
                        }
                        else -> return true
                    }
                }
            })
    }

    private fun disableBottomNaviTooltip() {
        binding.bottomNavigationMainBar.menu.forEach {
            val view = binding.bottomNavigationMainBar.findViewById<View>(it.itemId)
            view.setOnLongClickListener {
                true
            }
        }
    }
}