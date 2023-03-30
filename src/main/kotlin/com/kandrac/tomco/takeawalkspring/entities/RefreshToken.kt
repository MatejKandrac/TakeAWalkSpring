package com.kandrac.tomco.takeawalkspring.entities

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import java.time.ZonedDateTime

@Entity
class RefreshToken(

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Int? = null,

    var token: String? = null,

    var expiration: ZonedDateTime? = null,

    @OneToOne
    @JoinColumn(nullable = false, name = "user_id")
    var user: User? = null

)