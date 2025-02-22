package com.ym.knotice.controller
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.ym.knotice.controller.model.NoticeRequest
import com.ym.knotice.controller.model.NoticeResponse
import com.ym.knotice.service.NoticeService
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.time.LocalDateTime

class NoticeControllerTest {

    private lateinit var mockMvc: MockMvc
    private lateinit var noticeService: NoticeService
    private lateinit var objectMapper: ObjectMapper

    @BeforeEach
    fun setUp() {
        noticeService = mockk()
        val controller = NoticeController(noticeService)
        objectMapper = ObjectMapper()
            .registerModule(JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
            .build()
    }

    @Test
    fun `createNotice - 정상 등록`() {
        // given
        val request = NoticeRequest(
            title = "New Notice",
            content = "Hello World",
            startDateTime = LocalDateTime.now(),
            endDateTime = LocalDateTime.now().plusDays(1),
            attachments = listOf("file1", "file2"),
            writer = "Tester"
        )
        val response = NoticeResponse(
            id = 1L,
            title = request.title,
            content = request.content,
            startDateTime = request.startDateTime,
            endDateTime = request.endDateTime,
            attachments = request.attachments ?: emptyList(),
            createdAt = LocalDateTime.now(),
            updatedAt = null,
            viewCount = 0,
            writer = request.writer
        )

        every { noticeService.createNotice(request) } returns response

        // when & then
        mockMvc.perform(
            post("/api/notices")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.title").value("New Notice"))

        verify(exactly = 1) { noticeService.createNotice(request) }
    }

    @Test
    fun `getNotice - 정상 조회`() {
        // given
        val noticeId = 1L
        val response = NoticeResponse(
            id = noticeId,
            title = "Test Notice",
            content = "Content",
            startDateTime = null,
            endDateTime = null,
            attachments = emptyList(),
            createdAt = LocalDateTime.now(),
            updatedAt = null,
            viewCount = 10,
            writer = "Tester"
        )

        coEvery { noticeService.getNotice(noticeId) } returns response

        // when & then
        mockMvc.perform(get("/api/notices/{noticeId}", noticeId))
            .andExpect { org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk() }
            .andExpect { jsonPath("$.id").value(1L) }
            .andExpect { jsonPath("$.title").value("Test Notice") }
            .andExpect { jsonPath("$.viewCount").value(10) }

        coVerify(exactly = 1) { noticeService.getNotice(noticeId) }
    }

    @Test
    fun `updateNotice - 정상 수정`() {
        // given
        val noticeId = 1L
        val updateRequest = NoticeRequest(
            title = "Updated Notice",
            content = "Updated Content",
            startDateTime = null,
            endDateTime = null,
            attachments = listOf("fileX"),
            writer = "Updater"
        )
        val updatedResponse = NoticeResponse(
            id = noticeId,
            title = updateRequest.title,
            content = updateRequest.content,
            startDateTime = updateRequest.startDateTime,
            endDateTime = updateRequest.endDateTime,
            attachments = updateRequest.attachments ?: emptyList(),
            createdAt = LocalDateTime.now().minusDays(1),
            updatedAt = LocalDateTime.now(),
            viewCount = 20,
            writer = updateRequest.writer
        )

        every { noticeService.updateNotice(noticeId, any()) } returns updatedResponse

        // when & then
        mockMvc.perform(
            put("/api/notices/{noticeId}", noticeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.title").value("Updated Notice"))
            .andExpect(jsonPath("$.content").value("Updated Content"))
            .andExpect(jsonPath("$.attachments[0]").value("fileX"))

        verify(exactly = 1) { noticeService.updateNotice(noticeId, any()) }
    }

    @Test
    fun `deleteNotice - 정상 삭제`() {
        // given
        val noticeId = 1L
        every { noticeService.deleteNotice(noticeId) } returns Unit

        // when & then
        mockMvc.perform(
            delete("/api/notices/{noticeId}", noticeId)
        )
            .andExpect(status().isNoContent)

        verify(exactly = 1) { noticeService.deleteNotice(noticeId) }
    }
}
