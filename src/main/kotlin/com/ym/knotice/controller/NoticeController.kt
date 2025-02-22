package com.ym.knotice.controller

import com.ym.knotice.controller.model.NoticeRequest
import com.ym.knotice.controller.model.NoticeResponse
import com.ym.knotice.service.NoticeService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/notices")
class NoticeController(
    private val noticeService: NoticeService
) {

    /**
     * 등록
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createNotice(@RequestBody request: NoticeRequest): NoticeResponse {
        return noticeService.createNotice(request)
    }

    /**
     * 조회
     */
    @GetMapping("/{noticeId}")
    suspend fun getNotice(@PathVariable noticeId: Long): NoticeResponse {
        return noticeService.getNotice(noticeId)
    }

    /**
     * 수정
     */
    @PutMapping("/{noticeId}")
    fun updateNotice(
        @PathVariable noticeId: Long,
        @RequestBody request: NoticeRequest
    ): NoticeResponse {
        return noticeService.updateNotice(noticeId, request)
    }

    /**
     * 삭제(flag)
     */
    @DeleteMapping("/{noticeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteNotice(@PathVariable noticeId: Long) {
        noticeService.deleteNotice(noticeId)
    }

    /**
     * 목록 조회
     */
    @GetMapping
    fun getAllNotices(): List<NoticeResponse> {
        return noticeService.getAllNotices()
    }
}
