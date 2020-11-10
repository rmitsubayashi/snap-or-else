package com.github.rmitsubayashi.snaporelse.view.challengelist

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.github.rmitsubayashi.snaporelse.model.entity.Challenge
import kotlinx.android.synthetic.main.item_challenge_list.view.*

class ChallengeListViewHolder(private val viewModel: ChallengeListViewModel, itemView: View): RecyclerView.ViewHolder(itemView) {
    fun setChallenge(challenge: Challenge) {
        itemView.setOnClickListener {
            viewModel.openChallenge(challenge.id)
        }
        itemView.title_item_challenge_list.text = challenge.title
    }

}