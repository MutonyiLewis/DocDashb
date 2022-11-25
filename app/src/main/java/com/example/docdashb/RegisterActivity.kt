package com.example.docdashb

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.docdashb.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.Observable
import java.util.*

@SuppressLint("checkResult")

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)



 //Auth
        auth = FirebaseAuth.getInstance()

// Full name validation
        val nameStream = RxTextView.textChanges(binding.enterFullname)
            .skipInitialValue()
            .map { name ->
                name.isEmpty()
            }
        nameStream.subscribe {
            showNameExistAlert(it)
        }

//EMAIL validation
        val emailStream = RxTextView.textChanges(binding.enterEmail)
            .skipInitialValue()
            .map { email ->
                !Patterns.EMAIL_ADDRESS.matcher(email).matches()
            }
        emailStream.subscribe {
            showEmailValidAlert(it)
        }

//username validation
        val usernameStream = RxTextView.textChanges(binding.enterUsername)
            .skipInitialValue()
            .map { username ->
                username.length < 8
            }
        usernameStream.subscribe {
            showTextMinimalAlert(it, "Username")
        }

//Password validation
        val passwordStream = RxTextView.textChanges(binding.enterPassword)
            .skipInitialValue()
            .map { password ->
                password.length < 8
            }
        passwordStream.subscribe {
            showTextMinimalAlert(it, "Password")
        }

//Confirm password
        val passwordConfirmStream = Observable.merge(
            RxTextView.textChanges(binding.enterConfirmpassword)
                .skipInitialValue()
                .map { password ->
                    password.toString() != binding.enterConfirmpassword.text.toString()
                },
            RxTextView.textChanges(binding.enterConfirmpassword)
                .skipInitialValue()
                .map { confirmPassword ->
                    confirmPassword.toString() != binding.enterPassword.text.toString()
                }
        )
        passwordConfirmStream.subscribe {
            showPasswordConfirm(it)
        }

//Button Enable true or false
        val invalidFieldsStream = Observable.combineLatest(
            nameStream,
            emailStream,
            usernameStream,
            passwordStream,
            passwordConfirmStream
        ) { nameInvalid: Boolean, emailInvalid: Boolean, usernameInvalid: Boolean, passwordInvalid: Boolean, passwordConfirmInvalid: Boolean ->
            !nameInvalid && !emailInvalid && !usernameInvalid && !passwordInvalid && !passwordConfirmInvalid
        }
        invalidFieldsStream.subscribe { isValid ->
            if (isValid) {
                binding.btnRegister.isEnabled = true
                binding.btnRegister.backgroundTintList = ContextCompat.getColorStateList(this, R.color.btn_primary)
            } else {
                binding.btnRegister.isEnabled = false
                binding.btnRegister.backgroundTintList = ContextCompat.getColorStateList(this, android.R.color.darker_gray)
            }
        }

//Clicks

        binding.btnRegister.setOnClickListener {
           val email = binding.enterEmail.text.toString().trim()
            val password = binding.enterPassword.text.toString().trim()
            registerUser(email, password)
        }
        binding.haveAcc.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun showNameExistAlert(isNotValid: Boolean){
        binding.enterFullname.error = if (isNotValid) "Please enter a valid name" else null
    }

    private fun showTextMinimalAlert(isNotValid: Boolean, text: String) {
        if (text == "Username")
            binding.enterUsername.error = if (isNotValid) "$text Must be more than 6 letters" else null
        else if (text == "Password")
            binding.enterPassword.error = if (isNotValid) "$text must be more than 8 letters" else null
    }

    private fun showEmailValidAlert(isNotValid: Boolean) {
        binding.enterEmail.error = if (isNotValid) "Invalid email" else null
    }

    private fun showPasswordConfirm(isNotValid: Boolean) {
        binding.enterConfirmpassword.error = if (isNotValid) "Password is not same" else null
    }

    private fun registerUser(email: String, password: String){
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this){
                if (it.isSuccessful) {
                    startActivity(Intent(this,LoginActivity::class.java))
                    Toast.makeText(this,"Registration was successful",Toast.LENGTH_SHORT).show()
                } else{
                    Toast.makeText(this, it.exception?.message,Toast.LENGTH_SHORT).show()
                }
            }
    }
}