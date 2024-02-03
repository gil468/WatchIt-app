package com.example.watchit.data.user

import java.io.Serializable

data class PublishUserDTO(
    var firstName: String,
    var lastName: String,
    var email: String,
    var password: String
) : Serializable {
    constructor() : this("", "", "", "")
}