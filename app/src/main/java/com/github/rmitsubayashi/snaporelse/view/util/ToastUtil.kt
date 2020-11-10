package com.github.rmitsubayashi.snaporelse.view.util

import android.content.Context
import android.widget.Toast

private const val toastLengthThreshold = 10

fun Context.showToast(resID: Int) {
    val length = if (getString(resID).length > toastLengthThreshold) Toast.LENGTH_LONG else Toast.LENGTH_SHORT
    Toast.makeText(this, resID, length).show()
}

fun Context.showToast(text: String) {
    val length = if (text.length > toastLengthThreshold) Toast.LENGTH_LONG else Toast.LENGTH_SHORT
    Toast.makeText(this, text, length).show()
}