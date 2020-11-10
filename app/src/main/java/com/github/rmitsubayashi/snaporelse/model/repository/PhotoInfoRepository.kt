package com.github.rmitsubayashi.snaporelse.model.repository

import com.github.rmitsubayashi.snaporelse.model.entity.Challenge
import com.github.rmitsubayashi.snaporelse.model.entity.PhotoInfo
import java.time.LocalDateTime

interface PhotoInfoRepository {
    suspend fun getByChallenge(challenge: Challenge): List<PhotoInfo>
    suspend fun getByChallengeID(challengeID: Int): List<PhotoInfo>
    suspend fun get(infoID: Int): PhotoInfo
    suspend fun insert(photoInfo: PhotoInfo): Boolean
    suspend fun setActive(photoInfo: PhotoInfo, active: Boolean): Boolean
    suspend fun setLastUpdate(photoInfo: PhotoInfo, lastUpdate: LocalDateTime): Boolean
}