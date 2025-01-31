package com.ym.knotice.repository.entity

import java.time.LocalDateTime
import jakarta.persistence.*

@Entity
@Table(name = "notice")
class NoticeEntity (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    var title: String,

    @Lob
    @Column(nullable = false)
    var content: String,

    var startDateTime: LocalDateTime? = null,
    var endDateTime: LocalDateTime? = null,

    @ElementCollection
    var attachments: List<String> = emptyList(),

    var createdAt: LocalDateTime = LocalDateTime.now(),

    var updatedAt: LocalDateTime? = null,

    var viewCount: Long = 0,

    var writer: String? = null,

    var deleted: Boolean = false,
)
