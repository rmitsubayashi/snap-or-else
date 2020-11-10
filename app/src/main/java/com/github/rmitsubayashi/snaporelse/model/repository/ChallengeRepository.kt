package com.github.rmitsubayashi.snaporelse.model.repository

import com.github.rmitsubayashi.snaporelse.model.entity.Challenge
import java.time.LocalDate
import java.time.LocalTime

interface ChallengeRepository {
    suspend fun getChallenges(): List<Challenge>

    suspend fun insert(challenge: Challenge): Long

    suspend fun remove(challenge: Challenge): Boolean

    suspend fun getChallenge(id: Int): Challenge

    suspend fun updateCheatDay(challenge: Challenge, newValue: Int): Boolean

    suspend fun updateActive(challenge: Challenge, isActive: Boolean): Boolean

    suspend fun updateLastCheatDay(challenge: Challenge, cheatDay: LocalDate): Boolean

    suspend fun updateNotificationTime(challenge: Challenge, time: LocalTime?): Boolean
}