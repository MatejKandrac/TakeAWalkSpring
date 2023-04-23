package com.kandrac.tomco.takeawalkspring.responseEntities

import java.sql.Timestamp

data class MessageObj(
    val id: Int,
    val message: String,
    val sent: Timestamp,
    val userName: String,
    val userId: Int,
    val profilePicture: String?
)
