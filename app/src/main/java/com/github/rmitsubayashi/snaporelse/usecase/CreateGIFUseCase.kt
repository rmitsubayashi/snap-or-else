package com.github.rmitsubayashi.snaporelse.usecase

import android.graphics.Bitmap
import com.github.rmitsubayashi.snaporelse.model.repository.GifRepository
import com.github.rmitsubayashi.snaporelse.model.repository.PhotoInfoRepository
import com.github.rmitsubayashi.snaporelse.model.repository.PhotoRepository
import com.github.rmitsubayashi.snaporelse.usecase.util.AnimatedGifEncoder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

class CreateGIFUseCase(
    private val photoInfoRepository: PhotoInfoRepository,
    private val photoRepository: PhotoRepository,
    private val gifRepository: GifRepository
) {
    // returns null only if there are no photos
    suspend fun execute(challengeID: Int): ByteArray? {
        val photoInfo = photoInfoRepository.getByChallengeID(challengeID).filter { it.isActive }
        if (photoInfo.isEmpty()) return null
        val ordered = photoInfo.sortedBy { it.createdAt }
        val bitmaps = ordered.mapNotNull { photoRepository.get(it) }
        val frameRate = gifRepository.getGifFrameRate(challengeID)
        return bitmapsToByteArray(bitmaps, frameRate)
    }

    private suspend fun bitmapsToByteArray(bitmaps: List<Bitmap>, frameRate: Int): ByteArray {
        return withContext(Dispatchers.IO) {
            val gifEncoder = AnimatedGifEncoder()
            gifEncoder.setFrameRate(frameRate.toFloat())
            val stream = ByteArrayOutputStream()
            gifEncoder.start(stream)
            for (bitmap in bitmaps) {
                gifEncoder.addFrame(bitmap)
            }
            gifEncoder.finish()
            stream.toByteArray()
        }
    }
}