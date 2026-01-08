package com.example.educonnect.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.educonnect.R
import com.example.educonnect.data.database.AppDatabase
import com.example.educonnect.ui.adapters.NoteAdapter
import com.example.educonnect.ui.bottomsheet.AddEditNoteBottomSheet
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class NoteActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var adapter: NoteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note)

        db = AppDatabase.getDatabase(this)

        val recycler = findViewById<RecyclerView>(R.id.recyclerNotes)
        val fab = findViewById<FloatingActionButton>(R.id.fabAddNote)

        recycler.layoutManager = LinearLayoutManager(this)

        adapter = NoteAdapter(
            activity = this,
            notes = emptyList(),
            onUpdate = { note ->
                AddEditNoteBottomSheet(
                    note = note,
                    onSaved = { loadNotes() }
                ).show(supportFragmentManager, "EditNote")
            },
            onDelete = { note ->
                AlertDialog.Builder(this)
                    .setTitle("Delete note")
                    .setMessage("Are you sure?")
                    .setPositiveButton("Yes") { _, _ ->
                        lifecycleScope.launch {
                            db.noteDao().delete(note)
                            loadNotes()
                        }
                    }
                    .setNegativeButton("No", null)
                    .show()
            }
        )

        recycler.adapter = adapter

        fab.setOnClickListener {
            AddEditNoteBottomSheet(
                note = null,
                onSaved = { loadNotes() }
            ).show(supportFragmentManager, "AddNote")
        }

        loadNotes()
    }

    private fun loadNotes() {
        lifecycleScope.launch {
            adapter.updateData(db.noteDao().getAllNotes())
        }
    }
}
