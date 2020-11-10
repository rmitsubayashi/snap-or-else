package com.github.rmitsubayashi.snaporelse.usecase

import android.content.Context
import android.net.Uri
import com.github.rmitsubayashi.snaporelse.model.entity.PhotoInfo
import com.github.rmitsubayashi.snaporelse.model.repository.PhotoRepository
import com.github.rmitsubayashi.snaporelse.usecase.util.BitmapUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class ReorientBitmapUseCase(private val photoRepository: PhotoRepository, private val context: Context) {
    // some Android cameras are default landscape, so pictures taken in 'portrait'
    // are in fact landscape (the image is shown sideways on screen)
    suspend fun execute(photoInfo: PhotoInfo): Boolean {
        return withContext(Dispatchers.IO) {
            val file = File(photoInfo.fileName)
            val uri = Uri.fromFile(file)
            val reorientedBitmap = BitmapUtil.handleSamplingAndRotationBitmap(context, uri)
            photoRepository.update(photoInfo, reorientedBitmap)
        }
    }
}