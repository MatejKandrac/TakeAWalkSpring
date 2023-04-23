package com.kandrac.tomco.takeawalkspring.entities

import jakarta.persistence.*

@Entity
@Table(name = "pictures")
class Picture(

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Int? = null,

    var link: String? = null,

    var deleted: Boolean = false,

    @ManyToOne(cascade = [CascadeType.REFRESH])
    @JoinColumn(name = "event_id")
    var event: Event? = null

)