package com.github.rmitsubayashi.snaporelse.usecase

import com.github.rmitsubayashi.snaporelse.model.repository.GifRepository

class GetMostRecentGifUseCase(
    private val gifRepository: GifRepository
) {
    suspend fun execute(challengeID: Int): ByteArray? {
        return gifRepository.getGif(challengeID)
    }
}