package com.example.payandgo

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.apollographql.apollo.coroutines.await
import com.example.payandgo.utils.InitApplication.Companion.prefs
import com.example.payandgo.databinding.ActivityLoginBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var bindingLogin: ActivityLoginBinding
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingLogin = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(bindingLogin.root)

        val spannableString = SpannableString("¿No tienes cuenta? Registrate")// setting the text as a Spannable
        bindingLogin.register.setText(spannableString, TextView.BufferType.SPANNABLE)
        val spannableText = bindingLogin.register.text as Spannable
        spannableText.setSpan(
            ForegroundColorSpan(Color.parseColor("#c62332")),
            18, 29,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        checkUserValues()
        bindingLogin.buttonLogin.setOnClickListener {
            accessToInfo(this)
        }

        bindingLogin.register.setOnClickListener {
            val i = Intent(this, RegisterActivity::class.java)
            startActivity(i)
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
            finish()
        }
    }

    fun checkUserValues(){
        if (prefs.getMail().isNotEmpty()){
            accesApp()
        }
    }

    fun accessToInfo(ctx:Context){
        val userName = bindingLogin.etUserName.text.toString()
        val password = bindingLogin.etPassword.text.toString()
        if(userName.isNotEmpty()
                && password.isNotEmpty()){
            //Autenticarlo
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val response = apolloClient.mutate(LoginMutation(userName, password)).await()
                    if(response?.data?.login == null){
                        Toast.makeText(ctx, "No se encontro el usuario", Toast.LENGTH_SHORT).show()
                    }else{
                        val token = response?.data?.login?.token
                        val respQuery = apolloClient.query(GetUserQuery(userName,token!!)).await()
                        val user = respQuery.data?.getUser
                        prefs.saveId(user?.id!!)
                        prefs.saveFirstName(user.first_name)
                        prefs.saveLastName(user.last_name)
                        prefs.saveCC(user.cedula)
                        prefs.saveMail(user.mail)
                        prefs.savePassword(password)
                        prefs.saveToken(token)
                        accesApp()
                    }
                }  catch (e: Exception){
                    println("exception $e")
                }
            }
        }else {
            Toast.makeText(ctx, "Tienes que llenar todos los campos", Toast.LENGTH_SHORT).show()
        }
    }

    fun accesApp(){
        val i = Intent(this, MainActivity::class.java)
        startActivity(i)
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
        finish()
    }
}