package com.github.rmitsubayashi.snaporelse.usecase

import com.github.rmitsubayashi.snaporelse.model.entity.PhotoFrequency
import com.github.rmitsubayashi.snaporelse.usecase.util.TodayGenerator
import java.time.DayOfWeek
import java.time.LocalDate

class ValidateAddChallengeFormUseCase(private val todayGenerator: TodayGenerator) {

    /**
     * returns null if valid, error message if not
     * @see AddChallengeUseCase for assumptions made by this method
      */
    fun execute(title: String?, endDate: LocalDate?, photoFrequency: PhotoFrequency?, dayOfWeek: DayOfWeek?, isHardCore: Boolean?, cheatDays: Int?): String? {
        if (title == null) {
            return "empty title"
        }
        if (endDate == null) {
            return "please set a goal date"
        }

        if (photoFrequency == null) {
            return "photo frequency not set"
        }

        if (photoFrequency == PhotoFrequency.EVERY_WEEK && dayOfWeek == null) {
            return "deadline date not set"
        }

        if (isHardCore == true && photoFrequency == PhotoFrequency.WHENEVER) {
            return "cannot have a hardcore challenge with this photo frequency"
        }

        if (isHardCore == true && cheatDays == null) {
            return "cheat day not set"
        }

        if (todayGenerator.getDate() > endDate) {
            return "invalid end date"
        }

        return null
    }
}