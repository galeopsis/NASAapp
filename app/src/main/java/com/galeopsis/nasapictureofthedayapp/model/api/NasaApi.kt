package com.galeopsis.nasapictureofthedayapp.model.api

import com.galeopsis.nasapictureofthedayapp.BuildConfig.API_KEY
import com.galeopsis.nasapictureofthedayapp.model.entity.NasaData
import kotlinx.coroutines.Deferred
import retrofit2.http.GET
import retrofit2.http.Query

private const val api_key = API_KEY

interface NasaApi {

    @GET("apod?api_key=$api_key")
    fun getAllPhotosAsync(@Query("date") date: String): Deferred<NasaData>

}