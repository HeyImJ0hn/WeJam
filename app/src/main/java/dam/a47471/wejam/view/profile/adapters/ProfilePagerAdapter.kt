package dam.a47471.wejam.view.profile.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import dam.a47471.wejam.view.profile.tabs.AboutFragment
import dam.a47471.wejam.view.profile.tabs.EventsFragment

class ProfilePagerAdapter(fm: FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter(fm, lifecycle) {

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> AboutFragment()
            else -> EventsFragment()
        }
    }
}