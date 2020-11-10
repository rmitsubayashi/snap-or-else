package com.github.rmitsubayashi.snaporelse.usecase.util

import java.time.LocalDate
import java.time.LocalDateTime

// Basically to help mocking during testing
open class TodayGenerator {
    open fun getDate(): LocalDate = LocalDate.now()

    open fun getDateTime(): LocalDateTime = LocalDateTime.now()
}