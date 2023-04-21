package com.kandrac.tomco.takeawalkspring.entities

import jakarta.persistence.*

@Entity
@Table(name = "users")
class User(

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    var id: Int? = null,

    var picture: String? = null,

    var email: String? = null,

    var password: String? = null,

    @Column(name = "username", unique = true)
    var username: String? = null,

    var bio: String? = null,

    @Column(name = "device_token")
    var deviceToken: String? = null,

    @OneToMany(mappedBy = "user")
    var events: List<Event>? = null,

    @OneToMany(mappedBy = "user")
    var invites: List<Invite>? = null,

    @OneToMany(mappedBy = "user")
    var messages: List<Message>? = null,

    @OneToOne(mappedBy = "user")
    var refreshToken: RefreshToken? = null

)