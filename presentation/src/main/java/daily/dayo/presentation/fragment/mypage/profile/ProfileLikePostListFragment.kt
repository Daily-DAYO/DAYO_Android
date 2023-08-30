package daily.dayo.presentation.fragment.mypage.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import daily.dayo.presentation.R
import daily.dayo.presentation.common.autoCleared
import daily.dayo.presentation.databinding.FragmentProfileLikePostListBinding
import daily.dayo.domain.model.LikePost
import daily.dayo.presentation.adapter.ProfileLikePostListAdapter
import daily.dayo.presentation.viewmodel.ProfileViewModel
import daily.dayo.presentation.activity.MainActivity

class ProfileLikePostListFragment : Fragment() {
    private var binding by autoCleared<FragmentProfileLikePostListBinding>{ onDestroyBindingView() }
    private val profileViewModel by activityViewModels<ProfileViewModel>()
    private var profileLikePostListAdapter: ProfileLikePostListAdapter? = null
    private var glideRequestManager: RequestManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null)
            getProfileLikePostList()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileLikePostListBinding.inflate(inflater, container, false)
        glideRequestManager = Glide.with(this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setRvProfileLikePostListAdapter()
        setProfileLikePostList()
        setAdapterLoadStateListener()
    }

    override fun onResume() {
        super.onResume()
        setBottomNavigationIconClickListener()
    }

    private fun onDestroyBindingView() {
        glideRequestManager = null
        profileLikePostListAdapter = null
        binding.rvProfileLikePost.adapter = null
    }

    private fun setRvProfileLikePostListAdapter() {
        profileLikePostListAdapter = glideRequestManager?.let { requestManager ->
            ProfileLikePostListAdapter(
                requestManager = requestManager
            )
        }
        binding.rvProfileLikePost.adapter = profileLikePostListAdapter
        profileLikePostListAdapter?.setOnItemClickListener(object :
            ProfileLikePostListAdapter.OnItemClickListener {
            override fun onItemClick(v: View, likePost: LikePost, pos: Int) {
                when (requireParentFragment()) {
                    is MyPageFragment -> MyPageFragmentDirections.actionMyPageFragmentToPostFragment(likePost.postId)
                    is ProfileFragment -> ProfileFragmentDirections.actionProfileFragmentToPostFragment(likePost.postId)
                    else -> null
                }?.let {
                    findNavController().navigate(it)
                }
            }
        })
    }

    private fun getProfileLikePostList() {
        profileViewModel.requestAllMyLikePostList()
    }

    private fun setProfileLikePostList() {
        profileViewModel.likePostList.observe(viewLifecycleOwner) {
            profileLikePostListAdapter?.submitData(viewLifecycleOwner.lifecycle, it)
        }
    }

    private fun setAdapterLoadStateListener() {
        var isInitialLoad = false
        profileLikePostListAdapter?.let {
            it.addLoadStateListener { loadState ->
                if (loadState.refresh is LoadState.NotLoading && !isInitialLoad) {
                    val isListEmpty = it.itemCount == 0
                    binding.isEmpty = isListEmpty
                    if (isListEmpty || loadState.append is LoadState.NotLoading) {
                        completeLoadPost()
                        isInitialLoad = true
                    }
                }
            }
        }
    }

    private fun completeLoadPost() {
        binding.layoutProfileLikePostShimmer.stopShimmer()
        binding.layoutProfileLikePostShimmer.visibility = View.GONE
        binding.rvProfileLikePost.visibility = View.VISIBLE
    }

    private fun setBottomNavigationIconClickListener() {
        val currentViewPagerPosition =
            requireParentFragment().requireView()
                .findViewById<ViewPager2>(R.id.pager_my_page)
                .currentItem

        (requireActivity() as MainActivity).setBottomNavigationIconClickListener(reselectedIconId = R.id.MyPageFragment) {
            if (currentViewPagerPosition == LIKES_TAB_ID) {
                getProfileLikePostList()
                setScrollToTop(isSmoothScroll = true)
            }
        }
    }

    private fun setScrollToTop(isSmoothScroll: Boolean = false) {
        (requireParentFragment() as MyPageFragment).resetAppBarScrollPosition()
        with(binding.rvProfileLikePost) {
            if (isSmoothScroll) this.smoothScrollToPosition(0)
            else this.scrollToPosition(0)
        }
    }
}