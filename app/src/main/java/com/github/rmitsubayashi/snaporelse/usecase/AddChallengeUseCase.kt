package com.github.rmitsubayashi.snaporelse.usecase

import com.github.rmitsubayashi.snaporelse.model.entity.Challenge
import com.github.rmitsubayashi.snaporelse.model.entity.PhotoFrequency
import com.github.rmitsubayashi.snaporelse.model.repository.ChallengeRepository
import com.github.rmitsubayashi.snaporelse.model.repository.GifRepository
import com.github.rmitsubayashi.snaporelse.usecase.util.TodayGenerator
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class AddChallengeUseCase(
    private val challengeRepository: ChallengeRepository,
    private val gifRepository: GifRepository,
    private val todayGenerator: TodayGenerator
) {
    /**
     * @see ValidateAddChallengeFormUseCase for what we can assume
     */
    suspend fun execute(title: String?, endDate: LocalDate?, photoFrequency: PhotoFrequency?, dayOfDeadline: DayOfWeek?, isHardCore: Boolean?, cheatDays: Int?, notificationTime: LocalTime?): Challenge {
        val startDateTime = todayGenerator.getDateTime()
        // make sure to check assumptions on what we can safely cast
        val toSaveCheatDay = if (isHardCore == true) {
            cheatDays!!
        } else {
            0
        }
        val toSaveDayOfDeadline = if (photoFrequency!! == PhotoFrequency.EVERY_WEEK) {
            dayOfDeadline
        } else {
            null
        }
        val challenge = Challenge(
            endDate!!,
            startDateTime,
            photoFrequency,
            toSaveDayOfDeadline,
            isHardCore ?: false,
            toSaveCheatDay,
            title!!,
            notificationTime
        )
        val newID = challengeRepository.insert(challenge)
        val updatedChallenge = challenge.copy().apply { id = newID.toInt() }
        gifRepository.addChallenge(updatedChallenge.id)
        return updatedChallenge
    }
}