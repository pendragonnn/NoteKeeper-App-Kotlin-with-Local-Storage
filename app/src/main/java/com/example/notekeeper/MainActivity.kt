package com.example.notekeeper

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.notekeeper.room.Constant
import com.example.notekeeper.room.Note
import com.example.notekeeper.room.NoteDB
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private lateinit var btnCreate: Button
    lateinit var noteAdapter: NoteAdapter
    lateinit var rcNote: RecyclerView
    private val db by lazy { NoteDB(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupListener()
        setupRecyclerView()
    }

    private fun setupListener() {
        btnCreate = findViewById(com.example.notekeeper.R.id.button_create)
        btnCreate.setOnClickListener {
            intentEdit(Constant.TYPE_CREATE, 0)
        }
    }

    fun intentEdit(intentType: Int, noteId: Int) {
        startActivity(
            Intent(this, EditActivity::class.java)
                .putExtra("intent_type", intentType)
                .putExtra("note_id", noteId)
        )
    }

    private fun setupRecyclerView() {
        noteAdapter = NoteAdapter(arrayListOf(), object : NoteAdapter.OnAdapterListener{
            override fun onClick(note: Note) {
                // red detail note
                intentEdit(Constant.TYPE_READ, note.id)
            }

            override fun onUpdate(note: Note) {
                intentEdit(Constant.TYPE_UPDATE, note.id)
            }

            override fun onDelete(note: Note) {
                deleteDialog(note)
            }
        })
        rcNote = findViewById(R.id.list_note)
        rcNote.apply {
            layoutManager = LinearLayoutManager(applicationContext)
            adapter = noteAdapter
        }
    }

    override fun onStart() {
        super.onStart()
        loadNote()
    }

    fun loadNote() {
        CoroutineScope(Dispatchers.IO).launch {
            val notes = db.noteDao().getNotes()
            withContext(Dispatchers.Main) {
                noteAdapter.setData(notes)
            }
        }
    }

    private fun deleteDialog(note: Note) {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.apply {
            setTitle("Confirm Delete")
            setMessage("Are you sure want to delete this note?")
            setNegativeButton("Cancel") { dialogInterface, i ->
                dialogInterface.dismiss()
            }
            setPositiveButton("Delete") { dialogInterface, i ->
                dialogInterface.dismiss()
                CoroutineScope(Dispatchers.IO).launch {
                    db.noteDao().deleteNote(note)
                    loadNote()
                }
            }
        }
        alertDialog.show()
    }
}