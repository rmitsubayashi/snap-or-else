package com.github.rmitsubayashi.snaporelse.view.challengelist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.github.rmitsubayashi.snaporelse.R
import com.github.rmitsubayashi.snaporelse.model.entity.Challenge

class ChallengeListAdapter(lifecycleOwner: LifecycleOwner, private val viewModel: ChallengeListViewModel): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val list = mutableListOf<Challenge>()

    override fun getItemCount(): Int = list.size

    init {
        viewModel.challenges.observe(lifecycleOwner, Observer {
            list.clear()
            list.addAll(it)
            notifyDataSetChanged()
        })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ChallengeListViewHolder(viewModel, inflater.inflate(R.layout.item_challenge_list, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val challenge = list[position]
        (holder as ChallengeListViewHolder).setChallenge(challenge)
    }
}