package com.github.rmitsubayashi.snaporelse.usecase.util

import java.time.LocalDate
import java.time.LocalDateTime

object DateUtil {
    const val CHANGE_DATE_HOUR = 4
    // business logic. when we cut the time at night as 'today'
    fun dateTimeToDate(dateTime: LocalDateTime): LocalDate {
        // if ay time before 4 A.M., we consider it the day before
        val adjustedDateTime = if (dateTime.hour < CHANGE_DATE_HOUR) {
            // guarantee the day will move back.
            // the resulting hour doesn't matter
            dateTime.minusHours(CHANGE_DATE_HOUR.toLong())
        } else {
            dateTime
        }
        return LocalDate.from(adjustedDateTime)
    }
}