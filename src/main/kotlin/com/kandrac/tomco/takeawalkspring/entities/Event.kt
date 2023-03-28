package com.kandrac.tomco.takeawalkspring.entities

import jakarta.persistence.*
import java.sql.Timestamp

@Entity
@Table(name = "events")
class Event(

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Int? = null,

    var name: String? = null,

    var description: String? = null,

    var cancelled: Boolean? = null,

    var start: Timestamp? = null,

    @Column(name = "end_date")
    var endDate: Timestamp? = null,

    @Column(name = "owner_lat")
    var ownerLat: Double? = null,

    @Column(name = "owner_lon")
    var ownerLon: Double? = null,

    @Column(name = "actual_location")
    var actualLocation: Int? = null,


    @ManyToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "owner_id")
    var user: User? = null,

    @OneToMany(mappedBy = "event")
    var invites: List<Invite>? = null,

    @OneToMany(mappedBy = "event")
    var locations: List<Location>? = null,

    @OneToMany(mappedBy = "event")
    var pictures: List<Picture>? = null,

    @OneToMany(mappedBy = "event")
    var messages: List<Message>? = null

)