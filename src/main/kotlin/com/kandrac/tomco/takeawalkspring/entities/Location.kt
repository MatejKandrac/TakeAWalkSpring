package com.kandrac.tomco.takeawalkspring.entities

import jakarta.persistence.*

@Entity
@Table(name = "locations")
class Location(

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Int? = null,

    var name: String? = null,

    @Column(name = "location_order")
    var locationOrder: Int? = null,

    var latitude: Double? = null,

    var longitude: Double? = null,

    @ManyToOne()
    @JoinColumn(name = "event_id")
    var event: Event? = null

)
