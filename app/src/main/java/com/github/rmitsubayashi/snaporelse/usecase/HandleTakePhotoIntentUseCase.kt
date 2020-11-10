package com.github.rmitsubayashi.snaporelse.usecase

import android.app.Activity
import com.github.rmitsubayashi.snaporelse.model.entity.PhotoInfo
import com.github.rmitsubayashi.snaporelse.model.repository.GifRepository
import com.github.rmitsubayashi.snaporelse.model.repository.PhotoInfoRepository

class HandleTakePhotoIntentUseCase(
    private val photoInfoRepository: PhotoInfoRepository,
    private val gifRepository: GifRepository,
    private val reorientBitmapUseCase: ReorientBitmapUseCase,
    private val createPrivateGIFInBackgroundUseCase: CreatePrivateGIFInBackgroundUseCase
) {
    private val successMessage = "Success!"
    fun isSuccessMessage(message: String) = message == successMessage
    suspend fun execute(requestCode: Int, resultCode: Int, photoInfo: PhotoInfo?): String? {
        if (requestCode != FireTakePhotoIntentUseCase.REQUEST_CODE) return null
        if (resultCode != Activity.RESULT_OK) return "Something went wrong"
        photoInfo?.let {
            photoInfoRepository.insert(it)
            reorientBitmapUseCase.execute(it)
            gifRepository.setRequireBackgroundGIFUpdate(it.challengeID, true)
            createPrivateGIFInBackgroundUseCase.execute(it.challengeID)

            return successMessage
        }
        return "Something went wrong"
    }
}