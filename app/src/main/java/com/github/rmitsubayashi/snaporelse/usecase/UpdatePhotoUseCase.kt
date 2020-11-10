package com.github.rmitsubayashi.snaporelse.usecase

import android.graphics.Bitmap
import com.github.rmitsubayashi.snaporelse.model.entity.PhotoInfo
import com.github.rmitsubayashi.snaporelse.model.repository.GifRepository
import com.github.rmitsubayashi.snaporelse.model.repository.PhotoInfoRepository
import com.github.rmitsubayashi.snaporelse.model.repository.PhotoRepository
import com.github.rmitsubayashi.snaporelse.usecase.util.TodayGenerator

class UpdatePhotoUseCase(private val photoRepository: PhotoRepository,
                         private val photoInfoRepository: PhotoInfoRepository,
                         private val gifRepository: GifRepository,
                         private val createPrivateGIFInBackgroundUseCase: CreatePrivateGIFInBackgroundUseCase,
                         private val todayGenerator: TodayGenerator) {
    suspend fun execute(photoInfo: PhotoInfo, newBitmap: Bitmap): Boolean {
        val success = photoRepository.update(photoInfo, newBitmap)
        if (!success) return false
        photoInfoRepository.setLastUpdate(photoInfo, todayGenerator.getDateTime())
        gifRepository.setRequireBackgroundGIFUpdate(photoInfo.challengeID, true)
        createPrivateGIFInBackgroundUseCase.execute(photoInfo.challengeID)
        return true
    }
}