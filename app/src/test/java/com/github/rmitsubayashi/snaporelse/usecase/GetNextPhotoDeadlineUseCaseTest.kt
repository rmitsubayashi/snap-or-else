package com.github.rmitsubayashi.snaporelse.usecase

import com.github.rmitsubayashi.snaporelse.model.entity.Challenge
import com.github.rmitsubayashi.snaporelse.model.entity.PhotoInfo
import com.github.rmitsubayashi.snaporelse.model.entity.PhotoFrequency
import com.github.rmitsubayashi.snaporelse.model.repository.PhotoInfoRepository
import com.github.rmitsubayashi.snaporelse.usecase.util.DateUtil
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime

class GetNextPhotoDeadlineUseCaseTest {
    private lateinit var usecase: GetNextPhotoDeadlineUseCase
    @Mock
    private lateinit var photoInfoRepository: PhotoInfoRepository
    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        usecase = GetNextPhotoDeadlineUseCase(photoInfoRepository)
    }

    @Test
    fun noPhotos() {
        val challenge = challengeForTest(
            LocalDateTime.of(2000, 1, 14, 9,0),
            PhotoFrequency.EVERY_DAY,
            null
        )
        runBlocking {
            `when`(photoInfoRepository.getByChallenge(challenge)).thenReturn(emptyList())
            val nextDeadline = usecase.execute(challenge)
            assertEquals(LocalDateTime.of(2000, 1, 16, DateUtil.CHANGE_DATE_HOUR, 0), nextDeadline)
        }
    }

    @Test
    fun week() {
        val challenge = challengeForTest(
            LocalDateTime.of(2000, 1, 14, 9,0),
            PhotoFrequency.EVERY_WEEK,
            DayOfWeek.MONDAY
        )
        runBlocking {
            `when`(photoInfoRepository.getByChallenge(challenge)).thenReturn(emptyList())
            val nextDeadline = usecase.execute(challenge)
            // 14th is on Friday, 17th is on Monday
            assertEquals(LocalDateTime.of(2000, 1, 18, DateUtil.CHANGE_DATE_HOUR, 0), nextDeadline)
        }
    }

    @Test
    fun nextWeek() {
        val challenge = challengeForTest(
            LocalDateTime.of(2000, 1, 17, 9,0),
            PhotoFrequency.EVERY_WEEK,
            DayOfWeek.MONDAY
        )
        runBlocking {
            `when`(photoInfoRepository.getByChallenge(challenge)).thenReturn(emptyList())
            val nextDeadline = usecase.execute(challenge)
            // 17th is on Monday, 24th is on Monday
            assertEquals(LocalDateTime.of(2000, 1, 25, DateUtil.CHANGE_DATE_HOUR, 0), nextDeadline)
        }
    }

    @Test
    fun crossMonth() {
        val challenge = challengeForTest(
            LocalDateTime.of(2000, 1, 27, 9,0),
            PhotoFrequency.EVERY_WEEK,
            DayOfWeek.TUESDAY
        )
        runBlocking {
            `when`(photoInfoRepository.getByChallenge(challenge)).thenReturn(emptyList())
            val nextDeadline = usecase.execute(challenge)
            // 26th is on Wednesday, 1st is on Tuesday
            assertEquals(LocalDateTime.of(2000, 2, 2, DateUtil.CHANGE_DATE_HOUR, 0), nextDeadline)
        }
    }

    @Test
    fun photos() {
        val challenge = challengeForTest(
            LocalDateTime.of(2000, 1, 14, 9,0),
            PhotoFrequency.EVERY_DAY,
            null
        )
        runBlocking {
            `when`(photoInfoRepository.getByChallenge(challenge)).thenReturn(
                listOf(
                    PhotoInfo(0, "file1", LocalDateTime.of(2000, 1, 15, 9, 0)),
                    PhotoInfo(1, "file2", LocalDateTime.of(2000, 1, 16, 9, 0))
                )
            )
            val nextDeadline = usecase.execute(challenge)
            assertEquals(LocalDateTime.of(2000, 1, 18, DateUtil.CHANGE_DATE_HOUR, 0), nextDeadline)
        }
    }

    @Test
    fun lateNight() {
        val challenge = challengeForTest(
            LocalDateTime.of(2000, 1, 15, 1,0),
            PhotoFrequency.EVERY_DAY,
            null
        )
        runBlocking {
            `when`(photoInfoRepository.getByChallenge(challenge)).thenReturn(emptyList())
            val nextDeadline = usecase.execute(challenge)
            assertEquals(LocalDateTime.of(2000, 1, 16, DateUtil.CHANGE_DATE_HOUR, 0), nextDeadline)
        }
    }

    private fun challengeForTest(createdAt: LocalDateTime, photoFrequency: PhotoFrequency, dayOfDeadline: DayOfWeek?): Challenge {
        return Challenge(
            LocalDate.of(2100, 1, 14),
            createdAt, photoFrequency, dayOfDeadline,false,
            0, ""
        )
    }
}