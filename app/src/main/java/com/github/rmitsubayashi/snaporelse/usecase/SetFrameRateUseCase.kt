package com.github.rmitsubayashi.snaporelse.usecase

import com.github.rmitsubayashi.snaporelse.model.repository.GifRepository

class SetFrameRateUseCase(
    private val gifRepository: GifRepository,
    private val createPrivateGIFInBackgroundUseCase: CreatePrivateGIFInBackgroundUseCase
) {
    suspend fun execute(challengeID: Int, newFrameRate: Int) {
        val currentFrameRate = gifRepository.getGifFrameRate(challengeID)
        if (currentFrameRate == newFrameRate) {
            // no need to waste resources creating the same gif
            return
        }
        gifRepository.setGifFrameRate(challengeID, newFrameRate)
        gifRepository.setRequireBackgroundGIFUpdate(challengeID, true)
        createPrivateGIFInBackgroundUseCase.execute(challengeID)
    }
}