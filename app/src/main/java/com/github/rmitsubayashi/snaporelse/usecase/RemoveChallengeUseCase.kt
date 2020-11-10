package com.github.rmitsubayashi.snaporelse.usecase

import com.github.rmitsubayashi.snaporelse.model.entity.Challenge
import com.github.rmitsubayashi.snaporelse.model.repository.ChallengeRepository

class RemoveChallengeUseCase(private val challengeRepository: ChallengeRepository) {
    suspend fun execute(challenge: Challenge) {
        // don't permanently remove (for now)
        challengeRepository.updateActive(challenge, false)
    }
}