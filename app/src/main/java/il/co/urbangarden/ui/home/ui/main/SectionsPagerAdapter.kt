package il.co.urbangarden.ui.home.ui.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import il.co.urbangarden.ui.location.MyLocationsFragment
import il.co.urbangarden.ui.plants.MyPlantsFragment


/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class SectionsPagerAdapter(fm: FragmentManager) :
    FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        when(position){
            0-> return MyPlantsFragment.newInstance()
            1-> return MyLocationsFragment.newInstance()
        }
        return MyLocationsFragment.newInstance()
    }


    override fun getPageTitle(position: Int): CharSequence? {
        when(position){
            0->return "My Plants"
            1->return "My Locations"
        }
        return ""
    }

    override fun getCount(): Int {
        // Show 2 total pages.
        return 2
    }
}