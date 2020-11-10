package com.github.rmitsubayashi.snaporelse.usecase

import com.github.rmitsubayashi.snaporelse.model.entity.Challenge
import com.github.rmitsubayashi.snaporelse.model.repository.ChallengeRepository

class GetChallengeUseCase(private val challengeRepository: ChallengeRepository) {
    suspend fun execute(id: Int): Challenge {
        return challengeRepository.getChallenge(id)
    }
}