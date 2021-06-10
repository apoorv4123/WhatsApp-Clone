package com.example.whatsappclone.modals

import java.util.*

data class Inbox(
    var count: Int = 0,
    val from: String,
    var image: String,
    val msg: String,
    var name: String,
    val time: Date = Date()
) {
    constructor() : this(0, "", "", "", "", Date())
}
