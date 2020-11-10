package com.github.rmitsubayashi.snaporelse.model.repository

import android.graphics.Bitmap
import com.github.rmitsubayashi.snaporelse.model.entity.PhotoInfo

interface PhotoRepository {
    suspend fun get(photoInfo: PhotoInfo): Bitmap?
    /**
     * inserting is done by the request directly
     * @see com.github.rmitsubayashi.snaporelse.usecase.FireTakePhotoIntentUseCase
     */
    suspend fun delete(photoInfo: PhotoInfo): Boolean

    suspend fun update(photoInfo: PhotoInfo, bitmap: Bitmap): Boolean
}