package com.galeopsis.nasapictureofthedayapp.model

import androidx.room.Database
import androidx.room.RoomDatabase
import com.galeopsis.nasapictureofthedayapp.model.dao.NasaDao
import com.galeopsis.nasapictureofthedayapp.model.entity.NasaData

@Database(entities = [NasaData::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract val nasaDao: NasaDao
}