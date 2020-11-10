package com.github.rmitsubayashi.snaporelse.usecase

import com.github.rmitsubayashi.snaporelse.model.entity.Challenge
import java.time.LocalTime

class UpdateReminderUseCase(
    private val scheduleReminderUseCase: ScheduleReminderUseCase,
    private val cancelReminderUseCase: CancelReminderUseCase) {

    suspend fun execute(challenge: Challenge, reminderTime: LocalTime?) {
        if (reminderTime == null) {
            cancelReminderUseCase.execute(challenge)
        } else {
            scheduleReminderUseCase.execute(challenge, reminderTime, null)
        }
    }
}