package com.example.anitron.data.repository

import com.example.anitron.domain.service.RetrofitService

class MovieInfoRepository(private val retrofitService: RetrofitService) {
    suspend fun getMovieOnClick(imdbId : String) = retrofitService.getMovieOnClick(imdbId)
}