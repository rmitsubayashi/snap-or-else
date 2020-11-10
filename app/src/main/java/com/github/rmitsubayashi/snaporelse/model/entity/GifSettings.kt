package com.github.rmitsubayashi.snaporelse.model.entity

import androidx.room.*

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Challenge::class,
            parentColumns = ["id"],
            childColumns = ["challengeID"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value=["challengeID"])
    ]
)
data class GifSettings (
    @ColumnInfo(name = "challengeID") val challengeID: Int,
    @ColumnInfo(name = "requiresUpdate") val requiresUpdate: Boolean,
    // fps
    @ColumnInfo(name = "frameRate") val frameRate: Int
) {
    @PrimaryKey(autoGenerate = true)
    var id : Int = 0
}