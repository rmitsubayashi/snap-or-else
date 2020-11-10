package com.github.rmitsubayashi.snaporelse.usecase

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.github.rmitsubayashi.snaporelse.model.entity.Challenge
import com.github.rmitsubayashi.snaporelse.model.entity.PhotoFrequency
import com.github.rmitsubayashi.snaporelse.model.repository.ChallengeRepository
import com.github.rmitsubayashi.snaporelse.model.repository.PhotoInfoRepository
import com.github.rmitsubayashi.snaporelse.service.AlarmReceiver
import com.github.rmitsubayashi.snaporelse.usecase.util.DateUtil
import java.time.*

class ScheduleReminderUseCase(
    private val challengeRepository: ChallengeRepository,
    private val challengePhotoInfoRepository: PhotoInfoRepository,
    private val context: Context
) {
    // there are hardcore
    suspend fun execute(challenge: Challenge, time: LocalTime, dayOfWeek: DayOfWeek?) {
        // make sure a channel exists for the notification
        createNotificationChannel(context)
        val intent = Intent(context, AlarmReceiver::class.java).let {
            intent ->
            intent.putExtra(EXTRA_CHALLENGE_ID, challenge.id)
            intent.putExtra(EXTRA_TITLE, challenge.title)
            val requestCode = generateUniqueRequestCode(challenge)
            PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val deadline = getNextNotificationDate(challenge)
        val firstTime = getFirstReminderTime(time, deadline)
        // if the user doesn't take a photo on the day of the reminder,
        // we still want to remind him the next day
        val interval = 1000 * 60 * 60 * 24L
        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            firstTime,
            interval,
            intent
        )
        challengeRepository.updateNotificationTime(challenge, time)
    }

    private suspend fun getNextNotificationDate(challenge: Challenge): LocalDateTime {
        val photos = challengePhotoInfoRepository.getByChallenge(challenge)
        val orderedPhotos = photos.sortedBy { it.createdAt }
        val createdAtDay = DateUtil.dateTimeToDate(challenge.createdAt)
        val lastCheatDay = challenge.lastCheatDay ?: createdAtDay
        val lastPhotoDay = DateUtil.dateTimeToDate(orderedPhotos.lastOrNull()?.createdAt ?: challenge.createdAt)

        val latestDay = LocalDate.ofEpochDay(
            maxOf(lastCheatDay.toEpochDay(), lastPhotoDay.toEpochDay(), createdAtDay.toEpochDay())
        )
        val daysBetween = when (challenge.photoFrequency) {
            PhotoFrequency.EVERY_DAY -> 1L
            PhotoFrequency.EVERY_WEEK -> 7L
            else -> 0L
        }
        val nextDeadlineDay = latestDay.plusDays(daysBetween)
        return LocalDateTime.of(nextDeadlineDay.plusDays(1), LocalTime.of(DateUtil.CHANGE_DATE_HOUR, 0))
    }

    private fun getFirstReminderTime(scheduledTime: LocalTime, deadline: LocalDateTime): Long {
        val zone = ZoneId.systemDefault()
        val date = if (scheduledTime.isAfter(deadline.toLocalTime())) {
            deadline.minusDays(1).toLocalDate()
        } else {
            deadline.toLocalDate()
        }
        val earliestTime = LocalDateTime.of(date, scheduledTime)
        val zoneDateTime = earliestTime.atZone(zone)
        val instant = zoneDateTime.toInstant()
        return instant.toEpochMilli()
    }

    private fun createNotificationChannel(context: Context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(AlarmReceiver.CHANNEL_ID, AlarmReceiver.CHANNEL_ID, importance)
            // Register the channel with the system
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val EXTRA_TITLE = "title"
        const val EXTRA_CHALLENGE_ID = "challengeID"

        fun generateUniqueRequestCode(challenge: Challenge): Int {
            val randomPrime = 101
            return randomPrime * challenge.id
        }
    }
}