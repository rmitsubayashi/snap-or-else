package com.github.rmitsubayashi.snaporelse.usecase

import com.github.rmitsubayashi.snaporelse.model.entity.Challenge
import com.github.rmitsubayashi.snaporelse.model.repository.GifRepository

class CreatePublicGIFUseCase(
    private val createGIFUseCase: CreateGIFUseCase,
    private val gifRepository: GifRepository
) {
    suspend fun execute(challenge: Challenge): Boolean {
        // check whether the most recent version of the gif already exists
        val requiresUpdate = gifRepository.getRequireBackgroundGIFUpdate(challenge.id)
        val gif = if (requiresUpdate) {
            // the most recent gif doesn't exist, so we need to create it
            // (if the worker hasn't run yet)
            createGIFUseCase.execute(challenge.id)
        } else {
            // if the most recent gif was created already, just grab that
            gifRepository.getGif(challenge.id)
        }
        gif ?: return false

        // we shouldn't have to save the newly created gif locally
        // because a worker should be queued to do that,
        // but save it just in case the user wants to export twice
        // simultaneously??
        if (requiresUpdate) {
            gifRepository.saveGif(challenge, gif, false)
            gifRepository.setRequireBackgroundGIFUpdate(challenge.id, false)
        }


        // probably more efficient to do Files.copy(from, to)
        // but we are abstracting the implementation of the repository
        // so the use case should not know that we save files locally
        return gifRepository.saveGif(challenge, gif, true)
    }
}