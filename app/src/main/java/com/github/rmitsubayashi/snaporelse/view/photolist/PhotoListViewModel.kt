package com.github.rmitsubayashi.snaporelse.view.photolist

import android.widget.ImageView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.rmitsubayashi.snaporelse.model.entity.PhotoInfo
import com.github.rmitsubayashi.snaporelse.usecase.GetPhotoInfoListUseCase
import com.github.rmitsubayashi.snaporelse.view.util.Event
import kotlinx.coroutines.launch

class PhotoListViewModel(
    private val getPhotoInfoUseCase: GetPhotoInfoListUseCase
): ViewModel() {
    private val _info = MutableLiveData<List<PhotoInfo>>()
    val info: LiveData<List<PhotoInfo>> get() = _info

    private val _openPhotoEvent = MutableLiveData<Event<Pair<PhotoInfo,ImageView>>>()
    val openPhotoEvent: LiveData<Event<Pair<PhotoInfo,ImageView>>> get() = _openPhotoEvent

    fun loadPhotos(challengeID: Int) {
        viewModelScope.launch {
            val photoInfo = getPhotoInfoUseCase.execute(challengeID)
            _info.value = photoInfo
        }
    }

    fun openPhoto(photo: PhotoInfo, transitionView: ImageView) {
        _openPhotoEvent.value = Event(Pair(photo, transitionView))
    }
}