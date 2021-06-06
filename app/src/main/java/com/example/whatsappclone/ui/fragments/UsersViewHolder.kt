package com.example.whatsappclone.ui.fragments

import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.whatsappclone.R
import com.example.whatsappclone.modals.User
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.list_item.view.*

class UsersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    // bind views to list view
    fun bind(user: User, onClick: (name: String, photo: String, id: String) -> Unit) =
        with(itemView) {
            countTv.isVisible =
                false // In the PEOPLE fragment, time & no. of unread messages is useless
            timeTv.isVisible = false

            titleTv.text = user.name
            subTitleTv.text = user.status
            Picasso.get()
                .load(user.thumbImage) // url of image
                .placeholder(R.drawable.defaultavatar) // image which will be used if image is not loaded
                .error(R.drawable.defaultavatar)
                .into(userImgView) // id of image view
            // Now, we'll work on adapter code. For tht, goto PeopleFragment
            setOnClickListener {
                onClick.invoke(user.name, user.thumbImage, user.uid)
            }
        }
}