package com.github.rmitsubayashi.snaporelse.usecase

import android.graphics.Bitmap
import com.github.rmitsubayashi.snaporelse.model.entity.PhotoInfo
import com.github.rmitsubayashi.snaporelse.model.repository.PhotoRepository

class GetPhotoUseCase(private val photoRepository: PhotoRepository) {
    suspend fun execute(photoInfo: PhotoInfo): Bitmap? {
        return photoRepository.get(photoInfo)
    }
}