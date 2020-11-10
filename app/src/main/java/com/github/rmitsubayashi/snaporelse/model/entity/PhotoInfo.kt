package com.github.rmitsubayashi.snaporelse.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity
data class PhotoInfo (
    @ColumnInfo(name = "challenge_id") val challengeID: Int,
    // not full path. just 'something.jpg'
    @ColumnInfo(name = "file_name") val fileName: String,
    @ColumnInfo(name = "created_at") val createdAt: LocalDateTime,
    @ColumnInfo(name = "last_update") val lastUpdate: LocalDateTime = createdAt,
    @ColumnInfo(name = "active") val isActive: Boolean = true
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}