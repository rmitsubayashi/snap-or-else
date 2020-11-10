package com.github.rmitsubayashi.snaporelse.usecase

import com.github.rmitsubayashi.snaporelse.model.entity.Challenge
import com.github.rmitsubayashi.snaporelse.model.entity.PhotoFrequency
import com.github.rmitsubayashi.snaporelse.usecase.util.TodayGenerator
import org.junit.Assert.assertEquals

import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import java.time.LocalDate
import java.time.LocalDateTime

class CalculateDaysUntilGoalUseCaseTest {
    private lateinit var usecase: CalculateDaysUntilGoalUseCase
    @Mock
    private lateinit var todayGenerator: TodayGenerator

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        usecase = CalculateDaysUntilGoalUseCase(todayGenerator)
    }

    @Test
    fun today() {
        val now = LocalDateTime.of(2000, 1, 14, 9, 0)
        `when`(todayGenerator.getDateTime()).thenReturn(now)
        val challenge = challengeWithEndDate(LocalDate.of(2000,1,14))
        val result = usecase.execute(challenge)
        assert(result == 0)
    }

    @Test
    fun tomorrow() {
        val now = LocalDateTime.of(2000, 1, 14, 9, 0)
        `when`(todayGenerator.getDateTime()).thenReturn(now)
        val challenge = challengeWithEndDate(LocalDate.of(2000,1,15))
        val result = usecase.execute(challenge)
        assert(result == 1)
    }

    @Test
    fun yesterday() {
        val now = LocalDateTime.of(2000, 1, 14, 9, 0)
        `when`(todayGenerator.getDateTime()).thenReturn(now)
        val challenge = challengeWithEndDate(LocalDate.of(2000,1,13))
        val result = usecase.execute(challenge)
        assert(result == 0)
    }

    @Test
    fun lateNight() {
        val now = LocalDateTime.of(2000, 1, 14, 1, 0)
        `when`(todayGenerator.getDateTime()).thenReturn(now)
        val challenge = challengeWithEndDate(LocalDate.of(2000,1,14))
        val result = usecase.execute(challenge)
        assert(result == 1)
    }

    @Test
    fun month() {
        val now = LocalDateTime.of(2000, 1, 14, 9, 0)
        `when`(todayGenerator.getDateTime()).thenReturn(now)
        val challenge = challengeWithEndDate(LocalDate.of(2000,2,14))
        val result = usecase.execute(challenge)
        assertEquals(31, result)
    }

    private fun challengeWithEndDate(date: LocalDate): Challenge {
        return Challenge(
            date, LocalDateTime.now(), PhotoFrequency.EVERY_DAY, null, false, 0, ""
        )
    }
}