package com.example.whatsappclone.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.whatsappclone.R

const val UID = "uid"
const val NAME = "name"
const val IMAGE = "image"


class ChatActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)



    }
}