package com.github.rmitsubayashi.snaporelse.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@Entity
data class Challenge(
    // the day the user has set as their goal for the challenge
    @ColumnInfo(name = "endDate") val endDate: LocalDate,
    @ColumnInfo(name = "createdAt") val createdAt: LocalDateTime,
    // how often the user should take a picture
    @ColumnInfo(name = "pictureFrequency") val photoFrequency: PhotoFrequency,
    // if the photo frequency is weekly, the user will set a day of week to be the deadline
    @ColumnInfo(name = "weekDayOfDeadline") var dayOfDeadline: DayOfWeek?,
    // in hardcore mode, the user's photos are deleted if he misses a day
    @ColumnInfo(name = "isHardCore") val isHardCore: Boolean,
    // for hardcore mode
    @ColumnInfo(name = "cheatDays") val cheatDays: Int,
    // the title of the challenge, like "30 days to touch my toes"
    @ColumnInfo(name = "title") val title: String,
    // the date will be determined by the next deadline (if there is no deadline there are no notifications)
    @ColumnInfo(name = "notificationTime") val notificationTime: LocalTime? = null,
    @ColumnInfo(name = "lastCheatDay") val lastCheatDay: LocalDate? = null,
    @ColumnInfo(name = "isActive") var isActive: Boolean = true
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}