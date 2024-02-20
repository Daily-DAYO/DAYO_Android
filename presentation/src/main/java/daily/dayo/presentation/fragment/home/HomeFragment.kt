package daily.dayo.presentation.fragment.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import daily.dayo.presentation.R
import daily.dayo.presentation.adapter.HomeFragmentPagerStateAdapter
import daily.dayo.presentation.common.autoCleared
import daily.dayo.presentation.common.setOnDebounceClickListener
import daily.dayo.presentation.databinding.FragmentHomeBinding
import daily.dayo.presentation.theme.Gray1_313131
import daily.dayo.presentation.theme.Gray5_E8EAEE
import daily.dayo.presentation.view.TextButton
import daily.dayo.presentation.view.TopNavigation

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
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MaterialTheme {
                    HomeScreen()
                }
            }
        }
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

    @Composable
    private fun HomeScreen() {
        var homeTabState by rememberSaveable { mutableIntStateOf(HOME_DAYOPICK_PAGE_TAB_ID) }
        Scaffold(
            topBar = {
                TopNavigation(
                    leftIcon = {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.padding(start = 18.dp)
                        ) {
                            TextButton(
                                onClick = {
                                    homeTabState = HOME_DAYOPICK_PAGE_TAB_ID
                                },
                                text = stringResource(id = R.string.DayoPick),
                                textStyle = MaterialTheme.typography.titleLarge.copy(
                                    color = if (homeTabState == HOME_DAYOPICK_PAGE_TAB_ID) Gray1_313131 else Gray5_E8EAEE,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            )

                            TextButton(
                                onClick = {
                                    homeTabState = HOME_NEW_PAGE_TAB_ID
                                },
                                text = stringResource(id = R.string.New),
                                textStyle = MaterialTheme.typography.titleLarge.copy(
                                    color = if (homeTabState == HOME_NEW_PAGE_TAB_ID) Gray1_313131 else Gray5_E8EAEE,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            )
                        }
                    },
                    rightIcon = {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.ic_search),
                            contentDescription = "search",
                            tint = Gray1_313131,
                            modifier = Modifier
                                .padding(end = 12.dp)
                                .size(24.dp)
                        )
                    }
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier.padding(innerPadding)
            ) {

            }
        }
    }

    private fun setSearchClickListener() {
        binding.btnPostSearch.setOnDebounceClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
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

    @Composable
    @Preview(showBackground = true)
    private fun PreviewHomeDayoPickScreen() {
        MaterialTheme {
            HomeScreen()
        }
    }
}