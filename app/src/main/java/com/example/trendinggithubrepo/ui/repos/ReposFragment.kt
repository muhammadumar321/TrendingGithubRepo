package com.example.trendinggithubrepo.ui.repos

import android.app.ActionBar
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.paging.LoadState
import com.example.trendinggithubrepo.R
import com.example.trendinggithubrepo.databinding.FragmentReposBinding
import com.example.trendinggithubrepo.ui.repos.adapter.ReposAdapter
import com.example.trendinggithubrepo.ui.repos.adapter.ReposLoadStateAdapter
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ReposFragment : Fragment(R.layout.fragment_repos) {

    private val viewModel by viewModels<ReposViewModel>()
    private var _binding: FragmentReposBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.actionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        activity?.actionBar?.setCustomView(R.layout.custom_actionbar)
        val p: ActionBar.LayoutParams =
            ActionBar.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        p.gravity = Gravity.CENTER
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        (activity as AppCompatActivity).supportActionBar?.title = "Trending"

        _binding = FragmentReposBinding.bind(view)

        val adapter = ReposAdapter()

        binding.apply {

            recycler.apply {
                setHasFixedSize(true)
                itemAnimator = null
                this.adapter = adapter.withLoadStateHeaderAndFooter(
                    header = ReposLoadStateAdapter {
                        adapter.retry() },
                    footer = ReposLoadStateAdapter {
                        adapter.retry() }
                )
                postponeEnterTransition()
                viewTreeObserver.addOnPreDrawListener {
                    startPostponedEnterTransition()
                    true
                }
            }

            btnRetry.setOnClickListener {
                adapter.retry()
            }
        }

        viewModel.repos.observe(viewLifecycleOwner) {
            adapter.submitData(viewLifecycleOwner.lifecycle, it)
        }

        adapter.addLoadStateListener { loadState ->
            binding.apply {
                when (loadState.source.refresh) {
                    is LoadState.Loading -> {
                        shimmerViewContainer.duration = 1500
                        shimmerViewContainer.startShimmerAnimation()
                        shimmerViewContainer.visibility = View.VISIBLE

                    }
                    else -> {
                        shimmerViewContainer.stopShimmerAnimation()
                        shimmerViewContainer.visibility = View.GONE
                    }
                }

                recycler.isVisible = loadState.source.refresh is LoadState.NotLoading
                btnRetry.isVisible = loadState.source.refresh is LoadState.Error
                when (loadState.source.refresh) {
                    is LoadState.Error -> {
                        lavRetry.isVisible = true
                        lavRetry.playAnimation()
                    }
                    else -> {
                        lavRetry.isVisible = false
                        lavRetry.pauseAnimation()
                    }
                }

                // no results found
                if (loadState.source.refresh is LoadState.NotLoading &&
                    loadState.append.endOfPaginationReached &&
                    adapter.itemCount < 1
                ) {
                    recycler.isVisible = false
                    lavRetry.isVisible = true
                    lavRetry.playAnimation()
                } else {
                    lavRetry.pauseAnimation()
                    lavRetry.isVisible = false
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}