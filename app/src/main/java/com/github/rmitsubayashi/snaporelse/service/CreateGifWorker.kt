package com.github.rmitsubayashi.snaporelse.service

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.github.rmitsubayashi.snaporelse.model.repository.GifRepository
import com.github.rmitsubayashi.snaporelse.usecase.CreateGIFUseCase
import com.github.rmitsubayashi.snaporelse.usecase.CreatePrivateGIFInBackgroundUseCase
import kotlinx.coroutines.coroutineScope
import org.koin.core.KoinComponent
import org.koin.core.inject

class CreateGifWorker(appContext: Context, workerParams: WorkerParameters): CoroutineWorker(appContext, workerParams), KoinComponent {
    private val gifRepository: GifRepository by inject()
    private val createGIFUseCase: CreateGIFUseCase by inject()

    override suspend fun doWork(): Result = coroutineScope {
        val challengeID = inputData.getInt(CreatePrivateGIFInBackgroundUseCase.INPUT_CHALLENGE_ID, -1)
        val requiresUpdate = gifRepository.getRequireBackgroundGIFUpdate(challengeID)
        if (!requiresUpdate) {
            return@coroutineScope Result.success()
        }
        // do this first to prevent duplicate work
        gifRepository.setRequireBackgroundGIFUpdate(challengeID, false)
        val byteArray = createGIFUseCase.execute(challengeID)
        if (byteArray == null) {
            // this means there are no photos
            gifRepository.setRequireBackgroundGIFUpdate(challengeID, true)
            val removed = gifRepository.removeGif(challengeID)
            return@coroutineScope if (removed) Result.success() else Result.failure()
        }

        val result = gifRepository.saveGif(challengeID, byteArray, false)
        if (!result) {
            gifRepository.setRequireBackgroundGIFUpdate(challengeID, true)
            return@coroutineScope Result.failure()
        }
        return@coroutineScope Result.success()
    }
}