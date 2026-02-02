
package com.guerramath.safetyapp

import android.app.Application
import com.guerramath.safetyapp.auth.session.SessionManager

class SafetyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Inicializa o SessionManager
        SessionManager.init(this)
    }
}