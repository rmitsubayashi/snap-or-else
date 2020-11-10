package com.github.rmitsubayashi.snaporelse.view.photodetails

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.transition.TransitionInflater
import com.bumptech.glide.Glide
import com.bumptech.glide.signature.ObjectKey
import com.github.rmitsubayashi.snaporelse.R
import com.github.rmitsubayashi.snaporelse.view.util.EventObserver
import com.github.rmitsubayashi.snaporelse.view.util.showToast
import kotlinx.android.synthetic.main.fragment_photo_details.*
import org.koin.android.viewmodel.ext.android.viewModel

class PhotoDetailsFragment: Fragment() {
    private val viewModel: PhotoDetailsViewModel by viewModel()
    private val args: PhotoDetailsFragmentArgs by navArgs()
    private var isFirstLoad = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // we don't want the user to take a photo (should only be able to export after finishing the challenge)
        requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_photo_details, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.photo_details_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_delete_photo_details -> {
                // the user might have misclicked, so ask for confirmation
                AlertDialog.Builder(requireContext())
                    .setMessage("Are you sure?")
                    .setPositiveButton("Yes") {
                        _,_-> viewModel.deletePhoto()
                    }
                    .setNegativeButton("No") {_,_->}
                    .show()
            }
            R.id.action_rotate_photo_details -> {
                viewModel.rotatePhoto()
            }
            R.id.action_save_photo_details -> {
                viewModel.save()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.loadPhotoInfo(args.PhotoID)
        fillPhoto()
        bindViews()
        setupNavigation()
    }

    override fun onDestroy() {
        super.onDestroy()
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
    }

    private fun fillPhoto() {
        photo_details.transitionName = args.PhotoFilePath
        // we can't do this asynchronously with animation
        // (load photo info -> get file path -> load image)
        Glide.with(this)
            .load(args.PhotoFilePath)
            .signature(ObjectKey(args.GlidePhotoKey))
            .into(photo_details)
    }

    private fun bindViews() {
        viewModel.photo.observe(viewLifecycleOwner, Observer {
            if (!isFirstLoad) {
                Glide.with(this)
                    .load(it.first)
                    .signature(ObjectKey(it.second))
                    .into(photo_details)
            } else {
                // the first load is done by the transition animation
                isFirstLoad = false
            }
        })
    }

    private fun setupNavigation() {
        viewModel.deleteEvent.observe(viewLifecycleOwner, EventObserver {
            if (it.first) {
                requireContext().showToast("Deleted")
                val action = PhotoDetailsFragmentDirections.actionPhotoDetailsFragmentToPhotoListFragment(it.second)
                findNavController().navigate(action)
            } else {
                requireContext().showToast("Could not delete")
            }
        })
        viewModel.savedEvent.observe(viewLifecycleOwner, EventObserver {
            if (it.first) {
                requireContext().showToast("Saved!")
                val action = PhotoDetailsFragmentDirections.actionPhotoDetailsFragmentToPhotoListFragment(it.second)
                findNavController().navigate(action)
            } else {
                requireContext().showToast("Could not save")
            }
        })
    }


}