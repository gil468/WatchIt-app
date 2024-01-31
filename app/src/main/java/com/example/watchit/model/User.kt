package com.example.watchit.model

import java.io.Serializable

data class User(
    var firstName: String,
    var lastName: String,
    var email: String,
    var password: String
) : Serializable {
    constructor() : this("", "", "", "")
}