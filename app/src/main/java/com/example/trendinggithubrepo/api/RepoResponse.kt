package com.example.trendinggithubrepo.api

import com.example.trendinggithubrepo.data.model.Repo
import com.google.gson.annotations.SerializedName

data class RepoResponse(

    @SerializedName("total_count")
    val total: Int = 0,

    @SerializedName("items")
    val items: List<Repo> = emptyList(),

    val nextPage: Int? = null
)