package com.example.educonnect.ui.activities

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.educonnect.R
import com.example.educonnect.data.database.AppDatabase
import com.example.educonnect.data.entity.MapPlace
import com.example.educonnect.data.entity.PlaceType
import com.example.educonnect.data.entity.Subject
import com.example.educonnect.ui.adapters.SubjectAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class SubjectActivity : AppCompatActivity() {

    private lateinit var adapter: SubjectAdapter
    private lateinit var db: AppDatabase

    private var editingSubject: Subject? = null

    private var selectedDateMillis: Long? = null

    // προσωρινά κρατάμε picked lat/lng μέχρι να πατήσει Save
    private var pickedLat: Double? = null
    private var pickedLng: Double? = null

    private val pickOnMapLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { res ->
            if (res.resultCode != RESULT_OK || res.data == null) return@registerForActivityResult
            pickedLat = res.data!!.getDoubleExtra(MapActivity.RESULT_LAT, 0.0)
            pickedLng = res.data!!.getDoubleExtra(MapActivity.RESULT_LNG, 0.0)

            val tvPicked = findViewById<TextView>(R.id.tvPickedLocation)
            tvPicked.text = "Picked: %.5f, %.5f".format(pickedLat, pickedLng)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subject)

        db = AppDatabase.getDatabase(this)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerSubjects)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val etName = findViewById<EditText>(R.id.etSubjectName)
        val etProfessor = findViewById<EditText>(R.id.etProfessor)
        val etSchedule = findViewById<EditText>(R.id.etSchedule)
        val etSemester = findViewById<EditText>(R.id.etSemester)
        val etDate = findViewById<EditText>(R.id.etSubjectDate)

        val cbPlace = findViewById<CheckBox>(R.id.cbPlaceOnMap)
        val btnPick = findViewById<Button>(R.id.btnPickOnMap)
        val tvPicked = findViewById<TextView>(R.id.tvPickedLocation)

        val btnSave = findViewById<Button>(R.id.btnSave)

        // Date picker
        etDate.setOnClickListener {
            val cal = Calendar.getInstance()
            DatePickerDialog(
                this,
                { _, y, m, d ->
                    cal.set(y, m, d, 0, 0, 0)
                    selectedDateMillis = cal.timeInMillis
                    etDate.setText("$d/${m + 1}/$y")
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        cbPlace.setOnCheckedChangeListener { _, isChecked ->
            btnPick.isEnabled = isChecked
            if (!isChecked) {
                pickedLat = null
                pickedLng = null
                tvPicked.text = "No location selected"
            }
        }

        btnPick.setOnClickListener {
            val intent = Intent(this, MapActivity::class.java).apply {
                putExtra(MapActivity.EXTRA_PICK_MODE, true)
            }
            pickOnMapLauncher.launch(intent)
        }

        adapter = SubjectAdapter(
            emptyList(),
            onDelete = { subject ->
                AlertDialog.Builder(this)
                    .setTitle("Delete Subject")
                    .setMessage("Are you sure?")
                    .setPositiveButton("Yes") { _, _ ->
                        lifecycleScope.launch(Dispatchers.IO) {
                            // αν έχει mapPlaceId, σβήνουμε και το place (προαιρετικό)
                            subject.mapPlaceId?.let { id ->
                                db.mapPlaceDao().getById(id)?.let { db.mapPlaceDao().delete(it) }
                            }
                            db.subjectDao().delete(subject)
                            loadSubjects()
                        }
                    }
                    .setNegativeButton("No", null)
                    .show()
            },
            onUpdate = { subject ->
                editingSubject = subject

                etName.setText(subject.name)
                etProfessor.setText(subject.professor)
                etSchedule.setText(subject.schedule)
                etSemester.setText(subject.semester.toString())

                // date
                selectedDateMillis = subject.dateMillis
                subject.dateMillis?.let {
                    val c = Calendar.getInstance()
                    c.timeInMillis = it
                    etDate.setText("${c.get(Calendar.DAY_OF_MONTH)}/${c.get(Calendar.MONTH) + 1}/${c.get(Calendar.YEAR)}")
                } ?: run { etDate.setText("") }

                // map link (μόνο ένδειξη)
                if (subject.mapPlaceId != null) {
                    cbPlace.isChecked = true
                    btnPick.isEnabled = true
                    tvPicked.text = "Linked to map place id: ${subject.mapPlaceId}"
                } else {
                    cbPlace.isChecked = false
                    btnPick.isEnabled = false
                    tvPicked.text = "No location selected"
                }

                // δεν ξέρουμε lat/lng αν δεν ξαναδιαλέξεις (αν θες, το φορτώνουμε μετά)
                pickedLat = null
                pickedLng = null
            }
        )

        recyclerView.adapter = adapter
        loadSubjects()

        btnSave.setOnClickListener {
            val name = etName.text.toString().trim()
            val professor = etProfessor.text.toString().trim()
            val schedule = etSchedule.text.toString().trim()
            val semesterText = etSemester.text.toString().trim()

            if (name.isBlank() || semesterText.isBlank()) {
                Toast.makeText(this, "Fill required fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch(Dispatchers.IO) {
                var mapPlaceId: Int? = editingSubject?.mapPlaceId

                // αν θέλει place και έχει επιλέξει νέο σημείο -> δημιουργούμε MapPlace SUBJECT
                if (cbPlace.isChecked && pickedLat != null && pickedLng != null) {

                    // αν υπήρχε παλιό, το σβήνουμε για να μην γεμίζει το db
                    mapPlaceId?.let { oldId ->
                        db.mapPlaceDao().getById(oldId)?.let { db.mapPlaceDao().delete(it) }
                    }

                    val newId = db.mapPlaceDao().insert(
                        MapPlace(
                            title = name,
                            description = if (professor.isNotBlank()) professor else schedule,
                            latitude = pickedLat!!,
                            longitude = pickedLng!!,
                            type = PlaceType.SUBJECT,
                            subjectId = editingSubject?.id // αν είναι update
                        )
                    ).toInt()

                    mapPlaceId = newId
                }

                if (editingSubject == null) {
                    val newSubjectId = db.subjectDao().insert(
                        Subject(
                            name = name,
                            professor = professor,
                            schedule = schedule,
                            semester = semesterText.toInt(),
                            dateMillis = selectedDateMillis,
                            mapPlaceId = mapPlaceId
                        )
                    ).toInt()

                    // αν φτιάξαμε mapPlace πριν το insert (σπάνιο εδώ), μπορούμε να κάνουμε update subjectId
                    mapPlaceId?.let { id ->
                        val place = db.mapPlaceDao().getById(id)
                        if (place != null && place.subjectId == null) {
                            db.mapPlaceDao().update(place.copy(subjectId = newSubjectId))
                        }
                    }

                } else {
                    db.subjectDao().update(
                        editingSubject!!.copy(
                            name = name,
                            professor = professor,
                            schedule = schedule,
                            semester = semesterText.toInt(),
                            dateMillis = selectedDateMillis,
                            mapPlaceId = mapPlaceId
                        )
                    )
                    editingSubject = null
                }

                withContext(Dispatchers.Main) {
                    clearUI(etName, etProfessor, etSchedule, etSemester, etDate, cbPlace, tvPicked, btnPick)
                }
                loadSubjects()
            }
        }
    }

    private fun loadSubjects() {
        lifecycleScope.launch(Dispatchers.IO) {
            val subjects = db.subjectDao().getAllSubjects()
            withContext(Dispatchers.Main) { adapter.updateData(subjects) }
        }
    }

    private fun clearUI(
        etName: EditText,
        etProfessor: EditText,
        etSchedule: EditText,
        etSemester: EditText,
        etDate: EditText,
        cbPlace: CheckBox,
        tvPicked: TextView,
        btnPick: Button
    ) {
        etName.text.clear()
        etProfessor.text.clear()
        etSchedule.text.clear()
        etSemester.text.clear()
        etDate.text.clear()

        selectedDateMillis = null
        pickedLat = null
        pickedLng = null

        cbPlace.isChecked = false
        btnPick.isEnabled = false
        tvPicked.text = "No location selected"
    }
}
