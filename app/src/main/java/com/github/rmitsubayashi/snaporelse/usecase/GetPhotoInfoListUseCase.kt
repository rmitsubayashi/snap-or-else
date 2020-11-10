package com.github.rmitsubayashi.snaporelse.usecase

import com.github.rmitsubayashi.snaporelse.model.entity.PhotoInfo
import com.github.rmitsubayashi.snaporelse.model.repository.PhotoInfoRepository

class GetPhotoInfoListUseCase(
    private val photoInfoRepository: PhotoInfoRepository
) {
    suspend fun execute(challengeID: Int): List<PhotoInfo> {
        val list = photoInfoRepository.getByChallengeID(challengeID)
        return list.filter { it.isActive }.sortedBy { it.createdAt }
    }
}