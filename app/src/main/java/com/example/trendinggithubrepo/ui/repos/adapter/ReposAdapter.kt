package com.example.trendinggithubrepo.ui.repos.adapter

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.trendinggithubrepo.data.model.Repo
import com.example.trendinggithubrepo.databinding.ItemTrendingRepoBinding

class ReposAdapter : PagingDataAdapter<Repo, ReposAdapter.ViewHolder>(REPO_COMPARATOR) {

    companion object {
        private val REPO_COMPARATOR = object : DiffUtil.ItemCallback<Repo>() {
            override fun areItemsTheSame(oldItem: Repo, newItem: Repo) = oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Repo, newItem: Repo) = oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemTrendingRepoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position).let { repo ->
            with(holder) {
                itemView.tag = repo
                if (repo != null) {
                    bind(createOnClickListener(repo), repo)
                }
            }
        }
    }

    private fun createOnClickListener(
        repo: Repo
    ): View.OnClickListener {
        return View.OnClickListener {
            repo.expand = !repo.expand
            notifyDataSetChanged()
        }
    }

    class ViewHolder(val binding: ItemTrendingRepoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(listener: View.OnClickListener, repo: Repo) {

            binding.apply {

                clExpandable.visibility = if (repo.expand) View.VISIBLE else View.GONE

                Glide.with(itemView)
                    .load(repo.owner.avatar_url)
                    .circleCrop()
                    .error(android.R.drawable.stat_notify_error)
                    .into(avatar)

                val str = SpannableString(repo.owner.login + " / " + repo.name)
                str.setSpan(
                    StyleSpan(Typeface.BOLD),
                    repo.owner.login.length,
                    str.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                name.text = repo.owner.login
                title.text = repo.name

                description.text = repo.description
                when {
                    repo.language != null -> {
                        language.text = repo.language
                        language.visibility = View.VISIBLE
                    }
                    else -> {
                        language.visibility = View.GONE
                    }
                }
                rating.text = repo.stars.toString()

                root.setOnClickListener(listener)
            }

        }
    }
}