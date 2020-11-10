package com.github.rmitsubayashi.snaporelse.view.photolist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.github.rmitsubayashi.snaporelse.R
import com.github.rmitsubayashi.snaporelse.model.entity.PhotoInfo

class PhotoListAdapter(lifecycleOwner: LifecycleOwner, private val viewModel: PhotoListViewModel): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val list = mutableListOf<PhotoInfo>()

    override fun getItemCount(): Int = list.size

    init {
        viewModel.info.observe(lifecycleOwner, Observer {
            list.clear()
            list.addAll(it)
            notifyDataSetChanged()
        })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return PhotoListViewHolder(viewModel, inflater.inflate(R.layout.item_photo_list, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val photoInfo = list[position]
        (holder as PhotoListViewHolder).setPhotoInfo(photoInfo)
    }
}