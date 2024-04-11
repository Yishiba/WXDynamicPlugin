package com.wgllss.sample.features_ui.page.other2.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import com.wgllss.dynamic.plugin.library.HostServiceDelegate

class MyService2 : HostServiceDelegate {

    private lateinit var context: Context

    override fun attachBaseContext(newBase: Context) {
        this.context = newBase
        android.util.Log.e("MyService2", "attachBaseContext")
    }

    override fun onBind(intent: Intent?): IBinder? {
        android.util.Log.e("MyService2", "onBind")
        return null
    }

    override fun onCreate() {
        android.util.Log.e("MyService2", "onCreate")
    }

    override fun onStart(intent: Intent?, startId: Int) {
        val str = intent?.getStringExtra("service_key") ?: ""
        android.util.Log.e("MyService2", "onStart: str :$str")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val str = intent?.getStringExtra("service_key") ?: ""
        android.util.Log.e("MyService2", "onStartCommand: str :$str")


        return Service.START_NOT_STICKY
    }

    override fun onUnbind(intent: Intent?): Boolean {
        val str = intent?.getStringExtra("service_key") ?: ""
        android.util.Log.e("MyService2", "onUnbind: str :$str")
        return false
    }

    override fun onDestroy() {
        android.util.Log.e("MyService2", "onDestroy: ")
    }
}