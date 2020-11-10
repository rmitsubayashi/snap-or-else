package com.github.rmitsubayashi.snaporelse.view.challengedetails

import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.rmitsubayashi.snaporelse.model.entity.Challenge
import com.github.rmitsubayashi.snaporelse.model.entity.PhotoInfo
import com.github.rmitsubayashi.snaporelse.usecase.*
import com.github.rmitsubayashi.snaporelse.view.util.Event
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

class ChallengeDetailsViewModel(
    private val getChallengeUseCase: GetChallengeUseCase,
    private val fireTakePhotoIntentUseCase: FireTakePhotoIntentUseCase,
    private val handleTakePhotoIntentUseCase: HandleTakePhotoIntentUseCase,
    private val getMostRecentPhotoUseCase: GetMostRecentPhotoUseCase,
    private val checkUnsuccessfulChallengeUseCase: CheckUnsuccessfulChallengeUseCase,
    private val getNextPhotoDeadlineUseCase: GetNextPhotoDeadlineUseCase,
    private val createPublicGIFUseCase: CreatePublicGIFUseCase,
    private val updateReminderUseCase: UpdateReminderUseCase,
    private val cancelReminderUseCase: CancelReminderUseCase,
    private val calculateDaysUntilGoalUseCase: CalculateDaysUntilGoalUseCase,
    private val removeChallengeUseCase: RemoveChallengeUseCase,
    private val getMostRecentGifUseCase: GetMostRecentGifUseCase,
    private val getCurrentFrameRateUseCase: GetCurrentFrameRateUseCase,
    private val setFrameRateUseCase: SetFrameRateUseCase

): ViewModel() {
    private val _challenge = MutableLiveData<Challenge>()
    val challenge: LiveData<Challenge> get() = _challenge
    private val _thumbnailPath = MutableLiveData<String>()
    val thumbnailPath: LiveData<String> get() = _thumbnailPath
    private val _preview = MutableLiveData<ByteArray>()
    val preview: LiveData<ByteArray> get() = _preview
    private val _daysUntilGoal = MutableLiveData<String>()
    val daysUntilGoal: LiveData<String> get() = _daysUntilGoal
    private var photoInfo: PhotoInfo? = null
    private val _photoResultMessage = MutableLiveData<Event<String>>()
    val photoResultMessage: LiveData<Event<String>> get() = _photoResultMessage
    private val _failedChallengeEvent = MutableLiveData<Event<Unit>>()
    val failedChallengeEvent: LiveData<Event<Unit>> get() = _failedChallengeEvent
    private val _checkActiveResultEvent = MutableLiveData<Event<String>>()
    val checkActiveResultEvent: LiveData<Event<String>> get() = _checkActiveResultEvent
    private val _deadline = MutableLiveData<String>()
    val deadline: LiveData<String> get() = _deadline
    private val _exportResultEvent = MutableLiveData<Event<String>>()
    val exportResultEvent: LiveData<Event<String>> get() = _exportResultEvent
    private val _removedEvent = MutableLiveData<Event<Unit>>()
    val removedEvent: LiveData<Event<Unit>> get() = _removedEvent
    private val _editPhotoEvent = MutableLiveData<Event<Int>>()
    val editPhotoEvent: LiveData<Event<Int>> get() = _editPhotoEvent
    private val _openSetFrameRateEvent = MutableLiveData<Event<Int>>()
    val openSetFrameRateEvent: LiveData<Event<Int>> get() = _openSetFrameRateEvent
    private val _refreshedEvent = MutableLiveData<Event<Unit>>()
    val refreshedEvent: LiveData<Event<Unit>> get() = _refreshedEvent

    fun loadChallenge(id: Int) {
        viewModelScope.launch {
            _challenge.value = getChallengeUseCase.execute(id)
            load()
        }
    }

    private suspend fun load() {
        checkActive()
        calculateDeadline()
        calculateDaysUntilGoal()
        loadImage()
    }

    fun takePicture(fragment: Fragment) {
        _challenge.value?.let {
            photoInfo = fireTakePhotoIntentUseCase.execute(fragment, it)
        }
    }

    fun export() {
        viewModelScope.launch {
            _challenge.value?.let {
                if (it.endDate > LocalDate.now()) {
                    _exportResultEvent.value = Event("Finish your challenge before exporting!!")
                } else {
                    val result = createPublicGIFUseCase.execute(it)
                    if (result) {
                        _exportResultEvent.value = Event("Exported! Check your downloads")
                    } else {
                        _exportResultEvent.value = Event("Could not export")
                    }
                }
            }
        }
    }

    fun removeChallenge() {
        viewModelScope.launch {
            _challenge.value?.let {
                removeChallengeUseCase.execute(it)
                _removedEvent.value = Event(Unit)
            }
        }
    }

    fun editPhotos() {
        _challenge.value?.let {
            _editPhotoEvent.value = Event(it.id)
        }
    }

    fun openSetFrameRate() {
        viewModelScope.launch {
            _challenge.value?.let {
                val currentFrameRate = getCurrentFrameRateUseCase.execute(it.id)
                _openSetFrameRateEvent.value = Event(currentFrameRate)
            }
        }
    }

    fun setFrameRate(newFrameRate: Int) {
        viewModelScope.launch {
            _challenge.value?.let {
                setFrameRateUseCase.execute(it.id, newFrameRate)
            }
        }
    }

    fun setReminder(localTime: LocalTime?) {
        viewModelScope.launch {
            _challenge.value?.let {
                updateReminderUseCase.execute(it, localTime)
                reloadChallenge(it.id)
            }
        }
    }

    private suspend fun checkActive() {
        _challenge.value?.let {
            val message = checkUnsuccessfulChallengeUseCase.execute(it)
            if (checkUnsuccessfulChallengeUseCase.isDeactivateMessage(message)) {
                _failedChallengeEvent.value = Event(Unit)
            } else if (message != null) {
                _checkActiveResultEvent.value = Event(message)
                reloadChallenge(it.id)
            }
        }
    }

    private suspend fun reloadChallenge(id: Int) {
        //refresh
        _challenge.value = getChallengeUseCase.execute(id)
    }

    fun refresh() {
        viewModelScope.launch {
            _challenge.value?.let {
                reloadChallenge(it.id)
                load()
                _refreshedEvent.value = Event(Unit)
            }
        }
    }

    private suspend fun loadImage() {
        _challenge.value?.let {
            // not guaranteed to be the most recent (just the most recent at the time of query).
            val file = getMostRecentGifUseCase.execute(it.id)
            if (file != null) {
                _preview.value = file
            } else {
                // if this the user's first photo and there is no gif generated yet,
                // we still have the thumbnail to fall back on
                val path = getMostRecentPhotoUseCase.execute(it)
                if (path != null) {
                    _thumbnailPath.value = path
                }
            }
        }
    }

    private suspend fun calculateDeadline() {
        _challenge.value?.let {
            val daysUntilGoal = calculateDaysUntilGoalUseCase.execute(it)
            if (daysUntilGoal == 0) {
                _deadline.value = "n/a"
                return@let
            }
            val deadline = getNextPhotoDeadlineUseCase.execute(it)
            if (deadline != null) {
                val userPerceivedDeadline = deadline.minusDays(1)
                _deadline.value = "${userPerceivedDeadline.monthValue}/${userPerceivedDeadline.dayOfMonth}"
            }
        }
    }

    private suspend fun calculateDaysUntilGoal() {
        _challenge.value?.let {
            val days = calculateDaysUntilGoalUseCase.execute(it)
            if (days == 0) {
                // successful challenge!
                _daysUntilGoal.value = "0"
                // we don't want the user to get further notifications
                if (it.notificationTime!= null) {
                    cancelReminderUseCase.execute(it)
                }

            }  else {
                _daysUntilGoal.value = days.toString()
            }
        }
    }

    fun handleIntent(requestCode: Int, resultCode: Int) {
        viewModelScope.launch {
            val message = handleTakePhotoIntentUseCase.execute(requestCode, resultCode, photoInfo)
            if (message != null) {
                _photoResultMessage.value = Event(message)
                if (handleTakePhotoIntentUseCase.isSuccessMessage(message)) {
                    loadImage()
                }
            }
        }
    }
}