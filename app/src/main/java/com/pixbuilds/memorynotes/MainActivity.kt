package com.pixbuilds.memorynotes

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pixbuilds.memorynotes.ui.theme.MemoryNotesTheme
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {
    private val fileName: String = "notes.json"
    private val gson = Gson()

    data class Note(
        val id: Int,
        val title: String,
        val body: String,
        val date: Long = System.currentTimeMillis()
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val darkBlue = Color(0xFF1C2128)
            val surfaceColor = Color(0xFF222831)
            val primaryActionColor = Color(0xFF76ABAE)
            val destructiveActionColor = Color(0xFFE57373)
            val primaryTextColor = Color(0xFFEEEEEE)
            val secondaryTextColor = Color(0xFFAAAAAA)

            fun dateFormat(timestamp: Long): String {
                val pattern = "MMM d, yyyy, h:mm a"
                val simpleDateFormat = SimpleDateFormat(pattern, Locale.getDefault())
                return simpleDateFormat.format(Date(timestamp))
            }

            fun loadNotes(file: File): List<Note> {
                if (!file.exists() || file.readText().isBlank()) {
                    return emptyList()
                }
                val jsonString = file.readText()
                val type = object : TypeToken<List<Note>>() {}.type
                return gson.fromJson(jsonString, type)
            }

            fun saveNotes(file: File, notes: List<Note>) {
                val jsonString = gson.toJson(notes)
                file.writeText(jsonString)
            }

            val showDialog = remember { mutableStateOf(false) }
            val file = File(filesDir, fileName)
            if (!file.exists()) {
                file.createNewFile()
            }
            val editingNote = remember { mutableStateOf(false) }

            val activeNoteId = remember { mutableIntStateOf(0) }

            val notes = remember { mutableStateListOf(*loadNotes(file).toTypedArray()) }

            val savedText = remember { mutableStateOf("") }
            val savedTitle = remember { mutableStateOf("") }
            val titleState = remember { mutableStateOf("") }
            val textState = remember { mutableStateOf("") }
            val unSavedChanges =
                (textState.value != savedText.value) || (titleState.value != savedTitle.value)

            fun updateNotes() {
                val noteIndex = notes.indexOfFirst { it.id == activeNoteId.intValue }

                if (noteIndex != -1) {
                    notes[noteIndex] =
                        notes[noteIndex].copy(body = textState.value, title = titleState.value, date = System.currentTimeMillis())
                    saveNotes(file, notes)

                    savedText.value = textState.value
                    savedTitle.value = titleState.value
                    Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Error: Note not found", Toast.LENGTH_SHORT).show()
                }
            }

            fun deleteNote() {
                val noteIndex = notes.indexOfFirst { it.id == activeNoteId.intValue }
                notes.removeAt(noteIndex)
                saveNotes(file, notes)
                Toast.makeText(this, "Note deleted", Toast.LENGTH_SHORT).show()
                editingNote.value = false
            }

            @Composable
            fun BottomBar() {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                        .padding(bottom = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
                        onClick = { updateNotes() },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(36.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = primaryActionColor,
                            contentColor = darkBlue
                        ),
                        enabled = unSavedChanges
                    ) {
                        Text(
                            "Save",
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                    Button(
                        onClick = { showDialog.value = true },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(36.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = destructiveActionColor,
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            "Clear",
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }
            }

            @Composable
            fun titleField() {
                TextField(
                    value = titleState.value,
                    onValueChange = { titleState.value = it },
                    placeholder = { Text("Title", color = secondaryTextColor) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = surfaceColor,
                        unfocusedContainerColor = surfaceColor,
                        focusedTextColor = primaryTextColor,
                        unfocusedTextColor = primaryTextColor,
                        focusedIndicatorColor = primaryActionColor,
                        unfocusedIndicatorColor = if (unSavedChanges) primaryActionColor.copy(alpha = 0.6f) else Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        errorIndicatorColor = Color.Transparent
                    )
                )
            }

            @Composable
            fun ColumnScope.textField() {
                TextField(
                    value = textState.value,
                    onValueChange = { textState.value = it },
                    placeholder = { Text("What's on your mind?", color = secondaryTextColor) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = surfaceColor,
                        unfocusedContainerColor = surfaceColor,
                        focusedTextColor = primaryTextColor,
                        unfocusedTextColor = primaryTextColor,
                        focusedIndicatorColor = primaryActionColor,
                        unfocusedIndicatorColor = if (unSavedChanges) primaryActionColor.copy(alpha = 0.6f) else Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        errorIndicatorColor = Color.Transparent
                    )
                )
            }

            MemoryNotesTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = darkBlue,
                    bottomBar = {
                        if (editingNote.value) {
                            BottomBar()
                        }
                    },
                    content = { innerPadding ->
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                                .padding(horizontal = 16.dp, vertical = 16.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Memory Notes",
                                    color = primaryTextColor,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.weight(1f)
                                )
                                if (editingNote.value) {
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            if (unSavedChanges) "Unsaved" else "Saved",
                                            color = if (unSavedChanges) primaryActionColor else secondaryTextColor,
                                            fontSize = 13.sp,
                                            fontWeight = if (unSavedChanges) FontWeight.Bold else FontWeight.Normal,
                                        )
                                        Text(
                                            "${textState.value.length} chars",
                                            color = secondaryTextColor,
                                            fontSize = 13.sp
                                        )
                                    }
                                }
                            }

                            HorizontalDivider(
                                modifier = Modifier.padding(bottom = 16.dp),
                                thickness = 1.dp,
                                color = primaryActionColor
                            )

                            if (!editingNote.value) {
                                Column {
                                    LazyColumn(
                                        modifier = Modifier.weight(1f),
                                        verticalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        items(notes) { note ->
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clip(RoundedCornerShape(12.dp))
                                                    .background(surfaceColor)
                                                    .padding(16.dp)
                                                    .clickable {
                                                        savedText.value = note.body
                                                        savedTitle.value = note.title
                                                        titleState.value = note.title
                                                        textState.value = note.body
                                                        editingNote.value = true
                                                        activeNoteId.intValue = note.id
                                                    }
                                            ) {
                                                Column {
                                                    Text(
                                                        text = note.title,
                                                        color = primaryTextColor,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                    Text(
                                                        text = note.body,
                                                        color = secondaryTextColor,
                                                        modifier = Modifier.padding(top = 8.dp),
                                                        maxLines = 2,
                                                        overflow = TextOverflow.Ellipsis
                                                    )
                                                    Text(
                                                        text = dateFormat(note.date),
                                                        color = secondaryTextColor,
                                                        fontSize = 12.sp,
                                                        modifier = Modifier.padding(top = 8.dp)
                                                    )
                                                }
                                            }
                                        }
                                        item {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clip(RoundedCornerShape(12.dp))
                                                    .background(MaterialTheme.colorScheme.secondaryContainer)
                                                    .clickable {
                                                        val newNote = Note(
                                                            System
                                                                .currentTimeMillis()
                                                                .toInt(),
                                                            "New Note",
                                                            ""
                                                        )
                                                        notes.add(
                                                            0,
                                                            newNote
                                                        )
                                                        saveNotes(file, notes)
                                                    }.padding(16.dp), contentAlignment = Alignment.Center
                                            ) {
                                                Text(text = "+ New Note", color = primaryTextColor)
                                            }
                                        }
                                    }
                                }
                            } else {
                                Column(modifier = Modifier.fillMaxSize()) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        IconButton(onClick = {
                                            savedText.value = ""
                                            textState.value = ""
                                            titleState.value = ""
                                            editingNote.value = false
                                            activeNoteId.intValue = 0
                                        }) {
                                            Icon(
                                                Icons.AutoMirrored.Filled.ArrowBack,
                                                contentDescription = "Back",
                                                modifier = Modifier.size(24.dp),
                                                tint = primaryActionColor
                                            )
                                        }
                                        Text(
                                            "Edit Note",
                                            modifier = Modifier.padding(start = 8.dp),
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = primaryTextColor
                                        )
                                    }
                                    titleField()
                                    textField()
                                }
                            }

                            if (showDialog.value) {
                                AlertDialog(
                                    onDismissRequest = { showDialog.value = false },
                                    title = { Text("Confirm Delete?", color = primaryTextColor) },
                                    text = {
                                        Text(
                                            "Are you sure you want to permanently delete this note?",
                                            color = secondaryTextColor
                                        )
                                    },
                                    containerColor = surfaceColor,
                                    confirmButton = {
                                        TextButton(
                                            onClick = {
                                                deleteNote()
                                                showDialog.value = false
                                            }
                                        ) {
                                            Text("Yes, Delete", color = destructiveActionColor)
                                        }
                                    },
                                    dismissButton = {
                                        TextButton(
                                            onClick = { showDialog.value = false }
                                        ) {
                                            Text("Cancel", color = primaryActionColor)
                                        }
                                    }
                                )
                            }
                        }
                    })
            }
        }
    }
}
