package com.github.rmitsubayashi.snaporelse.usecase

import android.content.Context
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.github.rmitsubayashi.snaporelse.service.CreateGifWorker

class CreatePrivateGIFInBackgroundUseCase(
    private val context: Context
) {
    fun execute(challengeID: Int) {
        val workRequest = OneTimeWorkRequestBuilder<CreateGifWorker>()
            .setInputData(
                workDataOf(INPUT_CHALLENGE_ID to challengeID)
            )
            .build()
        WorkManager.getInstance(context.applicationContext).enqueue(workRequest)
    }

    companion object {
        const val INPUT_CHALLENGE_ID = "challenge_id"
    }
}