package com.github.rmitsubayashi.snaporelse.model.repository

import com.github.rmitsubayashi.snaporelse.model.dao.ChallengePhotoInfoDAO
import com.github.rmitsubayashi.snaporelse.model.entity.Challenge
import com.github.rmitsubayashi.snaporelse.model.entity.PhotoInfo
import java.time.LocalDateTime

class RoomPhotoInfoRepository(private val challengePhotoInfoDAO: ChallengePhotoInfoDAO): PhotoInfoRepository {
    override suspend fun getByChallenge(challenge: Challenge): List<PhotoInfo> {
        return challengePhotoInfoDAO.getByChallengeID(challenge.id)
    }

    override suspend fun getByChallengeID(challengeID: Int): List<PhotoInfo> {
        return challengePhotoInfoDAO.getByChallengeID(challengeID)
    }

    override suspend fun get(infoID: Int): PhotoInfo {
        return challengePhotoInfoDAO.get(infoID)
    }

    override suspend fun insert(photoInfo: PhotoInfo): Boolean {
        challengePhotoInfoDAO.insert(photoInfo)
        return true
    }

    override suspend fun setActive(photoInfo: PhotoInfo, active: Boolean): Boolean {
        challengePhotoInfoDAO.setActive(photoInfo.id, active)
        return true
    }

    override suspend fun setLastUpdate(photoInfo: PhotoInfo, lastUpdate: LocalDateTime): Boolean {
        challengePhotoInfoDAO.setLastUpdate(photoInfo.id, lastUpdate)
        return true
    }
}