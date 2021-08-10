package com.galeopsis.nasapictureofthedayapp.model.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.galeopsis.nasapictureofthedayapp.model.entity.NasaData

@Dao
interface NasaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun add(nasaData: NasaData)

    @Query("SELECT * FROM nasadata")
    fun findAll(): LiveData<List<NasaData>?>

    @Query("DELETE FROM nasadata")
    fun deleteAllData()

}