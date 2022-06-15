package com.example.trendinggithubrepo.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import com.example.trendinggithubrepo.api.GithubApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GithubRepository @Inject constructor(private val githubApi: GithubApi) {

    fun getReposResults() =
        Pager(
            config = PagingConfig(
                pageSize = 20,
                maxSize = 100,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { GithubPagingSource(githubApi) }
        ).liveData
}