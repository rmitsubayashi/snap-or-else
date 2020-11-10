package com.github.rmitsubayashi.snaporelse.usecase

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.github.rmitsubayashi.snaporelse.model.entity.Challenge
import com.github.rmitsubayashi.snaporelse.model.repository.ChallengeRepository
import com.github.rmitsubayashi.snaporelse.service.AlarmReceiver
import com.github.rmitsubayashi.snaporelse.usecase.ScheduleReminderUseCase.Companion.generateUniqueRequestCode

class CancelReminderUseCase(private val challengeRepository: ChallengeRepository, private val context: Context) {
    suspend fun execute(challenge: Challenge) {
        // cancel the alarm that repeats
        val intent = Intent(context, AlarmReceiver::class.java).let {
            intent ->
            intent.putExtra(ScheduleReminderUseCase.EXTRA_CHALLENGE_ID, challenge.id)
            intent.putExtra(ScheduleReminderUseCase.EXTRA_TITLE, challenge.title)
            val requestCode = generateUniqueRequestCode(challenge)
            PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_NO_CREATE)
        }
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (intent!= null) {
            alarmManager.cancel(intent)
            challengeRepository.updateNotificationTime(challenge, null)
        }
    }
}