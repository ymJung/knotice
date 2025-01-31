package com.ym.knotice.repository

import com.ym.knotice.repository.entity.NoticeEntity
import org.springframework.data.jpa.repository.JpaRepository

interface NoticeRepository : JpaRepository<NoticeEntity, Long> {
}
