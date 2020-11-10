package com.github.rmitsubayashi.snaporelse.model.repository

import com.github.rmitsubayashi.snaporelse.model.dao.ChallengeDAO
import com.github.rmitsubayashi.snaporelse.model.entity.Challenge
import java.time.LocalDate
import java.time.LocalTime

class RoomChallengeRepository(private val challengeDAO: ChallengeDAO): ChallengeRepository {
    override suspend fun getChallenges(): List<Challenge> {
        return challengeDAO.getAll()
    }

    override suspend fun insert(challenge: Challenge): Long {
        return challengeDAO.insert(challenge)
    }

    override suspend fun remove(challenge: Challenge): Boolean {
        challengeDAO.delete(challenge)
        return true
    }

    override suspend fun getChallenge(id: Int): Challenge {
        return challengeDAO.get(id)
    }

    override suspend fun updateCheatDay(challenge: Challenge, newValue: Int): Boolean {
        challengeDAO.updateCheatDay(challenge.id, newValue)
        return true
    }

    override suspend fun updateActive(challenge: Challenge, isActive: Boolean): Boolean {
        challengeDAO.updateActive(challenge.id, isActive)
        return true
    }

    override suspend fun updateLastCheatDay(challenge: Challenge, cheatDay: LocalDate): Boolean {
        challengeDAO.updateLastCheatDay(challenge.id, cheatDay)
        return true
    }

    override suspend fun updateNotificationTime(challenge: Challenge, time: LocalTime?): Boolean {
        challengeDAO.updateNotificationTime(challenge.id, time)
        return true
    }
}