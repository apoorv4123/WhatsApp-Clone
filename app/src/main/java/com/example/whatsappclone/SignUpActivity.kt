package com.example.whatsappclone

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.whatsappclone.models.User
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity() {

    val storage by lazy {
        FirebaseStorage.getInstance()
    }
    val auth by lazy {
        FirebaseAuth.getInstance()
    }
    val database by lazy {
        FirebaseFirestore.getInstance()
    }
    lateinit var downloadUrl: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        userImgView.setOnClickListener {
            checkPermissionForImage()
            // Firebase Extensions - Image Thumbnail
        }

        nextBtn.setOnClickListener {
            nextBtn.isEnabled = false
            val name = nameEt.text.toString()
            if(name.isEmpty()){
                Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show()
            }else if(!::downloadUrl.isInitialized){// If image is not set
                Toast.makeText(this, "Image cannot be empty", Toast.LENGTH_SHORT).show()
            }else{
                // Upload the user
                val user = User(name,downloadUrl,downloadUrl,auth.uid!!)
                database.collection("users").document(auth.uid!!).set(user).addOnSuccessListener {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }.addOnFailureListener {
                    nextBtn.isEnabled = true
                }
            }
        }

    }

    // disable back button
    override fun onBackPressed() {

    }


    private fun checkPermissionForImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if ((checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
                && (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
            ) {
                val permission = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                val permissionWrite = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)

                requestPermissions(
                    permission,
                    1001
                )// GIVE AN INTEGER VALUE FOR PERMISSION_CODE_READ LIKE 1001
                requestPermissions(
                    permissionWrite,
                    1002
                ) //// GIVE AN INTEGER VALUE FOR PERMISSION_CODE_WRITE LIKE 1002
            } else {
                pickImageFromGallery()
            }
        }
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK) // implicit intent
        intent.type = "image/*" // to pick only images
        startActivityForResult(
            intent,
            1000
        )// This request code will be checked when you want to get the result from another activity
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == 1000) {// If something was picked
            // up successfully, get the image uri(path of the image picked) inside the data
            data?.data?.let {
                userImgView.setImageURI(it)
                uploadImage(it)
            }
        }
    }

    private fun uploadImage(filePath: Uri) {
        nextBtn.isEnabled =
            false // disable NEXT button bcoz you don't wanna get disturbed while upload is in progress
        // Start the uploading process
        // Give this image a path and a name
        val ref = storage.reference.child("uploads/" + auth.uid.toString())// get reference
        val uploadTask = ref.putFile(filePath)
        uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            return@Continuation ref.downloadUrl// downloadUrl, which we get after uploading the image
        }).addOnCompleteListener { task ->
            nextBtn.isEnabled = true // Button NEXT will be enabled as soon as upload task is done.
            if (task.isSuccessful) {
                downloadUrl = task.result.toString()
                Log.i("URL", "downloadUrl: $downloadUrl")
            } else {
                // Handle Failures
            }
        }.addOnFailureListener {
            // Exception handling. Show some toast or show some error.
            // Error and exception may be bcoz of internet issue in device
            // of if you've not enabled download storage. So, enable these here
        }

    }

}