package daily.dayo.presentation.fragment.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import daily.dayo.presentation.R
import daily.dayo.presentation.adapter.HomeFragmentPagerStateAdapter
import daily.dayo.presentation.common.autoCleared
import daily.dayo.presentation.common.extension.navigateSafe
import daily.dayo.presentation.common.setOnDebounceClickListener
import daily.dayo.presentation.databinding.FragmentHomeBinding

const val HOME_DAYOPICK_PAGE_TAB_ID = 0
const val HOME_NEW_PAGE_TAB_ID = 1

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var binding by autoCleared<FragmentHomeBinding>(onDestroy = {
        onDestroyBindingView()
    })
    private var mediator: TabLayoutMediator? = null
    private var pagerAdapter: HomeFragmentPagerStateAdapter? = null
    private val pageChangeCallBack = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            pagerAdapter?.refreshFragment(position, pagerAdapter!!.fragments[position])
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewPager()
        setSearchClickListener()
    }

    private fun onDestroyBindingView() {
        mediator?.detach()
        mediator = null
        pagerAdapter = null
        with(binding.pagerHomePost) {
            unregisterOnPageChangeCallback(pageChangeCallBack)
            adapter = null
        }
    }

    private fun setSearchClickListener() {
        binding.btnPostSearch.setOnDebounceClickListener {
            findNavController().navigateSafe(
                currentDestinationId = R.id.HomeFragment,
                action = R.id.action_homeFragment_to_searchFragment
            )
        }
    }

    private fun initViewPager() {
        pagerAdapter =
            HomeFragmentPagerStateAdapter(childFragmentManager, viewLifecycleOwner.lifecycle)
        pagerAdapter?.addFragment(HomeDayoPickPostListFragment())
        pagerAdapter?.addFragment(HomeNewPostListFragment())
        for (i in 0 until pagerAdapter!!.itemCount) {
            pagerAdapter?.refreshFragment(i, pagerAdapter!!.fragments[i])
        }

        with(binding.pagerHomePost) {
            isUserInputEnabled = false // DISABLE SWIPE
            adapter = pagerAdapter
            registerOnPageChangeCallback(pageChangeCallBack)
        }

        initTabLayout()
    }

    private fun initTabLayout() {
        mediator = TabLayoutMediator(
            binding.tabsActionbarHomeCategory,
            binding.pagerHomePost
        ) { tab, position ->
            when (position) {
                HOME_DAYOPICK_PAGE_TAB_ID -> tab.text = "DAYO PICK"
                HOME_NEW_PAGE_TAB_ID -> tab.text = "NEW"
            }
        }
        mediator?.attach()
    }
}