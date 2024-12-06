package com.application.p3tskit.data.remote.response

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)
