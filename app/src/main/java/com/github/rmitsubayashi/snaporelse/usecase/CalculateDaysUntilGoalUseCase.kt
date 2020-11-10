package com.github.rmitsubayashi.snaporelse.usecase

import com.github.rmitsubayashi.snaporelse.model.entity.Challenge
import com.github.rmitsubayashi.snaporelse.usecase.util.DateUtil
import com.github.rmitsubayashi.snaporelse.usecase.util.TodayGenerator
import java.time.temporal.ChronoUnit

class CalculateDaysUntilGoalUseCase(private val todayGenerator: TodayGenerator) {
    fun execute(challenge: Challenge): Int {
        val todayDateTime = todayGenerator.getDateTime()
        var todayDate = todayDateTime.toLocalDate()
        if (todayDateTime.hour < DateUtil.CHANGE_DATE_HOUR) {
            todayDate = todayDate.minusDays(1)
        }
        if (todayDate >= challenge.endDate) return 0
        // period.between gives
        val period = ChronoUnit.DAYS.between(todayDate, challenge.endDate)
        return period.toInt()
    }
}