package com.github.rmitsubayashi.snaporelse.usecase

import com.github.rmitsubayashi.snaporelse.model.entity.Challenge
import com.github.rmitsubayashi.snaporelse.model.entity.PhotoFrequency
import com.github.rmitsubayashi.snaporelse.model.repository.ChallengeRepository
import com.github.rmitsubayashi.snaporelse.model.repository.PhotoInfoRepository
import com.github.rmitsubayashi.snaporelse.usecase.util.DateUtil
import com.github.rmitsubayashi.snaporelse.usecase.util.TodayGenerator
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import java.time.Period
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters

class CheckUnsuccessfulChallengeUseCase(private val challengeRepository: ChallengeRepository,
                                        private val getNextPhotoDeadlineUseCase: GetNextPhotoDeadlineUseCase,
                                        private val todayGenerator: TodayGenerator
) {
    private val deactivateMessage = "You're out of cheat days!"
    // null if nothing is wrong
    suspend fun execute(challenge: Challenge): String? {
        if (!challenge.isHardCore || challenge.photoFrequency == PhotoFrequency.WHENEVER) return null
        val deadline = getNextPhotoDeadlineUseCase.execute(challenge)
        deadline ?: return null
        // deadline is exactly CHANGE_DATE_HOUR, so dateTimeToDate() will not move it back a day.
        // we need to do this manually
        val deadlineDay = DateUtil.dateTimeToDate(deadline).minusDays(1)
        // when to start counting cheat days.
        // if the user has called this method before but still hasn't taken a photo,
        // he should start off from the last time he called this method
        val cheatDayStart = if (challenge.lastCheatDay != null && challenge.lastCheatDay >= deadlineDay) {
            // the last cheat day is covered, so we want to start from the following day
            challenge.lastCheatDay.plusDays(1)
        } else {
            deadlineDay
        }
        val today = DateUtil.dateTimeToDate(todayGenerator.getDateTime())
        var cheatDays = ChronoUnit.DAYS.between(cheatDayStart, today)
        // the user checked in before the next deadline
        if (cheatDays <= 0) {
            return null
        }
        // need to check if the user met his end date before all his cheat days ran out
        if (today > challenge.endDate) {
            val daysAfterEndDate = ChronoUnit.DAYS.between(challenge.endDate, today)
            cheatDays -= daysAfterEndDate
        }

        val newCheatDayValue = challenge.cheatDays - cheatDays
        challengeRepository.updateCheatDay(challenge, newCheatDayValue.toInt())
        if (newCheatDayValue < 0) {
            challengeRepository.updateActive(challenge, false)
            return deactivateMessage
        }
        val newCheatDate = DateUtil.dateTimeToDate(todayGenerator.getDateTime())
        challengeRepository.updateLastCheatDay(challenge, newCheatDate)
        return "You have lost $cheatDays cheat days :("
    }

    fun isDeactivateMessage(message: String?) = message == deactivateMessage
}