package com.kandrac.tomco.takeawalkspring.entities

import jakarta.persistence.*

@Entity
@Table(name = "invites")
class Invite(

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    var id: Int? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    var status: Status? = null,


    @ManyToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "user_id")
    var user: User? = null,

    @ManyToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "event_id")
    var event: Event? = null

)