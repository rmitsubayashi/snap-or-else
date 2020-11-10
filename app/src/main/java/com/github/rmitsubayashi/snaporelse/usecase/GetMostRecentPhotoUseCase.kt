package com.github.rmitsubayashi.snaporelse.usecase

import com.github.rmitsubayashi.snaporelse.model.entity.Challenge
import com.github.rmitsubayashi.snaporelse.model.repository.PhotoInfoRepository
import com.github.rmitsubayashi.snaporelse.model.repository.PhotoRepository

class GetMostRecentPhotoUseCase(private val photoInfoRepository: PhotoInfoRepository, private val photoRepository: PhotoRepository) {
    suspend fun execute(challenge: Challenge): String? {
        val photoInfo = photoInfoRepository.getByChallenge(challenge).filter { it.isActive }
        val lastPhoto = photoInfo.maxBy { it.createdAt }
        lastPhoto ?: return null

        return lastPhoto.fileName
    }
}