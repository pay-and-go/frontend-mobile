package com.example.payandgo

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.widget.TextView
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.coroutines.await
import com.example.payandgo.databinding.ActivityRegisterBinding
import com.example.payandgo.type.UserInput
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

class RegisterActivity : AppCompatActivity() {

    private lateinit var bindingRegister: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingRegister = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(bindingRegister.root)

        val spannableString = SpannableString("¿Ya tienes cuenta? Inicia Sesión")// setting the text as a Spannable
        bindingRegister.login.setText(spannableString, TextView.BufferType.SPANNABLE)
        val spannableText = bindingRegister.login.text as Spannable
        spannableText.setSpan(
            ForegroundColorSpan(Color.parseColor("#c62332")),
            18, 32,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        bindingRegister.btnRegister.setOnClickListener { createUser() }

        bindingRegister.login.setOnClickListener {
            val i = Intent(this, LoginActivity::class.java)
            startActivity(i)
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
            finish()
        }
    }

    fun createUser(){
        val username = bindingRegister.etFirstName.text.toString()
        val lastname = bindingRegister.etLastName.text.toString()
        val identifier = bindingRegister.etIdentifier.text.toString()
        val email = bindingRegister.etMail.text.toString()
        val password = bindingRegister.etPassword.text.toString()

        if(username.isNotEmpty() && lastname.isNotEmpty()
                && identifier.isNotEmpty() && email.isNotEmpty()
                && password.isNotEmpty()){
            println("Holaa")
            val createUserMutation = CreateUserMutation(UserInput(username,lastname, identifier.toInt(),email,password))

            CoroutineScope(Dispatchers.IO).launch {
                val response = try {
//                    apolloClient.mutate(createUserMutation).await()
                    apolloClient.query(AllUsersQuery()).await()
                }  catch (e: Exception){
                    println(e)
                }
                println(createUserMutation.toString())
                println(response?.toString())
            }

            println("ya esta haciendo la mutation")

        }else {
            println("Adioos")
        }

    }
}