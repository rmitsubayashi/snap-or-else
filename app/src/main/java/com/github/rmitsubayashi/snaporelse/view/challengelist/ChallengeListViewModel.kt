package com.github.rmitsubayashi.snaporelse.view.challengelist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.rmitsubayashi.snaporelse.model.entity.Challenge
import com.github.rmitsubayashi.snaporelse.usecase.GetChallengesUseCase
import com.github.rmitsubayashi.snaporelse.view.util.Event
import kotlinx.coroutines.launch

class ChallengeListViewModel(
    private val getChallengesUseCase: GetChallengesUseCase
): ViewModel() {
    private val _challenges = MutableLiveData<List<Challenge>>()
    val challenges: LiveData<List<Challenge>> get() = _challenges

    private val _openChallengeEvent = MutableLiveData<Event<Int>>()
    val openChallengeEvent: LiveData<Event<Int>> get() = _openChallengeEvent

    private val _newChallengeEvent = MutableLiveData<Event<Unit>>()
    val newChallengeEvent: LiveData<Event<Unit>> get() = _newChallengeEvent

    init {
        loadChallenges()
    }

    private fun loadChallenges() {
        viewModelScope.launch {
            val challenges = getChallengesUseCase.execute()
            _challenges.value = challenges
        }
    }

    fun openChallenge(challengeID: Int) {
        _openChallengeEvent.value = Event(challengeID)
    }

    fun addChallenge() {
        _newChallengeEvent.value = Event(Unit)
    }
}