package com.github.rmitsubayashi.snaporelse.model.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.github.rmitsubayashi.snaporelse.model.entity.GifSettings

@Dao
interface GifSettingsDAO {
    @Insert
    suspend fun insert(gifSettings: GifSettings)

    @Query("SELECT * FROM gifsettings WHERE challengeID = :challengeID")
    suspend fun getFromChallengeID(challengeID: Int): GifSettings

    @Query("UPDATE gifsettings SET requiresUpdate = :requiresUpdate WHERE challengeID = :challengeID")
    suspend fun setRequiresUpdate(challengeID: Int, requiresUpdate: Boolean)

    @Query("UPDATE gifsettings SET frameRate = :frameRate WHERE challengeID = :challengeID")
    suspend fun setFrameRate(challengeID: Int, frameRate: Int)
}