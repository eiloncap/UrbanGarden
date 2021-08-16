package il.co.urbangarden.ui.home.ui.main

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import il.co.urbangarden.ui.location.MyLocations
import il.co.urbangarden.ui.plants.MyPlants
import il.co.urbangarden.R



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
            0-> return MyPlants.newInstance()
            1-> return MyLocations.newInstance()
        }
        return MyLocations.newInstance()
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