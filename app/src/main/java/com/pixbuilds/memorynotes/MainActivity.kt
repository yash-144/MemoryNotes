package com.pixbuilds.memorynotes

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.pixbuilds.memorynotes.ui.theme.MemoryNotesTheme
import java.io.File

class MainActivity : ComponentActivity() {
    private val fileName: String = "saved_text.txt"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val textState = remember { mutableStateOf("") }
            val file = File(filesDir, fileName)
            if (file.exists()) {
                textState.value = file.readText()
            }

            fun save() {
                if (textState.value.isBlank()) {
                    Toast.makeText(this, "Empty", Toast.LENGTH_SHORT).show()
                } else {
                    file.writeText(textState.value)
                    Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show()
                }
            }

            fun delete() {
                if (file.delete()) {
                    textState.value = ""
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
                        TextField(
                            value = textState.value,
                            onValueChange = { textState.value = it },
                            label = { Text("Notes") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
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
                            modifier = Modifier.fillMaxWidth(),
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