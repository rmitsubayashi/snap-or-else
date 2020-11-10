package com.github.rmitsubayashi.snaporelse.usecase

import com.github.rmitsubayashi.snaporelse.model.repository.GifRepository

class GetCurrentFrameRateUseCase(private val gifRepository: GifRepository) {
    suspend fun execute(challengeID: Int): Int {
        return gifRepository.getGifFrameRate(challengeID)
    }
}