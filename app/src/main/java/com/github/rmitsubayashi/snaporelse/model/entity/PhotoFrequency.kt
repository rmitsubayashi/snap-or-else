package com.github.rmitsubayashi.snaporelse.model.entity

enum class PhotoFrequency(val stringVal: String) {
    EVERY_DAY("every day"),
    EVERY_WEEK("every week"),
    WHENEVER("whenever");

    companion object {
        fun fromString(stringVal: String): PhotoFrequency {
            return values().first { it.stringVal == stringVal }
        }
    }
}