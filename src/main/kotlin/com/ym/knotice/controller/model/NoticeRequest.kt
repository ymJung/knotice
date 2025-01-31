package com.ym.knotice.controller.model

import java.time.LocalDateTime

data class NoticeRequest(
    val title: String,
    val content: String,
    val startDateTime: LocalDateTime?,
    val endDateTime: LocalDateTime?,
    val attachments: List<String>?,
    val writer: String?
)
