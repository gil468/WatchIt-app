package com.example.watchit.data

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.watchit.WatchItApplication
import com.example.watchit.data.review.Review
import com.example.watchit.data.review.ReviewDAO
import com.example.watchit.data.user.User
import com.example.watchit.data.user.UserDAO


@Database(entities = [Review::class, User::class], version = 3, exportSchema = true)
abstract class AppLocalDbRepository : RoomDatabase() {
    abstract fun reviewDao(): ReviewDAO
    abstract fun userDao(): UserDAO
}

object AppLocalDatabase {
    val db: AppLocalDbRepository by lazy {
        val context = WatchItApplication.Globals.appContext
            ?: throw IllegalStateException("Application context not available")

        Room.databaseBuilder(
            context,
            AppLocalDbRepository::class.java,
            "watch-it"
        ).fallbackToDestructiveMigration()
            .build()
    }
}