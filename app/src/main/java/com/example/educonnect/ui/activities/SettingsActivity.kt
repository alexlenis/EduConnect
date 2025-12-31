package com.example.educonnect.ui.activities

import android.os.Bundle
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import com.example.educonnect.R
import com.example.educonnect.ui.theme.ThemeHelper

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val prefs = getSharedPreferences("settings", MODE_PRIVATE)

        val radioGroup = findViewById<RadioGroup>(R.id.radioGroupTheme)
        val radioLight = findViewById<RadioButton>(R.id.radioLight)
        val radioDark = findViewById<RadioButton>(R.id.radioDark)
        val radioAuto = findViewById<RadioButton>(R.id.radioAuto)

        when (prefs.getString("theme", "light")) {
            "light" -> radioLight.isChecked = true
            "dark" -> radioDark.isChecked = true
            "auto" -> radioAuto.isChecked = true
        }

        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radioLight -> {
                    ThemeHelper.saveTheme(this, "light")
                }

                R.id.radioDark -> {
                    ThemeHelper.saveTheme(this, "dark")
                }

                R.id.radioAuto -> {
                    ThemeHelper.saveTheme(this, "auto")
                }
            }

            // εφαρμόζει άμεσα το theme
            ThemeHelper.applyTheme(this)
        }
    }
}
