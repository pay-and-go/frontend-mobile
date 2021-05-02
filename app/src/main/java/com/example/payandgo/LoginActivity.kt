package com.example.payandgo

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Typeface.BOLD
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.apollographql.apollo.coroutines.await
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

        bindingLogin.buttonLogin.setOnClickListener {
//            accessToInfo(this)
            val i = Intent(this, InRouteActivity::class.java)
            startActivity(i)
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
            finish()
        }

        bindingLogin.register.setOnClickListener {
            val i = Intent(this, RegisterActivity::class.java)
            startActivity(i)
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
            finish()
        }
    }

    fun accessToInfo(ctx:Context){
        if(bindingLogin.etUserName.text.toString().isNotEmpty()
                && bindingLogin.etPassword.text.toString().isNotEmpty()){
            //Verificar si esta en la db
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = apolloClient.query(AllUsersQuery()).await()
                    val arrUsers = response?.data?.allUsers
                    if (arrUsers != null) {
                        val userEmail = bindingLogin.etUserName.text.toString()
                        val userPass = bindingLogin.etPassword.text.toString()
                        var found = false
                        for (user in arrUsers){
                            println("test: ${user?.mail == userEmail && user?.password == userPass} " +
                                    " email ${user?.mail} password ${user?.password}")
                            if(user?.mail == userEmail && user?.password == userPass){
                                found = true
                                val i = Intent(ctx, MapsActivity::class.java)
                                startActivity(i)
                                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
                                finish()
                            }
                        }
                        if (!found){
                            Toast.makeText(ctx, "No se encontro el usuario", Toast.LENGTH_SHORT).show()
                        }
                    }
                    println("Responsee ${arrUsers}")
                }  catch (e: Exception){
                    println("exception $e")
                }
            }
        }else {
            Toast.makeText(ctx, "Tienes que llenar todos los campos", Toast.LENGTH_SHORT).show()
        }
    }
}