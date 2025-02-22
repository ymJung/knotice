package com.ym.knotice.service

import com.ym.knotice.controller.model.NoticeRequest
import com.ym.knotice.controller.model.NoticeResponse
import com.ym.knotice.repository.NoticeRepository
import com.ym.knotice.repository.entity.NoticeEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class NoticeService(
    private val noticeRepository: NoticeRepository
) {

    /**
     * 등록
     */
    fun createNotice(request: NoticeRequest): NoticeResponse {
        val notice = NoticeEntity(
            title = request.title,
            content = request.content,
            startDateTime = request.startDateTime,
            endDateTime = request.endDateTime,
            attachments = request.attachments ?: emptyList(),
            createdAt = LocalDateTime.now(),
            updatedAt = null,
            viewCount = 0,
            writer = request.writer,
            deleted = false
        )
        val saved = noticeRepository.save(notice)
        return NoticeResponse.fromEntity(saved)
    }

    /**
     * 조회
     * - deleted = false 인 경우만 유효
     * - 조회 시 조회수 증가
     */
    suspend fun getNotice(noticeId: Long): NoticeResponse = withContext(Dispatchers.IO) {
        val notice = noticeRepository.findById(noticeId).orElseThrow {
            IllegalArgumentException("존재하지 않는 게시물입니다. ID: $noticeId")
        }
        if (notice.deleted) {
            throw IllegalArgumentException("이미 삭제된 게시물입니다. ID: $noticeId")
        }
        notice.viewCount += 1
        noticeRepository.save(notice)
        NoticeResponse.fromEntity(notice)
    }


    /**
     * 수정
     * - 수정 시 updatedAt 갱신
     */
    fun updateNotice(noticeId: Long, request: NoticeRequest): NoticeResponse {
        val notice = noticeRepository.findById(noticeId).orElseThrow {
            IllegalArgumentException("존재하지 않는 게시물입니다. ID: $noticeId")
        }

        if (notice.deleted) {
            throw IllegalArgumentException("이미 삭제된 게시물은 수정할 수 없습니다. ID: $noticeId")
        }

        notice.apply {
            title = request.title
            content = request.content
            startDateTime = request.startDateTime
            endDateTime = request.endDateTime
            attachments = request.attachments ?: emptyList()
            writer = request.writer
            updatedAt = LocalDateTime.now()
        }

        val updated = noticeRepository.save(notice)
        return NoticeResponse.fromEntity(updated)
    }

    /**
     * 삭제(논리 삭제)
     * - 실제 DB에서 제거하지 않고 deleted 플래그만 true로 변경
     */
    fun deleteNotice(noticeId: Long) {
        val notice = noticeRepository.findById(noticeId).orElseThrow {
            IllegalArgumentException("존재하지 않는 게시물입니다. ID: $noticeId")
        }

        // 이미 삭제된 게시물이라면 예외
        if (notice.deleted) {
            throw IllegalArgumentException("이미 삭제된 게시물입니다. ID: $noticeId")
        }

        // 소프트 삭제 처리
        notice.deleted = true
        noticeRepository.save(notice)
    }

    /**
     * 목록 조회
     * - deleted = false 인 게시물만 리턴
     */
    fun getAllNotices(): List<NoticeResponse> {
        return noticeRepository.findAll()
            .filter { !it.deleted }  // flag filter
            .map { NoticeResponse.fromEntity(it) }
    }
}
