package com.ym.knotice.service

import com.ym.knotice.controller.model.NoticeRequest
import com.ym.knotice.repository.NoticeRepository
import com.ym.knotice.repository.entity.NoticeEntity
import io.mockk.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime

class NoticeServiceTest {

    private lateinit var noticeRepository: NoticeRepository
    private lateinit var noticeService: NoticeService

    @BeforeEach
    fun setUp() {
        noticeRepository = mockk()
        noticeService = NoticeService(noticeRepository)
    }

    @Test
    fun createNotice() {
        // given
        val now = LocalDateTime.now()
        val request = NoticeRequest(
            title = "test",
            content = "content",
            startDateTime = now,
            endDateTime = now.plusDays(1),
            attachments = listOf("file1", "file2"),
            writer = "user"
        )

        // Notice 엔티티가 저장될 때, 리턴해줄 Mock 엔티티
        val savedNotice = NoticeEntity(
            id = 1L,
            title = request.title,
            content = request.content,
            startDateTime = request.startDateTime,
            endDateTime = request.endDateTime,
            attachments = request.attachments ?: emptyList(),
            createdAt = now,
            writer = request.writer
        )

        every { noticeRepository.save(any<NoticeEntity>()) } returns savedNotice

        // when
        val result = noticeService.createNotice(request)

        // then
        assertNotNull(result.id)
        assertEquals("test", result.title)
        assertEquals("content", result.content)
        verify(exactly = 1) { noticeRepository.save(any<NoticeEntity>()) }
    }

    @Test
    fun getNotice() {
        // given
        val noticeId = 1L
        val existingNotice = NoticeEntity(
            id = noticeId,
            title = "test",
            content = "content",
            deleted = false
        )

        every { noticeRepository.findById(noticeId) } returns java.util.Optional.of(existingNotice)
        val updatedNotice = existingNotice.apply {
            viewCount = 1
        }
        every { noticeRepository.save(any<NoticeEntity>()) } returns updatedNotice

        // when
        val result = noticeService.getNotice(noticeId)

        // then
        assertEquals(noticeId, result.id)
        assertEquals("test", result.title)

    }

    @Test
    fun updateNotice() {
        // given
        val noticeId = 1L
        val deletedNotice = NoticeEntity(id = noticeId, title = "Title", content = "Content", deleted = true)
        every { noticeRepository.findById(noticeId) } returns java.util.Optional.of(deletedNotice)

        val request = NoticeRequest(
            title = "test",
            content = "content",
            startDateTime = null,
            endDateTime = null,
            attachments = null,
            writer = "user"
        )

        // when & then
        val exception = assertThrows(IllegalArgumentException::class.java) {
            noticeService.updateNotice(noticeId, request)
        }
        assertTrue(exception.message!!.contains("삭제된"))
    }

    @Test
    fun deleteNotice() {
        val noticeId = 1L
        val existingNotice = NoticeEntity(id = noticeId, title = "test", content = "content", deleted = false)
        every { noticeRepository.findById(noticeId) } returns java.util.Optional.of(existingNotice)
        val updatedNotice = existingNotice.apply { deleted = true }
        every { noticeRepository.save(any<NoticeEntity>()) } returns updatedNotice

        assertThrows<IllegalArgumentException> {
            noticeService.deleteNotice(noticeId)
        }


    }
}
