package com.kandrac.tomco.takeawalkspring.entities

import jakarta.persistence.*
import java.sql.Timestamp

@Entity
@Table(name = "messages")
class Message(

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Int? = null,

    var message: String? = null,

    var sent: Timestamp? = null,


    @ManyToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "user_id")
    var user: User? = null,

    @ManyToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "event_id")
    var event: Event? = null

)