package com.example.notekeeper

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.notekeeper.room.Constant
import com.example.notekeeper.room.Note
import com.example.notekeeper.room.NoteDB
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditActivity : AppCompatActivity() {
    private lateinit var btnSave: Button
    private lateinit var edtTitle: EditText
    private lateinit var edtNote: EditText
    private lateinit var btnUpdate: Button

    private val db by lazy { NoteDB(this) }
    private var noteId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        btnSave = findViewById(R.id.button_save)
        edtTitle = findViewById(R.id.edit_title)
        edtNote = findViewById(R.id.edit_note)
        btnUpdate = findViewById(R.id.button_update)

        setupView()
        setupListener()
    }

    private fun setupListener() {
        btnSave.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                db.noteDao().addNote(
                    Note(0, edtTitle.text.toString(), edtNote.text.toString())
                )
                finish()
            }
        }

        btnUpdate.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                db.noteDao().updateNote(
                    Note(noteId, edtTitle.text.toString(), edtNote.text.toString())
                )
                finish()
            }
        }
    }

    fun setupView() {
        val intentType = intent.getIntExtra("intent_type", 0)
        when (intentType) {
            Constant.TYPE_CREATE -> {

            }
            Constant.TYPE_READ -> {
                btnSave.visibility = View.GONE
                btnUpdate.visibility = View.GONE
                getNote()
            }
            Constant.TYPE_UPDATE -> {
                btnSave.visibility = View.GONE
                btnUpdate.visibility = View.VISIBLE
                getNote()
            }
        }
    }

    fun getNote() {
        noteId = intent.getIntExtra("note_id", 0)
        CoroutineScope(Dispatchers.IO).launch {
            val notes = db.noteDao().getNote(noteId)[0]
            withContext(Dispatchers.Main) {
                edtTitle.setText(notes.title)
                edtNote.setText(notes.note)
            }
        }
    }
}