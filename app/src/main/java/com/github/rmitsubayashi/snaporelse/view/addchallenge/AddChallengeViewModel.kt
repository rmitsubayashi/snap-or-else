package com.github.rmitsubayashi.snaporelse.view.addchallenge

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.rmitsubayashi.snaporelse.model.entity.PhotoFrequency
import com.github.rmitsubayashi.snaporelse.usecase.AddChallengeUseCase
import com.github.rmitsubayashi.snaporelse.usecase.ScheduleReminderUseCase
import com.github.rmitsubayashi.snaporelse.usecase.ValidateAddChallengeFormUseCase
import com.github.rmitsubayashi.snaporelse.view.util.Event
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

class AddChallengeViewModel(
    private val validateAddChallengeFormUseCase: ValidateAddChallengeFormUseCase,
    private val addChallengeUseCase: AddChallengeUseCase,
    private val scheduleReminderUseCase: ScheduleReminderUseCase
): ViewModel() {
    private val _title = MutableLiveData<String>()
    private val _pictureFrequency = MutableLiveData<PhotoFrequency>()
    private val _dayOfDeadline = MutableLiveData<DayOfWeek?>()
    private val _isHardCore = MutableLiveData<Boolean>()
    private val _cheatDays = MutableLiveData<Int>()
    private val _notificationTime = MutableLiveData<LocalTime?>()
    val notificationTime: LiveData<LocalTime?> get() = _notificationTime
    private val _endDate = MutableLiveData<LocalDate>()
    val endDate: LiveData<LocalDate> get() = _endDate

    private val _errorMessage = MutableLiveData<Event<String>>()
    val errorMessage: LiveData<Event<String>> get() = _errorMessage

    private val _addedEvent = MutableLiveData<Event<Unit>>()
    val addedEvent: LiveData<Event<Unit>> get() = _addedEvent

    fun setTitle(title: String) {
        _title.value = title
    }

    fun setIsHardCore(isHardCore: Boolean) {
        _isHardCore.value = isHardCore
    }

    fun setCheatDays(days: Int?) {
        _cheatDays.value = days
    }

    fun setEndDate(date: LocalDate) {
        _endDate.value = date
    }

    fun setReminder(time: LocalTime?) {
        _notificationTime.value = time
    }

    fun setPictureFrequency(photoFrequency: PhotoFrequency?) {
        _pictureFrequency.value = photoFrequency
    }

    fun setDayOfDeadline(dayOfWeek: DayOfWeek?) {
        _dayOfDeadline.value = dayOfWeek
    }

    fun add() {
        val title = _title.value
        val pictureFrequency = _pictureFrequency.value
        val dayOfDeadline = _dayOfDeadline.value
        val isHardCore = _isHardCore.value ?: false
        val cheatDay  = _cheatDays.value ?: 0
        val endDate = _endDate.value
        val notificationTime = _notificationTime.value
        val errorMessage = validateAddChallengeFormUseCase.execute(title, endDate, pictureFrequency, dayOfDeadline, isHardCore, cheatDay)
        if (errorMessage == null) {
            viewModelScope.launch{
                val challenge = addChallengeUseCase.execute(title, endDate, pictureFrequency, dayOfDeadline, isHardCore, cheatDay, notificationTime)
                if (notificationTime != null) {
                    scheduleReminderUseCase.execute(challenge, notificationTime, null)
                }
                _addedEvent.value = Event(Unit)
            }
        } else {
            _errorMessage.value = Event(errorMessage)
        }
    }


}