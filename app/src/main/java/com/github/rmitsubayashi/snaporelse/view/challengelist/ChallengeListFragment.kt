package com.github.rmitsubayashi.snaporelse.view.challengelist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.rmitsubayashi.snaporelse.R
import com.github.rmitsubayashi.snaporelse.view.util.EventObserver
import kotlinx.android.synthetic.main.fragment_challenge_list.*
import org.koin.android.viewmodel.ext.android.viewModel
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt

class ChallengeListFragment : Fragment() {
    private val viewModel: ChallengeListViewModel by viewModel()

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_challenge_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindAdapter()
        bindViews()
        setupNavigation()
        setupClickListeners()
    }

    private fun bindAdapter() {
        val adapter = ChallengeListAdapter(viewLifecycleOwner, viewModel)
        recyclerview_challenge_list.layoutManager = LinearLayoutManager(context)
        recyclerview_challenge_list.adapter = adapter
    }

    private fun bindViews() {
        viewModel.challenges.observe(viewLifecycleOwner, Observer {
            if (it.isEmpty()) {
                MaterialTapTargetPrompt.Builder(requireActivity())
                    .setTarget(R.id.fab_challenge_list)
                    .setPrimaryText("Add your first challenge!")
                    .setSecondaryText("Create a challenge like \'30 Day Body Transformation\' or \'3 Months to Touch My Toes\'!")
                    .setBackButtonDismissEnabled(true)
                    .create()
                    ?.show()
            }
        })
    }

    private fun setupNavigation() {
        viewModel.openChallengeEvent.observe(viewLifecycleOwner, EventObserver {
            val action = ChallengeListFragmentDirections.actionChallengeListFragmentToChallengeDetailsFragment(it)
            findNavController().navigate(action)
        })
        viewModel.newChallengeEvent.observe(viewLifecycleOwner, EventObserver {
            val action = ChallengeListFragmentDirections.actionChallengeListFragmentToAddChallengeFragment()
            findNavController().navigate(action)
        })
    }

    private fun setupClickListeners() {
        fab_challenge_list.setOnClickListener {
            viewModel.addChallenge()
        }
    }
}