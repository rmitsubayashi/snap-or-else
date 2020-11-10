package com.github.rmitsubayashi.snaporelse.usecase

import android.content.Intent
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.github.rmitsubayashi.snaporelse.model.entity.Challenge
import com.github.rmitsubayashi.snaporelse.model.entity.PhotoInfo
import java.io.File
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/*
this use case technically saves the photo as well.
we can return just the bitmap from the request.
https://developer.android.com/training/camera/photobasics
then, we can save the bitmap in our repository class.
but since the request makes it simpler to save images, we do this instead
 */
class FireTakePhotoIntentUseCase {
    fun execute(
        fragment: Fragment,
        challenge: Challenge
    ): PhotoInfo? {
        val now = LocalDateTime.now()
        val fileName = now.format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also {
                takePictureIntent ->
            takePictureIntent.resolveActivity(fragment.requireActivity().packageManager)?.also {
                // create the file where the photo will be stored
                val photoFile: File? = try {
                    createImageFile(fragment, fileName)
                } catch (exception: IOException) {
                    return null
                }
                if (photoFile != null) {
                    val photoURI = FileProvider.getUriForFile(
                        fragment.requireContext(),
                        "com.github.rmitsubayashi.snaporelse.fileprovider",
                        photoFile
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    // we need the fragment to call this, or the fragment will not handle the intent
                    fragment.startActivityForResult(takePictureIntent, REQUEST_CODE)
                    return PhotoInfo(
                        challenge.id, photoFile.absolutePath, LocalDateTime.now()
                    )
                }
            }
        }
        return null
    }

    @Throws(IOException::class)
    private fun createImageFile(fragment: Fragment, fileName: String): File {
        val storageDir = fragment.requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            fileName, ".jpg", storageDir
        )
    }

    companion object {
        const val REQUEST_CODE = 11
    }
}