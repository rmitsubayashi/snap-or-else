package com.github.rmitsubayashi.snaporelse.model.entity

import androidx.room.TypeConverter

class PhotoFrequencyConverter {
    @TypeConverter
    fun fromString(value: String?): PhotoFrequency? {
        return value?.let {
            PhotoFrequency.fromString(value)
        }
    }

    @TypeConverter
    fun pictureFrequencyToString(photoFrequency: PhotoFrequency?): String? {
        return photoFrequency?.stringVal
    }
}