package com.example.trendinggithubrepo.ui.repos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.trendinggithubrepo.data.GithubRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ReposViewModel @Inject constructor(
    repository: GithubRepository
) : ViewModel() {

    val repos = repository.getReposResults().cachedIn(viewModelScope)
}