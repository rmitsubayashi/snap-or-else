package com.github.rmitsubayashi.snaporelse.model.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.github.rmitsubayashi.snaporelse.model.entity.PhotoInfo
import java.time.LocalDateTime

@Dao
interface ChallengePhotoInfoDAO {
    @Query("SELECT * FROM photoinfo WHERE id = :id LIMIT 1")
    suspend fun get(id: Int): PhotoInfo

    @Query("SELECT * FROM photoinfo WHERE challenge_id = :challengeID")
    suspend fun getByChallengeID(challengeID: Int): List<PhotoInfo>

    @Insert
    suspend fun insert(vararg photoInfo: PhotoInfo)

    @Query("UPDATE photoinfo SET active = :isActive WHERE id = :id")
    suspend fun setActive(id: Int, isActive: Boolean)

    @Query("UPDATE photoinfo SET last_update = :lastUpdate WHERE id = :id")
    suspend fun setLastUpdate(id: Int, lastUpdate: LocalDateTime)
}