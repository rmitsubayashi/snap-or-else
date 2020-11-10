package com.github.rmitsubayashi.snaporelse.service

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.github.rmitsubayashi.snaporelse.MainActivity
import com.github.rmitsubayashi.snaporelse.R
import com.github.rmitsubayashi.snaporelse.usecase.ScheduleReminderUseCase


class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent == null) return
        val challengeID = intent.getIntExtra(ScheduleReminderUseCase.EXTRA_CHALLENGE_ID, -1)
        val title = intent.getStringExtra(ScheduleReminderUseCase.EXTRA_TITLE)
        val notificationIntent = Intent(context, MainActivity::class.java)
        notificationIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        // so we can directly move to the challenge details screen
        notificationIntent.putExtra(EXTRA_CHANNEL_ID, challengeID)
        val pendingIntent = PendingIntent.getActivity(
            context,
            generateUniqueRequestCode(challengeID),
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val builder = NotificationCompat.Builder(
            context,
            CHANNEL_ID
        )
            .setSmallIcon(R.drawable.ic_baseline_calendar_today_24)
            .setContentTitle("Take a snap!")
            .setContentText("A snap for \"$title\" is due soon!")
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
        with (NotificationManagerCompat.from(context)) {
            notify(generateUniqueRequestCode(challengeID), builder.build())
        }
    }

    private fun generateUniqueRequestCode(challengeID: Int): Int {
        val randomPrime = 103
        return randomPrime * challengeID
    }

    companion object {
        const val CHANNEL_ID = "challenge reminders"
        const val EXTRA_CHANNEL_ID = "challengeID"
    }
}