package com.github.rmitsubayashi.snaporelse.model

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.github.rmitsubayashi.snaporelse.model.dao.ChallengeDAO
import com.github.rmitsubayashi.snaporelse.model.dao.ChallengePhotoInfoDAO
import com.github.rmitsubayashi.snaporelse.model.dao.GifSettingsDAO
import com.github.rmitsubayashi.snaporelse.model.entity.*

@Database(entities = [Challenge::class, PhotoInfo::class, GifSettings::class], version = 1)
@TypeConverters(DateConverter::class, PhotoFrequencyConverter::class)
abstract class AppDB: RoomDatabase() {
    abstract fun challengeDao(): ChallengeDAO
    abstract fun challengePhotoInfoDao(): ChallengePhotoInfoDAO
    abstract fun gifUpdateDao(): GifSettingsDAO
}