package com.github.rmitsubayashi.snaporelse.view.photolist

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.signature.ObjectKey
import com.github.rmitsubayashi.snaporelse.model.entity.PhotoInfo
import kotlinx.android.synthetic.main.item_photo_list.view.*

class PhotoListViewHolder(private val viewModel: PhotoListViewModel, itemView: View): RecyclerView.ViewHolder(itemView) {
    fun setPhotoInfo(photoInfo: PhotoInfo) {
        itemView.setOnClickListener {
            viewModel.openPhoto(photoInfo, itemView.item_photo)
        }
        Glide.with(itemView)
            .load(photoInfo.fileName)
            .signature(ObjectKey(photoInfo.lastUpdate))
            .into(itemView.item_photo)
        itemView.item_photo.transitionName = photoInfo.fileName
    }
}