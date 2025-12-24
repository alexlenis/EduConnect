package com.example.educonnect.ui.activities

import android.os.Bundle
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import com.example.educonnect.R

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val darkModeSwitch = findViewById<Switch>(R.id.switchDarkMode)
        val notificationsSwitch = findViewById<Switch>(R.id.switchNotifications)

        val prefs = getSharedPreferences("settings", MODE_PRIVATE)

        // Load saved values
        darkModeSwitch.isChecked = prefs.getBoolean("dark_mode", false)
        notificationsSwitch.isChecked = prefs.getBoolean("notifications", true)

        darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("dark_mode", isChecked).apply()
        }

        notificationsSwitch.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("notifications", isChecked).apply()
        }
    }
}
