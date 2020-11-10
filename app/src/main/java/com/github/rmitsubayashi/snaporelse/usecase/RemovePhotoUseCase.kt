package com.github.rmitsubayashi.snaporelse.usecase

import com.github.rmitsubayashi.snaporelse.model.entity.PhotoInfo
import com.github.rmitsubayashi.snaporelse.model.repository.GifRepository
import com.github.rmitsubayashi.snaporelse.model.repository.PhotoInfoRepository
import com.github.rmitsubayashi.snaporelse.model.repository.PhotoRepository

class RemovePhotoUseCase(
    private val photoRepository: PhotoRepository,
    private val photoInfoRepository: PhotoInfoRepository,
    private val gifRepository: GifRepository,
    private val gifInBackgroundUseCase: CreatePrivateGIFInBackgroundUseCase
    ) {
    suspend fun execute(photoInfo: PhotoInfo): Boolean {
        val success = photoRepository.delete(photoInfo)
        if (!success) return false
        // we can't delete the record because it's used to calculate deadlines
        photoInfoRepository.setActive(photoInfo, false)
        // we need to update the gif
        gifRepository.setRequireBackgroundGIFUpdate(photoInfo.challengeID, true)
        gifInBackgroundUseCase.execute(photoInfo.challengeID)
        return true
    }
}