package com.github.rmitsubayashi.snaporelse.model.repository

import com.github.rmitsubayashi.snaporelse.model.entity.Challenge

interface GifRepository {
    suspend fun addChallenge(challengeID: Int)

    suspend fun setRequireBackgroundGIFUpdate(challengeID: Int, required: Boolean)

    suspend fun getRequireBackgroundGIFUpdate(challengeID: Int): Boolean

    suspend fun getGif(challengeID: Int): ByteArray?

    suspend fun saveGif(challenge: Challenge, gif: ByteArray, public: Boolean): Boolean
    // with work manager, we can't pass the whole Challenge object
    suspend fun saveGif(challengeID: Int, gif: ByteArray, public: Boolean): Boolean

    // assume private because we shouldn't have to manage the user's public directories
    suspend fun removeGif(challengeID: Int): Boolean

    suspend fun setGifFrameRate(challengeID: Int, millis: Int)

    suspend fun getGifFrameRate(challengeID: Int): Int
}