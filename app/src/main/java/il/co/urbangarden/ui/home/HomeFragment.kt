package il.co.urbangarden.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import il.co.urbangarden.R
import il.co.urbangarden.databinding.FragmentHomeBinding
import il.co.urbangarden.ui.home.ui.main.SectionsPagerAdapter

class HomeFragment : Fragment() {

    private val homeViewModel: HomeViewModel by lazy {
        ViewModelProvider(requireActivity()).get(HomeViewModel::class.java)
    }
    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    // When requested, this adapter returns a DemoObjectFragment,
    // representing an object in the collection.
    private lateinit var sectionsPagerAdapter: SectionsPagerAdapter
    private lateinit var viewPager: ViewPager

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        sectionsPagerAdapter = SectionsPagerAdapter(childFragmentManager)
        viewPager = view.findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        val tabLayout: TabLayout = view.findViewById(R.id.tabs)
        tabLayout.setupWithViewPager(viewPager)
        val tab: TabLayout.Tab? = tabLayout.getTabAt(homeViewModel.tab)
        tabLayout.selectTab(tab)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}