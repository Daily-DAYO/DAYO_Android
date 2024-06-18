package daily.dayo.presentation.fragment.mypage.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.google.android.material.tabs.TabLayoutMediator
import daily.dayo.presentation.R
import daily.dayo.presentation.adapter.ProfileFragmentPagerStateAdapter
import daily.dayo.presentation.common.GlideLoadUtil.loadImageViewProfile
import daily.dayo.presentation.common.autoCleared
import daily.dayo.presentation.common.extension.navigateSafe
import daily.dayo.presentation.common.setOnDebounceClickListener
import daily.dayo.presentation.databinding.FragmentMyPageBinding
import daily.dayo.presentation.viewmodel.AccountViewModel
import daily.dayo.presentation.viewmodel.ProfileViewModel

const val FOLDERS_TAB_ID = 0
const val LIKES_TAB_ID = 1
const val BOOKMARKS_TAB_ID = 2

class MyPageFragment : Fragment() {
    private var binding by autoCleared<FragmentMyPageBinding> { onDestroyBindingView() }
    private val accountViewModel by activityViewModels<AccountViewModel>()
    private val profileViewModel by activityViewModels<ProfileViewModel>()
    private var glideRequestManager: RequestManager? = null
    private var mediator: TabLayoutMediator? = null
    private var pagerAdapter: ProfileFragmentPagerStateAdapter? = null
    private val pageChangeCallBack = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            pagerAdapter?.let {
                it.refreshFragment(position, it.fragments[position])
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyPageBinding.inflate(inflater, container, false)
        glideRequestManager = Glide.with(this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setScrollPosition()
        setProfileDescription()
        setViewPager()
        setViewPagerChangeEvent()
        setMyProfileOptionClickListener()
    }

    private fun onDestroyBindingView() {
        mediator?.detach()
        mediator = null
        glideRequestManager = null
        pagerAdapter = null
        with(binding.pagerMyPage) {
            unregisterOnPageChangeCallback(pageChangeCallBack)
            adapter = null
        }
        profileViewModel.cleanUpFolders()
    }

    private fun setScrollPosition() {
        with(binding) {
            ViewCompat.requestApplyInsets(layoutMyPage)
            ViewCompat.requestApplyInsets(layoutMyPageAppBar)
        }
    }

    fun resetAppBarScrollPosition() {
        with(binding) {
            layoutMyPageAppBar.setExpanded(true, true)
        }
    }

    private fun setProfileDescription() {
        profileViewModel.requestMyProfile()
        profileViewModel.profileInfo.observe(viewLifecycleOwner) {
            it?.let { profile ->
                binding.profile = profile.data
                glideRequestManager?.let { requestManager ->
                    profile.data?.profileImg?.let { profileImg ->
                        loadImageViewProfile(
                            requestManager = requestManager,
                            width = binding.imgMyPageUserProfile.width,
                            height = binding.imgMyPageUserProfile.height,
                            imgName = profileImg,
                            imgView = binding.imgMyPageUserProfile
                        )
                    }
                }

                profile.data?.let { profile ->
                    profile.memberId?.let { memberId ->
                        setFollowerCountButtonClickListener(
                            memberId = memberId,
                            nickname = profile.nickname
                        )
                        setFollowingCountButtonClickListener(
                            memberId = memberId,
                            nickname = profile.nickname
                        )
                    }
                }
            }
        }
    }

    private fun setViewPagerChangeEvent() {
        binding.pagerMyPage.registerOnPageChangeCallback(pageChangeCallBack)
    }

    private fun setViewPager() {
        pagerAdapter =
            ProfileFragmentPagerStateAdapter(childFragmentManager, viewLifecycleOwner.lifecycle)
        pagerAdapter?.addFragment(ProfileFolderListFragment())
        pagerAdapter?.addFragment(ProfileLikePostListFragment())
        pagerAdapter?.addFragment(ProfileBookmarkPostListFragment())
        binding.pagerMyPage.adapter = pagerAdapter

        mediator = TabLayoutMediator(binding.tabsMyPage, binding.pagerMyPage) { tab, position ->
            when (position) {
                FOLDERS_TAB_ID -> tab.text = "작성한 글"
                LIKES_TAB_ID -> tab.text = "좋아요"
                BOOKMARKS_TAB_ID -> tab.text = "북마크"
            }
        }
        mediator?.attach()
    }

    private fun setMyProfileOptionClickListener() {
        binding.btnMyPageOption.setOnDebounceClickListener {
            findNavController().navigateSafe(
                currentDestinationId = R.id.MyPageFragment,
                action = R.id.action_myPageFragment_to_profileOptionFragment,
                args = MyPageFragmentDirections.actionMyPageFragmentToProfileOptionFragment(
                    isMine = true,
                    memberId = accountViewModel.getCurrentUserInfo().memberId!!
                ).arguments
            )
        }
    }

    private fun setFollowerCountButtonClickListener(memberId: String, nickname: String) {
        binding.layoutMyPageFollowerCount.setOnDebounceClickListener { v ->
            Navigation.findNavController(v).navigateSafe(
                currentDestinationId = R.id.MyPageFragment,
                action = R.id.action_myPageFragment_to_followFragment,
                args = MyPageFragmentDirections.actionMyPageFragmentToFollowFragment(
                    memberId = memberId,
                    nickname = nickname,
                    initPosition = 0
                ).arguments
            )
        }
    }

    private fun setFollowingCountButtonClickListener(memberId: String, nickname: String) {
        binding.layoutMyPageFollowingCount.setOnDebounceClickListener { v ->
            Navigation.findNavController(v).navigateSafe(
                currentDestinationId = R.id.MyPageFragment,
                action = R.id.action_myPageFragment_to_followFragment,
                args = MyPageFragmentDirections.actionMyPageFragmentToFollowFragment(
                    memberId = memberId,
                    nickname = nickname,
                    initPosition = 1
                ).arguments
            )
        }
    }
}