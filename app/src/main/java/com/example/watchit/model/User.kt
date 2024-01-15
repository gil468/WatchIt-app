package com.example.watchit.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class User(
    var firstName: String,
    var lastName: String,
    var email: String,
    var password: String
) : Serializable