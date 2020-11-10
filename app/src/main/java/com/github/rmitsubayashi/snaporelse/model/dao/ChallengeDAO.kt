package com.github.rmitsubayashi.snaporelse.model.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.github.rmitsubayashi.snaporelse.model.entity.Challenge
import java.time.LocalDate
import java.time.LocalTime

@Dao
interface ChallengeDAO {
    @Query("SELECT * from challenge WHERE isActive = 1")
    suspend fun getAll(): List<Challenge>

    @Query("SELECT * from challenge WHERE id = :id")
    suspend fun get(id: Int): Challenge

    @Insert
    suspend fun insert(challenge: Challenge): Long

    @Delete
    suspend fun delete(challenge: Challenge)

    @Query("UPDATE challenge SET cheatDays = :newValue WHERE id = :challengeID")
    suspend fun updateCheatDay(challengeID: Int, newValue: Int)

    @Query("UPDATE challenge SET isActive = :active WHERE id = :challengeID")
    suspend fun updateActive(challengeID: Int, active: Boolean)

    @Query("UPDATE challenge SET lastCheatDay = :date WHERE id = :challengeID")
    suspend fun updateLastCheatDay(challengeID: Int, date: LocalDate)

    @Query("UPDATE challenge SET notificationTime = :time WHERE id = :challengeID")
    suspend fun updateNotificationTime(challengeID: Int, time: LocalTime?)
}