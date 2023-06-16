package dam.a47471.wejam.view.profile.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import dam.a47471.wejam.view.profile.tabs.AboutFragment
import dam.a47471.wejam.view.profile.tabs.EventsFragment
import dam.a47471.wejam.view.profile.tabs.MediaFragment

class ProfilePagerAdapter(fm: FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter(fm, lifecycle) {

    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            1 -> MediaFragment()
            2 -> EventsFragment()
            else -> AboutFragment() // 1
        }
    }
}