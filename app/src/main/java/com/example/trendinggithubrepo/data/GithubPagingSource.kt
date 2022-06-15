package com.example.trendinggithubrepo.data

import androidx.paging.PagingSource
import com.example.trendinggithubrepo.api.GithubApi
import com.example.trendinggithubrepo.data.model.Repo
import retrofit2.HttpException
import java.io.IOException

private const val STARTING_PAGE_INDEX = 1

class GithubPagingSource(
    private val githubApi: GithubApi
) : PagingSource<Int, Repo>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Repo> {
        val position = params.key ?: STARTING_PAGE_INDEX

        return try {
            val response = githubApi.getTrendingRepos("language",position, params.loadSize)
            val repos = response.items
            LoadResult.Page(
                data = repos,
                prevKey = if (position == STARTING_PAGE_INDEX) null else position - 1,
                nextKey = if (repos.isEmpty()) null else position + 1
            )
        } catch (exception: IOException) {
            LoadResult.Error(exception)
        } catch (exception: HttpException) {
            LoadResult.Error(exception)
        }
    }

}