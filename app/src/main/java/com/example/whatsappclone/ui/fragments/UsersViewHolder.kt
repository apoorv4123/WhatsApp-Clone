package com.example.whatsappclone.ui.fragments

import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.whatsappclone.R
import com.example.whatsappclone.modals.User
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.list_item.view.*

class UsersViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

    // bind views to list view
    fun bind(user: User)= with(itemView){
        countTv.isVisible = false
        timeTv.isVisible = false

        titleTv.text = user.name
        subTitleTv.text = user.status`

        Picasso.get()
            .load(user.thumbImage)
            .placeholder(R.drawable.defaultavatar)
            .error(R.drawable.defaultavatar)
            .into(userImgView)


    }

}