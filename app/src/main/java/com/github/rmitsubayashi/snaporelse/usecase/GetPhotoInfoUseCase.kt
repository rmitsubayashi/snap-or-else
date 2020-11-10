package com.github.rmitsubayashi.snaporelse.usecase

import com.github.rmitsubayashi.snaporelse.model.entity.PhotoInfo
import com.github.rmitsubayashi.snaporelse.model.repository.PhotoInfoRepository

class GetPhotoInfoUseCase(private val photoInfoRepository: PhotoInfoRepository) {
   suspend fun execute(infoID: Int): PhotoInfo {
        return photoInfoRepository.get(infoID)
    }
}