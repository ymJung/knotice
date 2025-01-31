package com.ym.knotice.controller.model

import com.ym.knotice.repository.entity.NoticeEntity
import java.time.LocalDateTime

data class NoticeResponse(
    val id: Long?,
    val title: String,
    val content: String,
    val startDateTime: LocalDateTime?,
    val endDateTime: LocalDateTime?,
    val attachments: List<String>,
    val createdAt: LocalDateTime,
    val viewCount: Long,
    val writer: String?
) {
    companion object {
        fun fromEntity(entity: NoticeEntity): NoticeResponse {
            return NoticeResponse(
                id = entity.id,
                title = entity.title,
                content = entity.content,
                startDateTime = entity.startDateTime,
                endDateTime = entity.endDateTime,
                attachments = entity.attachments,
                createdAt = entity.createdAt,
                viewCount = entity.viewCount,
                writer = entity.writer
            )
        }
    }
}
