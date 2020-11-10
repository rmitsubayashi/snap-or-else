package com.github.rmitsubayashi.snaporelse.view.challengedetails

import android.Manifest
import android.app.TimePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.github.rmitsubayashi.snaporelse.R
import com.github.rmitsubayashi.snaporelse.model.entity.PhotoFrequency
import com.github.rmitsubayashi.snaporelse.view.util.EventObserver
import com.github.rmitsubayashi.snaporelse.view.util.showToast
import kotlinx.android.synthetic.main.dialog_frame_rate.view.*
import kotlinx.android.synthetic.main.fragment_challenge_details.*
import org.koin.android.viewmodel.ext.android.viewModel
import pl.droidsonroids.gif.AnimationListener
import pl.droidsonroids.gif.GifDrawable
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle


class ChallengeDetailsFragment: Fragment() {
    private val viewModel: ChallengeDetailsViewModel by viewModel()
    private val args: ChallengeDetailsFragmentArgs by navArgs()
    private val externalStorageRequestCode = 7

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_challenge_details, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.challenge_details_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_export_challenge_details -> {
                //ask for permission first
                // Check if we have write permission

                // Check if we have write permission
                val permission = ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                val storagePermissions = arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )

                if (permission != PackageManager.PERMISSION_GRANTED) {
                    // We don't have permission so prompt the user
                    requestPermissions(
                        storagePermissions,
                        externalStorageRequestCode
                    )
                } else {
                    viewModel.export()
                }
            }
            R.id.action_delete_challenge_details -> {
                viewModel.removeChallenge()
            }
            R.id.action_edit_challenge_details -> {
                viewModel.editPhotos()
            }
            R.id.action_set_frame_rate -> {
                viewModel.openSetFrameRate()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val challengeID = args.ChallengeID
        viewModel.loadChallenge(challengeID)
        setupNavigation()
        setupClickListeners()
        setupViewBinding()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        viewModel.handleIntent(requestCode, resultCode)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == externalStorageRequestCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                viewModel.export()
            }
        }
    }

    private fun setupNavigation() {
        viewModel.failedChallengeEvent.observe(viewLifecycleOwner, EventObserver {
            val action = ChallengeDetailsFragmentDirections.actionChallengeDetailsFragmentToChallengeFailedFragment(args.ChallengeID)
            findNavController().navigate(action)
        })
        viewModel.removedEvent.observe(viewLifecycleOwner, EventObserver {
            val action = ChallengeDetailsFragmentDirections.actionChallengeDetailsFragmentToChallengeListFragment()
            findNavController().navigate(action)
        })
        viewModel.editPhotoEvent.observe(viewLifecycleOwner, EventObserver {
            val action = ChallengeDetailsFragmentDirections.actionChallengeDetailsFragmentToPhotoListFragment(it)
            findNavController().navigate(action)
        })
    }

    private fun setupClickListeners() {
        button_take_picture.setOnClickListener {
            viewModel.takePicture(this)
        }

        button_remove_reminder.setOnClickListener {
            viewModel.setReminder(null)
        }

        viewModel.challenge.observe(viewLifecycleOwner, Observer {
            challenge ->
            button_edit_reminder.setOnClickListener {
                val onTimeSetListener = TimePickerDialog.OnTimeSetListener {
                        _, hour, minutes ->
                    viewModel.setReminder(LocalTime.of(hour, minutes))

                }
                val timePickerDialog = TimePickerDialog(
                    requireContext(),
                    onTimeSetListener,
                    challenge.notificationTime?.hour ?: 18,
                    challenge.notificationTime?.minute ?: 0,
                    true
                )
                timePickerDialog.show()
            }
        })

        swipe_challenge_details.setOnRefreshListener {
            viewModel.refresh()
        }
    }

    private fun setupViewBinding() {
        viewModel.thumbnailPath.observe(viewLifecycleOwner, Observer {
            if (viewModel.preview.value == null)
                Glide.with(this)
                    .load(it)
                    .into(image_recent_photo)
        })
        viewModel.preview.observe(viewLifecycleOwner, Observer {
            byteArray ->
            byteArray?.let {
                val drawable = GifDrawable(it).apply {
                    // set to the most recent photo
                    seekToBlocking(this.duration)
                    // automatically runs otherwise
                    stop()
                }
                image_recent_photo.setImageDrawable(drawable)
                button_play.setOnClickListener {
                    // we don't hide the visibility of the play button
                    // so the user can't capture the screen
                    drawable.reset()
                }
                button_play.visibility = View.VISIBLE
            }
        })
        viewModel.photoResultMessage.observe(viewLifecycleOwner, EventObserver {
            requireContext().showToast(it)
        })
        viewModel.checkActiveResultEvent.observe(viewLifecycleOwner, EventObserver {
            requireContext().showToast(it)
        })
        viewModel.exportResultEvent.observe(viewLifecycleOwner, EventObserver {
            requireContext().showToast(it)
        })
        viewModel.challenge.observe(viewLifecycleOwner, Observer {
            label_challenge_title.text = it.title
            if (it.isHardCore) {
                label_cheat_days_left.visibility = View.VISIBLE
                label_cheat_day_number.visibility = View.VISIBLE
                label_cheat_day_number.text = it.cheatDays.toString()
            } else {
                label_cheat_days_left.visibility = View.GONE
                label_cheat_day_number.visibility = View.GONE
            }

            if (it.photoFrequency == PhotoFrequency.WHENEVER) {
                label_next_deadline.visibility = View.GONE
                label_next_deadline_value.visibility = View.GONE
            } else {
                label_next_deadline.visibility = View.VISIBLE
                label_next_deadline_value.visibility = View.VISIBLE
            }

            // will not matter if the photo frequency is whenever
            button_remove_reminder.visibility =if (it.notificationTime == null) {
                View.INVISIBLE
            } else {
                View.VISIBLE
            }
            label_reminder_value.text =
                if (it.notificationTime == null) "Not Set" else DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).format(it.notificationTime)

            if (it.photoFrequency == PhotoFrequency.WHENEVER) {
                button_remove_reminder.visibility = View.GONE
                button_edit_reminder.visibility = View.GONE
                label_reminder.visibility = View.GONE
                label_reminder_value.visibility = View.GONE
            }
        })
        viewModel.deadline.observe(viewLifecycleOwner, Observer {
            label_next_deadline_value.text = it
        })
        viewModel.daysUntilGoal.observe(viewLifecycleOwner, Observer {
            label_days_until_goal_value.text = it
        })
        viewModel.openSetFrameRateEvent.observe(viewLifecycleOwner, EventObserver {
            currentFrameRate ->
            val layout = layoutInflater.inflate(R.layout.dialog_frame_rate, null)
            layout.edittext_frame_rate.apply {
                setText(currentFrameRate.toString())
                // cursor still at beginning
                setSelection(currentFrameRate.toString().length)
            }
            AlertDialog.Builder(requireContext())
                .setTitle("Set a new frame rate")
                .setMessage("Note that you will need to reload the screen to view changes")
                .setView(layout)
                .setPositiveButton("Set"){
                        _, _ ->
                    val newValue = layout.edittext_frame_rate.text.toString()
                    val newValueInt = try {
                        Integer.parseInt(newValue)
                    } catch (e: NumberFormatException) {
                        requireContext().showToast("Error processing input")
                        return@setPositiveButton
                    }
                    viewModel.setFrameRate(newValueInt)
                }
                .setNegativeButton("cancel") {_,_ ->}
                .show()
        })
        viewModel.refreshedEvent.observe(viewLifecycleOwner, EventObserver {
            swipe_challenge_details.isRefreshing = false
        })
    }



}