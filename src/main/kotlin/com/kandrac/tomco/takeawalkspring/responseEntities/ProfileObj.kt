package com.kandrac.tomco.takeawalkspring.responseEntities

data class ProfileObj(
    val id: Int,
    val userName: String,
    val email: String,
    val bio: String?,
    val profilePicture: String?
)