package com.daily.dayo.presentation.adapter

import android.os.Handler
import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class ProfileFragmentPagerStateAdapter(@NonNull fragmentManager: FragmentManager, @NonNull lifecycle: Lifecycle) : FragmentStateAdapter(fragmentManager, lifecycle) {
    var fragments : ArrayList<Fragment> = ArrayList()

    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }

    fun addFragment(fragment: Fragment){
        fragments.add(fragment)
        notifyItemInserted(fragments.size - 1)
    }

    fun removeFragment(){
        fragments.removeLast()
        notifyItemRemoved(fragments.size)
    }

    fun refreshFragment(index: Int, fragment: Fragment) {
        fragments[index] = fragment
        Handler().post(object: Runnable {
            override fun run() {
                notifyDataSetChanged()
            }
        })
    }
}