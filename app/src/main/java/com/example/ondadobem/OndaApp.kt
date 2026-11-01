package com.example.ondadobem

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import org.osmdroid.config.Configuration
import java.io.File

class OndaApp : Application() {

    override fun onCreate() {
        super.onCreate()

        val config = Configuration.getInstance()
        config.load(this, getSharedPreferences("osmdroid", Context.MODE_PRIVATE))
        config.userAgentValue = packageName
        config.osmdroidBasePath = File(cacheDir, "osmdroid")
        config.osmdroidTileCache = File(cacheDir, "osmdroid/tiles")

        AppCompatDelegate.setDefaultNightMode(Armazenamento.modoTema(this))
    }
}
