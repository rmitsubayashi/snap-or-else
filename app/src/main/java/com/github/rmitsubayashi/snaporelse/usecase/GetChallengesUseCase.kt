package com.github.rmitsubayashi.snaporelse.usecase

import com.github.rmitsubayashi.snaporelse.model.entity.Challenge
import com.github.rmitsubayashi.snaporelse.model.repository.ChallengeRepository

class GetChallengesUseCase(private val challengeRepository: ChallengeRepository) {
    suspend fun execute(): List<Challenge> {
        return challengeRepository.getChallenges()
    }
}