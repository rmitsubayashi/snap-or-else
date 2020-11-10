package com.github.rmitsubayashi.snaporelse.model.repository

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.github.rmitsubayashi.snaporelse.model.entity.PhotoInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.*

class LocalPhotoRepository(

): PhotoRepository {
    override suspend fun get(photoInfo: PhotoInfo): Bitmap? {
        return withContext(Dispatchers.IO) {
            try {
                val file = File(photoInfo.fileName)
                BitmapFactory.decodeStream(FileInputStream(file))
            } catch (exception: FileNotFoundException) {
                exception.printStackTrace()
                null
            }
        }
    }

    override suspend fun delete(photoInfo: PhotoInfo): Boolean {
        return withContext(Dispatchers.IO) {
            val file = File(photoInfo.fileName)
            file.delete()
        }
    }

    override suspend fun update(photoInfo: PhotoInfo, bitmap: Bitmap): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val stream = FileOutputStream(photoInfo.fileName)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            } catch (exception: IOException) {
                false
            }
        }
    }
}