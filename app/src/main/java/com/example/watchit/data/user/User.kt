package com.example.watchit.data.user

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class User(
    @PrimaryKey
    val id: String,
    val firstName: String,
    val lastName: String,
    val profileImage: String
)
