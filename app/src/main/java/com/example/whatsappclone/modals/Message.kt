package com.example.whatsappclone.modals

import android.content.Context
import com.example.whatsappclone.utils.formatAsHeader
import java.util.*

interface ChatEvent { // This interface is for showing that some bunch of messages were sent at some date
    val sentAt: Date
}

data class Message(
    val msg: String,
    val senderId: String,
    val msgId: String,
    val type: String = "TEXT",
    val status: Int = 1, // Read state, delivered state, etc
    val liked: Boolean = false,// If you like a message, double tap a message, mark that message as liked
    override val sentAt: Date = Date()// For time of sending message
) : ChatEvent {
    constructor() : this("", "", "", "", 1, false, Date())
}

data class DateHeader(
    override val sentAt: Date = Date(),
    val context: Context
) : ChatEvent {
    val date: String =
        sentAt.formatAsHeader(context) // We wrote formatAsHeader() function in DateFormatUtils class in utils package
}
