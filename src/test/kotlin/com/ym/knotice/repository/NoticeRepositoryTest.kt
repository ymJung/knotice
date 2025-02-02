package com.ym.knotice.repository

import com.ym.knotice.repository.entity.NoticeEntity
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import java.time.LocalDateTime

@DataJpaTest
class NoticeRepositoryTest {

    @Autowired
    private lateinit var noticeRepository: NoticeRepository



    @Test
    fun `save`() {
        val notice = NoticeEntity(
            title = "test",
            content = "content",
            createdAt = LocalDateTime.now(),
            deleted = false
        )
        // when
        val saved = noticeRepository.save(notice)

        // then
        assertNotNull(saved.id)
        assertEquals("test", saved.title)
    }

    @Test
    fun `findById`() {
        // given
        val notice = NoticeEntity(
            title = "fe",
            content = "c",
            createdAt = LocalDateTime.now()
        )
        val saved = noticeRepository.save(notice)

        // when
        val found = noticeRepository.findById(saved.id!!)

        // then
        assertTrue(found.isPresent)
        assertEquals("fe", found.get().title)
    }

    @Test
    fun `delete`() {
        // given
        val notice = NoticeEntity(
            title = "dd",
            content = "c",
            createdAt = LocalDateTime.now()
        )
        val saved = noticeRepository.save(notice)

        // when
        noticeRepository.deleteById(saved.id!!)
        val found = noticeRepository.findById(saved.id!!)

        // then
        assertFalse(found.isPresent)
    }
}
