package com.github.rmitsubayashi.snaporelse.usecase

import com.github.rmitsubayashi.snaporelse.model.entity.Challenge
import com.github.rmitsubayashi.snaporelse.model.entity.PhotoFrequency
import com.github.rmitsubayashi.snaporelse.model.repository.PhotoInfoRepository
import com.github.rmitsubayashi.snaporelse.usecase.util.DateUtil
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.TemporalAdjuster
import java.time.temporal.TemporalAdjusters

class GetNextPhotoDeadlineUseCase(private val challengePhotoInfoRepository: PhotoInfoRepository) {
    suspend fun execute(challenge: Challenge): LocalDateTime? {
        if (challenge.photoFrequency == PhotoFrequency.WHENEVER) return null
        val photos = challengePhotoInfoRepository.getByChallenge(challenge)
        val orderedPhotos = photos.sortedBy { it.createdAt }
        val createdAtDay = DateUtil.dateTimeToDate(challenge.createdAt)
        val lastPhotoDay = DateUtil.dateTimeToDate(orderedPhotos.lastOrNull()?.createdAt ?: challenge.createdAt)

        val latestDay = LocalDate.ofEpochDay(
            maxOf(lastPhotoDay.toEpochDay(), createdAtDay.toEpochDay())
        )
        val nextDeadline = when (challenge.photoFrequency) {
            PhotoFrequency.EVERY_DAY -> latestDay.plusDays(1)
            PhotoFrequency.EVERY_WEEK -> {
                latestDay.with(TemporalAdjusters.next(challenge.dayOfDeadline))
            }
            else -> return null
        }
        return LocalDateTime.of(nextDeadline.plusDays(1), LocalTime.of(DateUtil.CHANGE_DATE_HOUR, 0))
    }
}