package dam.a47471.wejam.view.home.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import dam.a47471.wejam.view.home.tabs.FriendsFragment
import dam.a47471.wejam.view.home.tabs.SavedFragment
import dam.a47471.wejam.view.home.tabs.AttendingFragment

class HomePagerAdapter(fm: FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter(fm, lifecycle) {

    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> AttendingFragment()
            1 -> FriendsFragment()
            else -> SavedFragment()
        }
    }
}