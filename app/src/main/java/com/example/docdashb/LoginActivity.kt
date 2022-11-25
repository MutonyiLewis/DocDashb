package com.example.docdashb

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.docdashb.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.Observable

@SuppressLint("CheckResult")
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        //Auth

        auth = FirebaseAuth.getInstance()

        //username validation
        val usernameStream = RxTextView.textChanges(binding.enterEmail)
            .skipInitialValue()
            .map { username ->
                username.isEmpty()
            }
        usernameStream.subscribe {
            showTextMinimalAlert(it, "Email/Username")
        }

//Password validation
        val passwordStream = RxTextView.textChanges(binding.enterPassword)
            .skipInitialValue()
            .map { password ->
                password.isEmpty()
            }
        passwordStream.subscribe {
            showTextMinimalAlert(it, "Password")
        }

//Button Enable true or false
        val invalidFieldsStream = Observable.combineLatest(
            usernameStream,
            passwordStream,
        ) { usernameInvalid: Boolean, passwordInvalid: Boolean ->
            !usernameInvalid && !passwordInvalid
        }
        invalidFieldsStream.subscribe { isValid ->
            if (isValid) {
                binding.btnLogin.isEnabled = true
                binding.btnLogin.backgroundTintList = ContextCompat.getColorStateList(this, R.color.btn_primary)
            } else {
                binding.btnLogin.isEnabled = false
                binding.btnLogin.backgroundTintList = ContextCompat.getColorStateList(this, android.R.color.darker_gray)
            }
        }

//Click

        binding.btnLogin.setOnClickListener {
            val email = binding.enterEmail.text.toString().trim()
            val password = binding.enterPassword.text.toString().trim()
            loginUser(email, password)
        }
        binding.dontHaveAcc.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

    }

    private fun showTextMinimalAlert(isNotValid: Boolean, text: String) {
        if (text == "Email/Username")
            binding.enterEmail.error = if (isNotValid) "$text Cannot be empty" else null
        else if (text == "Password")
            binding.enterPassword.error = if (isNotValid) "$text Cannot be empty" else null
    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { login ->
                if (login.isSuccessful) {
                    Intent(this,HomeActivity::class.java).also {
                        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(it)
                        Toast.makeText(this,"Login was successful", Toast.LENGTH_SHORT).show()
                    }
                    } else {
                    Toast.makeText(this, login.exception?.message,Toast.LENGTH_SHORT).show()
                }
            }
    }
}