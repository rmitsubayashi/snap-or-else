package com.github.rmitsubayashi.snaporelse.di

import com.github.rmitsubayashi.snaporelse.usecase.*
import com.github.rmitsubayashi.snaporelse.usecase.util.TodayGenerator
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module


val useCaseModules = module {
    factory { GetChallengesUseCase(get()) }
    factory { AddChallengeUseCase(get(), get(), get()) }
    factory { ValidateAddChallengeFormUseCase(get()) }
    factory { GetChallengeUseCase(get()) }
    factory { FireTakePhotoIntentUseCase() }
    factory { HandleTakePhotoIntentUseCase(get(), get(), get(), get()) }
    factory { GetMostRecentPhotoUseCase(get(), get()) }
    factory { CheckUnsuccessfulChallengeUseCase(get(), get(), get()) }
    factory { GetNextPhotoDeadlineUseCase(get()) }
    factory { CreatePublicGIFUseCase(get(), get()) }
    factory { CreateGIFUseCase(get(), get(), get()) }
    factory { ScheduleReminderUseCase(get(), get(), androidContext()) }
    factory { CancelReminderUseCase(get(), androidContext()) }
    factory { UpdateReminderUseCase(get(), get()) }
    factory { CalculateDaysUntilGoalUseCase(get()) }
    factory { RemoveChallengeUseCase(get()) }
    factory { TodayGenerator() }
    factory { GetPhotoInfoListUseCase(get()) }
    factory { GetPhotoInfoUseCase(get()) }
    factory { RemovePhotoUseCase(get(), get(), get(), get()) }
    factory { GetPhotoUseCase(get()) }
    factory { UpdatePhotoUseCase(get(), get(), get(), get(), get()) }
    factory { ReorientBitmapUseCase(get(), androidContext()) }
    factory { CreatePrivateGIFInBackgroundUseCase(androidContext()) }
    factory { GetMostRecentGifUseCase(get()) }
    factory { GetCurrentFrameRateUseCase(get()) }
    factory { SetFrameRateUseCase(get(), get()) }
}