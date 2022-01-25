package com.app.kot_projecmanag.activities.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.app.kot_projecmanag.R
import com.app.kot_projecmanag.databinding.ActivityMyProfileBinding
import com.app.kot_projecmanag.firebase.FirestoreClass
import com.app.kot_projecmanag.models.User
import com.app.kot_projecmanag.utils.Constants
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.IOException

class MyProfileActivity : BaseActivity() {

      private var mSelectedImageFileUri: Uri? = null
    private var mProfileImageURL: String =""
    private lateinit var mUserDetails: User

    private lateinit var binding: ActivityMyProfileBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()

        FirestoreClass().loadUserData(this)

        binding.civUserImageMyProfile.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED){
                    Constants.showImageChooser(this)
            } else {
                ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                Constants.READ_STORAGE_PERMISSION_CODE)
            }
        }

        binding.btnUpdateMyProfile.setOnClickListener {
            if (mSelectedImageFileUri != null){
                uploadUserImage()
            }else{
                showProgressDialog(resources.getString(R.string.please_wait))
                updateUserProfileData()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == Constants.READ_STORAGE_PERMISSION_CODE){
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Constants.showImageChooser(this)
            }
        } else {
            Toast.makeText(this, "You have denied permissions for storage. " +
                    "You can go to the app setting and enable them", Toast.LENGTH_LONG).show()
        }
    }

    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == Constants.PICK_IMAGE_REQUEST_CODE
            && data!!.data != null){
            mSelectedImageFileUri = data.data

            try {
                var userImage = findViewById<ImageView>(R.id.civUserImageMyProfile)
                Glide
                    .with(this@MyProfileActivity)
                    .load(mSelectedImageFileUri)
                    .fitCenter()
                    .placeholder(R.drawable.ic_user_place_holder)
                    .into(userImage)
            } catch (e: IOException){
                e.printStackTrace()
            }
        }
    }

    fun setUserDataInUI(user: User){
        mUserDetails = user
        var userImage = findViewById<ImageView>(R.id.civUserImageMyProfile)
        Glide
            .with(this@MyProfileActivity)
            .load(user.image)
            .fitCenter()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(userImage)

        binding.etNameMyProfile.setText(user.name)
        binding.etEmailMyProfile.setText(user.email)
        if (user.mobile != 0L){
            binding.etMobileMyProfile.setText(user.mobile.toString())
        }
    }

    private fun setupActionBar(){
        val toolbarMyProfileActivity =
            findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_my_profile_activity)
        setSupportActionBar(toolbarMyProfileActivity)
        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = resources.getString(R.string.my_profile_title)
        }

        binding.toolbarMyProfileActivity.setNavigationOnClickListener { onBackPressed() }
    }

    private fun updateUserProfileData(){
        val userHashMap = HashMap<String, Any>()

        if (mProfileImageURL.isNotEmpty() && mProfileImageURL != mUserDetails.image){
            userHashMap[Constants.IMAGE] = mProfileImageURL
        }

        if (binding.etNameMyProfile.text.toString() != mUserDetails.name){
            userHashMap[Constants.NAME] = binding.etNameMyProfile.text.toString()
        }

        if (binding.etMobileMyProfile.text.toString() != mUserDetails.mobile.toString()){
            userHashMap[Constants.MOBILE] = binding.etMobileMyProfile.text.toString().toLong()
        }

        FirestoreClass().updateUserProfileData(this, userHashMap)
    }

    private fun uploadUserImage(){
        showProgressDialog(resources.getString(R.string.please_wait))

        if (mSelectedImageFileUri != null){

            val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
                "USER IMAGE " + System.currentTimeMillis()
                        + "." + Constants.getFileExtension(this, mSelectedImageFileUri))

            sRef.putFile(mSelectedImageFileUri!!).addOnSuccessListener {
                taskSnapshot ->
                Log.i("Firebase Image URL",
                    taskSnapshot.metadata!!.reference!!.downloadUrl.toString())
                taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                    uri ->
                    Log.i("Downloadable Image URL", uri.toString())
                    mProfileImageURL = uri.toString()

                    updateUserProfileData()
                }
            }.addOnFailureListener{
                exception ->
                Toast.makeText(this@MyProfileActivity, exception.message,
                Toast.LENGTH_LONG).show()
                hideProgressDialog()
            }
        }
    }

    fun profileUpdateSuccess(){
        hideProgressDialog()

        setResult(Activity.RESULT_OK)
        finish()
    }

}