package com.galeopsis.nasapictureofthedayapp.model.repository

import com.galeopsis.nasapictureofthedayapp.model.api.NasaApi
import com.galeopsis.nasapictureofthedayapp.model.dao.NasaDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NasaRepository(
    private val nasaApi: NasaApi,
    private val nasaDao: NasaDao
) {
    val data = nasaDao.findAll()

    suspend fun refresh(date: String) {
        withContext(Dispatchers.IO) {
            val nasaPicture = nasaApi.getAllPhotosAsync(date).await()
            nasaDao.deleteAllData()
            nasaDao.add(nasaPicture)
        }
    }
}
