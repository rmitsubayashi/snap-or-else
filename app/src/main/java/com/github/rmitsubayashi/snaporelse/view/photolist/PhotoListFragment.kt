package com.github.rmitsubayashi.snaporelse.view.photolist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.github.rmitsubayashi.snaporelse.R
import com.github.rmitsubayashi.snaporelse.view.util.EventObserver
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_photo_list.*
import org.koin.android.viewmodel.ext.android.viewModel
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt

class PhotoListFragment: Fragment() {
    private val viewModel: PhotoListViewModel by viewModel()
    private val args: PhotoListFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_photo_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val challengeID = args.ChallengeID
        viewModel.loadPhotos(challengeID)
        bindAdapter()
        bindViews()
        setupNavigation()
    }

    private fun bindAdapter() {
        val adapter = PhotoListAdapter(viewLifecycleOwner, viewModel)
        list_photo.layoutManager = GridLayoutManager(context, 4)
        list_photo.adapter = adapter
    }

    private fun bindViews() {
        viewModel.info.observe(viewLifecycleOwner, Observer {
            if (it.isEmpty()) {
                val array = arrayListOf<View>()
                // hacky way of getting up arrow
                requireActivity().toolbar.findViewsWithText(array, "Navigate up", View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION)
                val targetView: View = if (array.isEmpty()) {
                    // fallback in case Android changes its content description
                    requireActivity().toolbar
                } else {
                    array[0]
                }
                MaterialTapTargetPrompt.Builder(requireActivity())
                    .setTarget(targetView)
                    .setPrimaryText("No photos to edit!")
                    .setSecondaryText("Go back and take a photo!")
                    // default is white?? covers up the up arrow
                    .setFocalColour(ContextCompat.getColor(requireContext(), android.R.color.transparent))
                    .create()
                    ?.show()
            }
        })
    }

    private fun setupNavigation() {
        viewModel.openPhotoEvent.observe(viewLifecycleOwner, EventObserver {
            val action = PhotoListFragmentDirections.actionPhotoListFragmentToPhotoDetailsFragment(it.first.id, it.first.fileName, it.first.lastUpdate)
            val extras = FragmentNavigatorExtras(
                it.second to it.first.fileName
            )
            findNavController().navigate(action, extras)
        })
    }
}