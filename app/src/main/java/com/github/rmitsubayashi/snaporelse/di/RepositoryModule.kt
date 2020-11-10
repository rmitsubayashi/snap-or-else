package com.github.rmitsubayashi.snaporelse.di

import androidx.room.Room
import com.github.rmitsubayashi.snaporelse.model.AppDB
import com.github.rmitsubayashi.snaporelse.model.repository.*
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val repositoryModules = module {
    single<ChallengeRepository> {
        RoomChallengeRepository(
            get<AppDB>().challengeDao()
        )
    }

    single<PhotoInfoRepository> {
        RoomPhotoInfoRepository(
            get<AppDB>().challengePhotoInfoDao()
        )
    }

    single<PhotoRepository> {
        LocalPhotoRepository()
    }

    single<GifRepository> {
        LocalAndRoomGifRepository(
            get<AppDB>().gifUpdateDao(),
            get(),
            androidContext(),
            get()
        )
    }

    single {
        Room.databaseBuilder(androidApplication(), AppDB::class.java, "appDB.db").build()
    }
}