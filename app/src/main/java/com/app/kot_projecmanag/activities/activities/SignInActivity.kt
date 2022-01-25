package com.app.kot_projecmanag.activities.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import com.app.kot_projecmanag.R
import com.app.kot_projecmanag.databinding.ActivitySignInBinding
import com.app.kot_projecmanag.models.User
import com.google.firebase.auth.FirebaseAuth

class SignInActivity : BaseActivity() {

    private lateinit var binding: ActivitySignInBinding

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        binding.btnSignIn.setOnClickListener {
            signInRegisteredUser()
        }
        setupActionBar()
    }

    fun signInSuccess(user: User){
        hideProgressDialog()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun setupActionBar() {

        setSupportActionBar(binding.toolbarSignInActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }

        binding.toolbarSignInActivity.setNavigationOnClickListener { onBackPressed() }
    }

    private fun signInRegisteredUser(){
        val email: String = binding.etEmailSignIn.text.toString().trim{ it <= ' '}
        val password: String = binding.etPasswordSignIn.text.toString().trim{ it <= ' '}

        if (validateForm(email, password)){
            showProgressDialog(resources.getString(R.string.please_wait))
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this){
                task ->
                hideProgressDialog()
                if (task.isSuccessful){
                    Log.d("Sign in", "signInWithEmail:success")
                    val user = auth.currentUser
                    startActivity(Intent(this, MainActivity::class.java))
                } else {
                    Log.w("Sign in", "signInWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed!", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    private fun validateForm(email: String, password: String) : Boolean{
        return when{
            TextUtils.isEmpty(email)->{
                showErrorSnackBar("Please enter a valid email address")
                false
            }
            TextUtils.isEmpty(password)->{
                showErrorSnackBar("Please enter a valid password")
                false
            } else -> {
                true
            }
        }
    }
}