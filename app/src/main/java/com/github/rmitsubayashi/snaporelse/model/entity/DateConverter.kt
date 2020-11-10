package com.github.rmitsubayashi.snaporelse.model.entity

import androidx.room.TypeConverter
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class DateConverter {
    @TypeConverter
    fun stringToDateTime(value: String?): LocalDateTime? {
        return value?.let { LocalDateTime.parse(value) }
    }

    @TypeConverter
    fun dateTimeToString(dateTime: LocalDateTime?): String? {
        return dateTime?.toString()
    }

    @TypeConverter
    fun stringToDate(value: String?): LocalDate? {
        return value?.let { LocalDate.parse(value) }
    }

    @TypeConverter
    fun dateToString(date: LocalDate?): String? {
        return date?.toString()
    }

    @TypeConverter
    fun stringToTime(value: String?): LocalTime? {
        return value?.let { LocalTime.parse(value) }
    }

    @TypeConverter
    fun timeToString(time: LocalTime?): String? {
        return time?.toString()
    }

    @TypeConverter
    fun intToDayOfWeek(value: Int?): DayOfWeek? {
        return value?.let { DayOfWeek.of(value) }
    }

    @TypeConverter
    fun dayOfWeekToInt(dayOfWeek: DayOfWeek?): Int? {
        return dayOfWeek?.value
    }
}