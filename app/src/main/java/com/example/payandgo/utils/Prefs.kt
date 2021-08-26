package com.example.payandgo.utils

import android.content.Context

class Prefs(context: Context) {

    val SHARED_NAME = "MyDB"
    val SHARED_USER_ID = "user_id"
    val SHARED_USER_NAME = "user_name"
    val SHARED_USER_LASTNAME = "user_lastname"
    val SHARED_USER_CC = "user_cc"
    val SHARED_USER_MAIL = "user_mail"
    val SHARED_USER_PASSWORD = "user_password"
    val SHARED_USER_TOKEN = "user_token"
    val storage = context.getSharedPreferences(SHARED_NAME, 0)

    fun saveId(id: Int){
        storage.edit().putInt(SHARED_USER_ID, id).apply()
    }

    fun getId(): Int {
        return storage.getInt(SHARED_USER_ID, -1)
    }

    fun saveFirstName(name: String){
        storage.edit().putString(SHARED_USER_NAME, name).apply()
    }

    fun getFistName(): String {
        return storage.getString(SHARED_USER_NAME, "")!!
    }

    fun saveLastName(name: String){
        storage.edit().putString(SHARED_USER_LASTNAME, name).apply()
    }

    fun getLastName(): String {
        return storage.getString(SHARED_USER_LASTNAME, "")!!
    }

    fun saveCC(cc: Int){
        storage.edit().putInt(SHARED_USER_CC, cc).apply()
    }

    fun getCC(): Int {
        return storage.getInt(SHARED_USER_CC, -1)
    }

    fun saveMail(mail: String){
        storage.edit().putString(SHARED_USER_MAIL, mail).apply()
    }

    fun getMail(): String {
        return storage.getString(SHARED_USER_MAIL, "")!!
    }

    fun savePassword(password: String){
        storage.edit().putString(SHARED_USER_PASSWORD, password).apply()
    }

    fun getPassword(): String {
        return storage.getString(SHARED_USER_PASSWORD, "")!!
    }

    fun saveToken(token: String){
        storage.edit().putString(SHARED_USER_TOKEN, token).apply()
    }

    fun getToken(): String {
        return storage.getString(SHARED_USER_TOKEN, "")!!
    }

    fun wipe(){
        storage.edit().clear().apply()
    }
}