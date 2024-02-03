package com.example.watchit.data.review

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ReviewDAO {
    @Query("SELECT * FROM review order by timestamp desc")
    fun getAll(): List<Review>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg reviews: Review)

    @Delete
    fun delete(review: Review)
}