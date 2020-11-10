package com.github.rmitsubayashi.snaporelse.usecase

import com.github.rmitsubayashi.snaporelse.model.entity.Challenge
import com.github.rmitsubayashi.snaporelse.model.entity.PhotoInfo
import com.github.rmitsubayashi.snaporelse.model.entity.PhotoFrequency
import com.github.rmitsubayashi.snaporelse.model.repository.ChallengeRepository
import com.github.rmitsubayashi.snaporelse.model.repository.PhotoInfoRepository
import com.github.rmitsubayashi.snaporelse.usecase.util.TodayGenerator
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime

class CheckUnsuccessfulChallengeUseCaseTest {
    private lateinit var usecase: CheckUnsuccessfulChallengeUseCase
    private lateinit var getNextPhotoDeadlineUseCase: GetNextPhotoDeadlineUseCase
    @Mock
    private lateinit var todayGenerator: TodayGenerator
    @Mock
    private lateinit var challengeRepository: ChallengeRepository
    @Mock
    private lateinit var photoInfoRepository: PhotoInfoRepository

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        getNextPhotoDeadlineUseCase = GetNextPhotoDeadlineUseCase(photoInfoRepository)
        usecase = CheckUnsuccessfulChallengeUseCase(challengeRepository, getNextPhotoDeadlineUseCase, todayGenerator)
    }

    @Test
    fun noCheatDaysLeft() {
        val endDay = LocalDate.of(2000, 2,1)
        val createdAt = LocalDateTime.of(2000, 1, 14, 9, 0)
        val cheatDaysLeft = 0
        val challenge = challengeForTest(createdAt, endDay, PhotoFrequency.EVERY_DAY, null, cheatDaysLeft, null)
        val today = LocalDateTime.of(2000, 1, 16, 9, 0)
        `when`(todayGenerator.getDateTime()).thenReturn(today)
        `when`(todayGenerator.getDate()).thenReturn(today.toLocalDate())
        runBlocking {
            `when`(photoInfoRepository.getByChallenge(challenge)).thenReturn(emptyList())
            usecase.execute(challenge)
            verify(challengeRepository).updateActive(challenge, false)
        }
    }

    @Test
    fun enoughCheatDays() {
        val endDay = LocalDate.of(2000, 2,1)
        val createdAt = LocalDateTime.of(2000, 1, 14, 9, 0)
        val cheatDaysLeft = 1
        val challenge = challengeForTest(createdAt, endDay, PhotoFrequency.EVERY_DAY, null, cheatDaysLeft, null)
        val today = LocalDateTime.of(2000, 1, 16, 9, 0)
        `when`(todayGenerator.getDateTime()).thenReturn(today)
        `when`(todayGenerator.getDate()).thenReturn(today.toLocalDate())
        runBlocking {
            `when`(photoInfoRepository.getByChallenge(challenge)).thenReturn(emptyList())
            val message = usecase.execute(challenge)
            assert(!usecase.isDeactivateMessage(message))
            verify(challengeRepository).updateCheatDay(challenge, 0)
        }
    }

    @Test
    fun enoughMultipleCheatDays() {
        val endDay = LocalDate.of(2000, 2,1)
        val createdAt = LocalDateTime.of(2000, 1, 14, 9, 0)
        val cheatDaysLeft = 2
        val challenge = challengeForTest(createdAt, endDay, PhotoFrequency.EVERY_DAY, null, cheatDaysLeft, null)
        val today = LocalDateTime.of(2000, 1, 17, 9, 0)
        `when`(todayGenerator.getDateTime()).thenReturn(today)
        `when`(todayGenerator.getDate()).thenReturn(today.toLocalDate())
        runBlocking {
            `when`(photoInfoRepository.getByChallenge(challenge)).thenReturn(emptyList())
            val message = usecase.execute(challenge)
            assert(!usecase.isDeactivateMessage(message))
            verify(challengeRepository).updateCheatDay(challenge, 0)
        }
    }

    @Test
    fun lateNight() {
        val endDay = LocalDate.of(2000, 2,1)
        val createdAt = LocalDateTime.of(2000, 1, 14, 9, 0)
        val cheatDaysLeft = 0
        val challenge = challengeForTest(createdAt, endDay, PhotoFrequency.EVERY_DAY, null, cheatDaysLeft, null)
        val today = LocalDateTime.of(2000, 1, 16, 1, 0)
        `when`(todayGenerator.getDateTime()).thenReturn(today)
        `when`(todayGenerator.getDate()).thenReturn(today.toLocalDate())
        runBlocking {
            `when`(photoInfoRepository.getByChallenge(challenge)).thenReturn(emptyList())
            val message = usecase.execute(challenge)
            assert(!usecase.isDeactivateMessage(message))
        }
    }

    @Test
    fun endDayBetweenDeadlineAndToday() {
        val endDay = LocalDate.of(2000, 1,16)
        val createdAt = LocalDateTime.of(2000, 1, 14, 9, 0)
        val cheatDaysLeft = 1
        val challenge = challengeForTest(createdAt, endDay, PhotoFrequency.EVERY_DAY, null, cheatDaysLeft, null)
        val today = LocalDateTime.of(2000, 1, 17, 9, 0)
        `when`(todayGenerator.getDateTime()).thenReturn(today)
        `when`(todayGenerator.getDate()).thenReturn(today.toLocalDate())
        runBlocking {
            `when`(photoInfoRepository.getByChallenge(challenge)).thenReturn(emptyList())
            val message = usecase.execute(challenge)
            assert(!usecase.isDeactivateMessage(message))
            verify(challengeRepository).updateCheatDay(challenge, 0)
        }
    }

    @Test
    fun deadlineBetweenEndDayAndToday() {
        val endDay = LocalDate.of(2000, 1,13)
        val createdAt = LocalDateTime.of(2000, 1, 8, 9, 0)
        val cheatDaysLeft = 0
        // deadline = 1/15
        val challenge = challengeForTest(createdAt, endDay, PhotoFrequency.EVERY_WEEK, DayOfWeek.SATURDAY, cheatDaysLeft, null)
        val today = LocalDateTime.of(2000, 1, 18, 9, 0)
        `when`(todayGenerator.getDateTime()).thenReturn(today)
        `when`(todayGenerator.getDate()).thenReturn(today.toLocalDate())
        runBlocking {
            `when`(photoInfoRepository.getByChallenge(challenge)).thenReturn(emptyList())
            val message = usecase.execute(challenge)
            assert(!usecase.isDeactivateMessage(message))
        }
    }

    @Test
    fun alreadyDone() {
        val endDay = LocalDate.of(2000, 1,17)
        val createdAt = LocalDateTime.of(2000, 1, 14, 9, 0)
        val cheatDaysLeft = 0
        val challenge = challengeForTest(createdAt, endDay, PhotoFrequency.EVERY_DAY, null, cheatDaysLeft, null)
        val today = LocalDateTime.of(2000, 2, 1, 9, 0)
        `when`(todayGenerator.getDateTime()).thenReturn(today)
        `when`(todayGenerator.getDate()).thenReturn(today.toLocalDate())
        runBlocking {
            `when`(photoInfoRepository.getByChallenge(challenge)).thenReturn(
                listOf(
                    PhotoInfo(0, "file1", LocalDateTime.of(2000, 1, 20, 9, 0))
                )
            )
            val message = usecase.execute(challenge)
            assert(!usecase.isDeactivateMessage(message))
        }
    }

    @Test
    fun alreadyHandledCheatDays() {
        val endDay = LocalDate.of(2000, 2,1)
        val createdAt = LocalDateTime.of(2000, 1, 14, 9, 0)
        val cheatDaysLeft = 0
        val lastCheatDay = LocalDate.of(2000, 1, 15)
        val challenge = challengeForTest(createdAt, endDay, PhotoFrequency.EVERY_DAY, null, cheatDaysLeft, lastCheatDay)
        val today = LocalDateTime.of(2000, 1, 16, 9, 0)
        `when`(todayGenerator.getDateTime()).thenReturn(today)
        `when`(todayGenerator.getDate()).thenReturn(today.toLocalDate())
        runBlocking {
            `when`(photoInfoRepository.getByChallenge(challenge)).thenReturn(emptyList())
            val message = usecase.execute(challenge)
            assert(!usecase.isDeactivateMessage(message))
        }
    }

    private fun challengeForTest(createdAt: LocalDateTime, endDay: LocalDate, photoFrequency: PhotoFrequency, dayOfDeadline: DayOfWeek?, cheatDaysLeft: Int, lastCheatDay: LocalDate?): Challenge {
        return Challenge(endDay, createdAt, photoFrequency, dayOfDeadline, true, cheatDaysLeft, "title", lastCheatDay = lastCheatDay)
    }
}