package com.example.payandgo

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface.BOLD
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.payandgo.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var bindingLogin: ActivityLoginBinding

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
            val i = Intent(this, MapsActivity::class.java)
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

    fun initSesion(){
        bindingLogin.buttonLogin.setOnClickListener{ accessToInfo() }
    }

    fun accessToInfo(){
        if(bindingLogin.etUserName.text.toString().isNotEmpty()){
            //Guardar el usuario

        }else {

        }
    }
}