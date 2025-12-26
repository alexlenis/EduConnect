package com.example.educonnect

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.educonnect.ui.activities.*
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray

class MainActivity : AppCompatActivity() {

    companion object {
        private const val NOTIFICATION_PERMISSION_CODE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestNotificationPermission()
        fetchDailyQuote()

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)
        val navView = findViewById<NavigationView>(R.id.navView)

        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.open,
            R.string.close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_subjects ->
                    startActivity(Intent(this, SubjectActivity::class.java))

                // 🔥 ΕΔΩ Η ΔΙΟΡΘΩΣΗ
                R.id.nav_assignments ->
                    startActivity(Intent(this, AssignmentListActivity::class.java))

                R.id.nav_grades ->
                    startActivity(Intent(this, GradesActivity::class.java))

                R.id.nav_notes ->
                    startActivity(Intent(this, NoteActivity::class.java))

                R.id.nav_calendar ->
                    startActivity(Intent(this, CalendarActivity::class.java))

                R.id.nav_map ->
                    startActivity(Intent(this, MapActivity::class.java))

                R.id.nav_settings ->
                    startActivity(Intent(this, SettingsActivity::class.java))
            }
            drawerLayout.closeDrawers()
            true
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    NOTIFICATION_PERMISSION_CODE
                )
            }
        }
    }

    private fun fetchDailyQuote() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val client = OkHttpClient()
                val request = Request.Builder()
                    .url("https://type.fit/api/quotes")
                    .build()

                val response = client.newCall(request).execute()
                val json = response.body()?.string() ?: return@launch
                JSONArray(json)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
