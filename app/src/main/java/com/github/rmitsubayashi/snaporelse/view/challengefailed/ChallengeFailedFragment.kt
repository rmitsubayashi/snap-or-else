package com.github.rmitsubayashi.snaporelse.view.challengefailed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.github.rmitsubayashi.snaporelse.R
import kotlinx.android.synthetic.main.fragment_challenge_failed.*

class ChallengeFailedFragment: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_challenge_failed, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        button_failed_go_back.setOnClickListener {
            val action = ChallengeFailedFragmentDirections.actionChallengeFailedFragmentToChallengeListFragment()
            findNavController().navigate(action)
        }
    }
}