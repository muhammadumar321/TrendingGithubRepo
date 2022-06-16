package com.example.trendinggithubrepo.ui.repos

import android.app.ActionBar
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Html
import android.view.*
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.paging.LoadState
import com.example.trendinggithubrepo.R
import com.example.trendinggithubrepo.databinding.FragmentReposBinding
import com.example.trendinggithubrepo.ui.repos.adapter.ReposLoadStateAdapter
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ReposFragment : Fragment(R.layout.fragment_repos) {

    private val viewModel by viewModels<ReposViewModel>()
    private var _binding: FragmentReposBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.actionBar?.title = Html.fromHtml("<font color=\"black\">" + getString(R.string.app_title) + "</font>")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        (activity as AppCompatActivity).supportActionBar?.title = "Trending"
//        val adapter = ReposAdapter()

        _binding = FragmentReposBinding.bind(view)

        binding.apply {

            recycler.apply {
                setHasFixedSize(true)
                itemAnimator = null
                this.adapter = viewModel.adapter.withLoadStateHeaderAndFooter(
                    header = ReposLoadStateAdapter {
                        viewModel.adapter.retry()
                    },
                    footer = ReposLoadStateAdapter {
                        viewModel.adapter.retry()
                    }
                )
                postponeEnterTransition()
                viewTreeObserver.addOnPreDrawListener {
                    startPostponedEnterTransition()
                    true
                }
            }

            btnRetry.setOnClickListener {
                viewModel.adapter.retry()
            }
        }

        viewModel.repos.observe(viewLifecycleOwner) {
            viewModel.adapter.submitData(viewLifecycleOwner.lifecycle, it)
        }

        viewModel.adapter.addLoadStateListener { loadState ->
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
                    viewModel.adapter.itemCount < 1
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_refresh -> {
                viewModel.adapter.refresh()
                return true
            }
        }
        return false
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

}