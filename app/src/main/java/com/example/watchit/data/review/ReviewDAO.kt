package com.example.watchit.data.review

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ReviewDAO {
    @Query("SELECT * FROM review order by timestamp desc")
    fun getAll(): LiveData<MutableList<Review>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(review: Review)

    @Delete
    fun delete(review: Review)
}