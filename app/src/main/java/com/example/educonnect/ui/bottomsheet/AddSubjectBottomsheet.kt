package com.example.educonnect.ui.bottomsheet

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.example.educonnect.R
import com.example.educonnect.data.database.AppDatabase
import com.example.educonnect.data.entity.MapPlace
import com.example.educonnect.data.entity.PlaceType
import com.example.educonnect.data.entity.Subject
import com.example.educonnect.ui.activities.MapActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

class AddSubjectBottomSheet(
    private val subject: Subject?,          // null = ADD, not null = EDIT
    private val onSaved: () -> Unit
) : BottomSheetDialogFragment() {

    private var selectedDateMillis: Long? = null
    private var pickedLat: Double? = null
    private var pickedLng: Double? = null
    private var editingMapPlaceId: Int? = null

    private val pickOnMapLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { res ->
            if (res.resultCode != Activity.RESULT_OK || res.data == null) return@registerForActivityResult

            pickedLat = res.data!!.getDoubleExtra(MapActivity.RESULT_LAT, Double.NaN)
            pickedLng = res.data!!.getDoubleExtra(MapActivity.RESULT_LNG, Double.NaN)

            if (pickedLat!!.isNaN() || pickedLng!!.isNaN()) return@registerForActivityResult

            view?.findViewById<TextView>(R.id.tvPickedLocation)?.text =
                "Picked: %.5f, %.5f".format(pickedLat, pickedLng)
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.bottomsheet_add_subject, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val db = AppDatabase.getDatabase(requireContext())

        val etName = view.findViewById<EditText>(R.id.etSubjectName)
        val etProfessor = view.findViewById<EditText>(R.id.etProfessor)
        val etSchedule = view.findViewById<EditText>(R.id.etSchedule)
        val etSemester = view.findViewById<EditText>(R.id.etSemester)
        val etDate = view.findViewById<EditText>(R.id.etDate)

        val cbPlace = view.findViewById<CheckBox>(R.id.cbPlaceOnMap)
        val btnPick = view.findViewById<Button>(R.id.btnPickOnMap)
        val tvPicked = view.findViewById<TextView>(R.id.tvPickedLocation)

        val btnSave = view.findViewById<Button>(R.id.btnSave)
        val btnCancel = view.findViewById<Button>(R.id.btnCancel)
        val tvTitle = view.findViewById<TextView>(R.id.tvTitle)

        // ===== EDIT MODE =====
        subject?.let { s ->
            tvTitle.text = "Edit Subject"
            btnSave.text = "UPDATE"

            etName.setText(s.name)
            etProfessor.setText(s.professor)
            etSchedule.setText(s.schedule)
            etSemester.setText(s.semester.toString())

            selectedDateMillis = s.dateMillis
            s.dateMillis?.let {
                val c = Calendar.getInstance()
                c.timeInMillis = it
                etDate.setText(
                    "${c.get(Calendar.DAY_OF_MONTH)}/${c.get(Calendar.MONTH) + 1}/${c.get(Calendar.YEAR)}"
                )
            }

            editingMapPlaceId = s.mapPlaceId
            if (editingMapPlaceId != null) {
                cbPlace.isChecked = true
                btnPick.isEnabled = true
                tvPicked.text = "Linked to map place"
            }
        }

        // ===== DATE PICKER
        etDate.setOnClickListener {
            val cal = Calendar.getInstance()
            DatePickerDialog(
                requireContext(),
                { _, y, m, d ->
                    cal.set(y, m, d)
                    selectedDateMillis = cal.timeInMillis
                    etDate.setText("$d/${m + 1}/$y")
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        // ===== MAP
        cbPlace.setOnCheckedChangeListener { _, checked ->
            btnPick.isEnabled = checked
            if (!checked) {
                pickedLat = null
                pickedLng = null
                tvPicked.text = "No location selected"
            }
        }

        btnPick.setOnClickListener {
            val intent = Intent(requireContext(), MapActivity::class.java).apply {
                putExtra(MapActivity.EXTRA_PICK_MODE, true)
            }
            pickOnMapLauncher.launch(intent)
        }

        btnCancel.setOnClickListener { dismiss() }

        // ===== SAVE
        btnSave.setOnClickListener {
            val name = etName.text.toString().trim()
            val semesterText = etSemester.text.toString().trim()

            if (name.isBlank() || semesterText.isBlank()) {
                Toast.makeText(requireContext(), "Fill required fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch(Dispatchers.IO) {

                var mapPlaceId: Int? = editingMapPlaceId

                // ----- MAP PLACE
                if (cbPlace.isChecked && pickedLat != null && pickedLng != null) {

                    // delete old if exists
                    mapPlaceId?.let { id ->
                        db.mapPlaceDao().getById(id)?.let { db.mapPlaceDao().delete(it) }
                    }

                    mapPlaceId = db.mapPlaceDao().insert(
                        MapPlace(
                            title = name,
                            description = etProfessor.text.toString(),
                            latitude = pickedLat!!,
                            longitude = pickedLng!!,
                            type = PlaceType.SUBJECT,
                            subjectId = subject?.id
                        )
                    ).toInt()
                } else if (!cbPlace.isChecked && mapPlaceId != null) {
                    db.mapPlaceDao().getById(mapPlaceId!!)?.let { db.mapPlaceDao().delete(it) }
                    mapPlaceId = null
                }

                // ----- SUBJECT -----
                if (subject == null) {
                    val newId = db.subjectDao().insert(
                        Subject(
                            name = name,
                            professor = etProfessor.text.toString(),
                            schedule = etSchedule.text.toString(),
                            semester = semesterText.toInt(),
                            dateMillis = selectedDateMillis,
                            mapPlaceId = mapPlaceId
                        )
                    ).toInt()

                    mapPlaceId?.let { id ->
                        db.mapPlaceDao().getById(id)?.let {
                            db.mapPlaceDao().update(it.copy(subjectId = newId))
                        }
                    }

                } else {
                    db.subjectDao().update(
                        subject.copy(
                            name = name,
                            professor = etProfessor.text.toString(),
                            schedule = etSchedule.text.toString(),
                            semester = semesterText.toInt(),
                            dateMillis = selectedDateMillis,
                            mapPlaceId = mapPlaceId
                        )
                    )
                }

                launch(Dispatchers.Main) {
                    onSaved()
                    dismiss()
                }
            }
        }
    }
}
