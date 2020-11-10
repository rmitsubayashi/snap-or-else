package com.github.rmitsubayashi.snaporelse.view.photodetails

import android.graphics.Bitmap
import android.graphics.Matrix
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.rmitsubayashi.snaporelse.model.entity.PhotoInfo
import com.github.rmitsubayashi.snaporelse.usecase.GetPhotoInfoUseCase
import com.github.rmitsubayashi.snaporelse.usecase.GetPhotoUseCase
import com.github.rmitsubayashi.snaporelse.usecase.RemovePhotoUseCase
import com.github.rmitsubayashi.snaporelse.usecase.UpdatePhotoUseCase
import com.github.rmitsubayashi.snaporelse.view.util.Event
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class PhotoDetailsViewModel(
    private val getPhotoInfoUseCase: GetPhotoInfoUseCase,
    private val removePhotoUseCase: RemovePhotoUseCase,
    private val getPhotoUseCase: GetPhotoUseCase,
    private val updatePhotoUseCase: UpdatePhotoUseCase
): ViewModel() {
    private val _photoInfo = MutableLiveData<PhotoInfo>()
    val photoInfo: LiveData<PhotoInfo> get() = _photoInfo
    private val _deleteEvent = MutableLiveData<Event<Pair<Boolean, Int>>>()
    val deleteEvent: LiveData<Event<Pair<Boolean, Int>>> get() = _deleteEvent
    private val _photo = MutableLiveData<Pair<Bitmap, LocalDateTime>>()
    val photo: LiveData<Pair<Bitmap, LocalDateTime>> get() = _photo
    private val _savedEvent = MutableLiveData<Event<Pair<Boolean, Int>>>()
    val savedEvent: LiveData<Event<Pair<Boolean, Int>>> get() = _savedEvent

    fun loadPhotoInfo(id: Int) {
        viewModelScope.launch {
            val info = getPhotoInfoUseCase.execute(id)
            _photoInfo.value = info
            val bitmap = getPhotoUseCase.execute(info)
            if (bitmap != null) {
                _photo.value = bitmap to info.lastUpdate
            }
        }
    }

    fun deletePhoto() {
        viewModelScope.launch {
            _photoInfo.value?.let {
                val result = removePhotoUseCase.execute(it)
                _deleteEvent.value = Event(result to it.challengeID)
            }
        }
    }

    fun rotatePhoto() {
        _photo.value?.let {
            pair ->
            val matrix = Matrix()
            matrix.postRotate(90f)
            val newBitmap = Bitmap.createBitmap(pair.first, 0, 0, pair.first.width, pair.first.height, matrix, true)
            _photoInfo.value?.let {
                info ->
                // we don't update the lastUpdate value until the user saves
                _photo.value = newBitmap to info.lastUpdate
            }
        }
    }

    fun save() {
        viewModelScope.launch {
            _photo.value?.let { bitmapPair ->
                _photoInfo.value?.let { photoInfo ->
                    val result = updatePhotoUseCase.execute(photoInfo, bitmapPair.first)
                    _savedEvent.value = Event(result to photoInfo.challengeID)
                }
            }
        }
    }
}