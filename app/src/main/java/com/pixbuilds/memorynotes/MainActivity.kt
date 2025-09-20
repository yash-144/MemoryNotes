package com.pixbuilds.memorynotes

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pixbuilds.memorynotes.ui.theme.MemoryNotesTheme
import java.io.File

class MainActivity : ComponentActivity() {
    private val fileName: String = "saved_text.txt"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val file = File(filesDir, fileName)
            if (!file.exists()) {
                file.createNewFile()
            }
            val savedText = remember { mutableStateOf(file.readText()) }
            val textState = remember { mutableStateOf(savedText.value) }
            val unSavedChanges = textState.value != savedText.value

            fun save() {
                if (textState.value.isBlank()) {
                    Toast.makeText(this, "Empty", Toast.LENGTH_SHORT).show()
                } else {
                    file.writeText(textState.value)
                    savedText.value = textState.value
                    Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show()
                }
            }

            fun delete() {
                if (file.delete()) {
                    textState.value = ""
                    savedText.value = textState.value
                    Toast.makeText(this, "Cleared", Toast.LENGTH_SHORT).show()
                }
            }

            MemoryNotesTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = MaterialTheme.colorScheme.background
                ) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Left: Title
                            Text(
                                "Memory Notes",
                                color = Color.White,
                                fontSize = 22.sp,
                                modifier = Modifier.weight(1f) // takes remaining space
                            )

                            // Right: Status + Count
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    if (unSavedChanges) "Unsaved Changes" else "Changes Saved",
                                    color = Color.LightGray,
                                    fontSize = 12.sp
                                )
                                Text(
                                    "${textState.value.length}",
                                    color = Color.LightGray,
                                    fontSize = 12.sp
                                )
                            }
                        }
                        TextField(
                            value = textState.value,
                            onValueChange = { textState.value = it },
                            label = { Text("Notes") },
                            modifier = Modifier
                                .fillMaxWidth()
//                                .padding(start = 12.dp, end = 12.dp, bottom = 12.dp, top = 8.dp)
                                .weight(1f)
                                .border(
                                    width = 1.dp,
                                    color = if (unSavedChanges) Color(0xFFB71C1C) else Color.Gray,
                                    shape = RoundedCornerShape(8.dp)
                                ),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            )
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth()
                                .padding(top = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = { save() },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Text("Save", color = MaterialTheme.colorScheme.onPrimary)
                            }
                            OutlinedButton(
                                onClick = { delete() },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Clear")
                            }
                        }
                    }
                }
            }
        }
    }
}