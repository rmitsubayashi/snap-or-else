package com.github.rmitsubayashi.snaporelse.model.repository

import android.content.Context
import android.media.MediaScannerConnection
import android.os.Environment
import com.github.rmitsubayashi.snaporelse.model.dao.GifSettingsDAO
import com.github.rmitsubayashi.snaporelse.model.entity.Challenge
import com.github.rmitsubayashi.snaporelse.model.entity.GifSettings
import com.github.rmitsubayashi.snaporelse.usecase.util.TodayGenerator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Files
import java.time.format.DateTimeFormatter

class LocalAndRoomGifRepository(
    private val gifSettingsDAO: GifSettingsDAO,
    private val challengeRepository: ChallengeRepository,
    private val context: Context,
    private val todayGenerator: TodayGenerator
): GifRepository {
    override suspend fun addChallenge(challengeID: Int) {
        // this looks good for me :)
        val frameRate = 5
        // we need to mark requiresUpdate as true so the first call will always create a gif
        val requiresUpdate = true
        val gifUpdate = GifSettings(challengeID, requiresUpdate, frameRate)
        gifSettingsDAO.insert(gifUpdate)
    }

    override suspend fun setRequireBackgroundGIFUpdate(challengeID: Int, required: Boolean) {
        gifSettingsDAO.setRequiresUpdate(challengeID, required)
    }

    override suspend fun getRequireBackgroundGIFUpdate(challengeID: Int): Boolean {
        val gifSettings = gifSettingsDAO.getFromChallengeID(challengeID)
        return gifSettings.requiresUpdate
    }

    override suspend fun getGif(challengeID: Int): ByteArray? {
        val privatePath = generatePrivateFilePath(challengeID)
        val file = File(privatePath)
        if (file.length() == 0L) {
            // file is either empty or doesn't exist
            return null
        }
        return withContext(Dispatchers.IO) {
            Files.readAllBytes(file.toPath())
        }
    }

    override suspend fun saveGif(challenge: Challenge, gif: ByteArray, public: Boolean): Boolean {
        val filePath = if (public) {
            generatePublicFilePath(challenge)
        } else {
            generatePrivateFilePath(challenge.id)
        }

        return withContext(Dispatchers.IO) {
            try {
                val outputStream = FileOutputStream(filePath)
                outputStream.write(gif)
                outputStream.close()
            } catch (exception: Exception) {
                exception.printStackTrace()
                return@withContext false
            }

            if (public) {
                // scan so the Android device recognizes a new file
                MediaScannerConnection.scanFile(
                    context,
                    arrayOf(filePath),
                    null,
                    null
                )
            }
            true
        }
    }

    override suspend fun saveGif(challengeID: Int, gif: ByteArray, public: Boolean): Boolean {
        val challenge = challengeRepository.getChallenge(challengeID)
        return saveGif(challenge, gif, public)
    }

    private fun generatePrivateFilePath(challengeID: Int): String {
        val path = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val fileName = "recent_gif_$challengeID"
        return "${path?.absolutePath}/$fileName.gif"
    }
    private fun generatePublicFilePath(challenge: Challenge): String {
        // DIRECTORY_PICTURES doesn't show on Google Photos on emulator
        val path =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")
        val startDate = challenge.createdAt.format(dateFormatter)
        val endDate = todayGenerator.getDate().format(dateFormatter)
        return "${path.absolutePath}/${startDate}_$endDate.gif"
    }

    override suspend fun removeGif(challengeID: Int): Boolean {
        val path = generatePrivateFilePath(challengeID)
        return withContext(Dispatchers.IO) {
            val file = File(path)
            file.delete()
        }
    }

    override suspend fun setGifFrameRate(challengeID: Int, fps: Int) {
        gifSettingsDAO.setFrameRate(challengeID, fps)
    }

    override suspend fun getGifFrameRate(challengeID: Int): Int {
        val settings = gifSettingsDAO.getFromChallengeID(challengeID)
        return settings.frameRate
    }
}