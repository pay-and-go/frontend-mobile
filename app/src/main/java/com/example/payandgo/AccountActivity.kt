package com.example.payandgo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.payandgo.databinding.ActivityAccountBinding
import com.example.payandgo.databinding.ActivityLoginBinding

class AccountActivity : AppCompatActivity() {
    private lateinit var bindingAccount: ActivityAccountBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bindingAccount = ActivityAccountBinding.inflate(layoutInflater)

        setContentView(bindingAccount.root)

        bindingAccount.acUpdateDatabtn

    }
}