package com.android.gymaround

import android.app.Application
import android.content.Context

class App: Application() {
    init {
        appliction = this
    }
    companion object {
        private lateinit var appliction: App
        fun getApplicationContext(): Context = appliction.applicationContext
    }
}