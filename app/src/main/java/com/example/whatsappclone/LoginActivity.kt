package com.example.whatsappclone

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private lateinit var phoneNumber:String
    private lateinit var countryCode: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        phoneNumberEt.addTextChangedListener {
            nextBtn.isEnabled =
                !(it.isNullOrEmpty() || it.length < 10)//phoneNumberEt.text.length >= 10
        }

        nextBtn.setOnClickListener {
            checkNumber()
        }

    }

    private fun checkNumber() {
        countryCode = ccp.selectedCountryCodeWithPlus //Firebase needs to be given +91, +92, etc format
        phoneNumber = countryCode + phoneNumberEt.text.toString()

        notifyUser()// Make a dialog box and show it to the user: Optional
    }

    private fun notifyUser() {
        MaterialAlertDialogBuilder(this).apply {
            setMessage("We will be verifying the phone number:$phoneNumber\n " +
                    "Is this okay, or would you like to edit the number?" )
            setPositiveButton("Ok"){ _,_ -> // These are just two empty & un-needed arguments of lambda functions
                showOtpActivity()
            }
            setNegativeButton("Edit"){dialog,which ->
                dialog.dismiss()
            }
            setCancelable(false)
            create()
            show()
        }
    }

    private fun showOtpActivity() {
        TODO("Not yet implemented")
    }
}