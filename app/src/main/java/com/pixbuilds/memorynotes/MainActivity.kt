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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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
            val darkBlue = Color(0xFF1C2128)
            val surfaceColor = Color(0xFF222831)
            val primaryActionColor = Color(0xFF76ABAE)
            val destructiveActionColor = Color(0xFFE57373)
            val primaryTextColor = Color(0xFFEEEEEE)
            val secondaryTextColor = Color(0xFFAAAAAA)

            val showDialog = remember { mutableStateOf(false) }
            val file = File(filesDir, fileName)
            if (!file.exists()) {
                file.createNewFile()
            }
            val savedText = remember { mutableStateOf(file.readText()) }
            val textState = remember { mutableStateOf(savedText.value) }
            val unSavedChanges = textState.value != savedText.value

            fun save() {
                if (textState.value.isBlank()) {
                    Toast.makeText(this, "Note is empty", Toast.LENGTH_SHORT).show()
                } else {
                    file.writeText(textState.value)
                    savedText.value = textState.value
                    Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show()
                }
            }

            fun delete() {
                file.delete()
                file.createNewFile()
                textState.value = ""
                savedText.value = ""
                Toast.makeText(this, "Cleared", Toast.LENGTH_SHORT).show()
            }

            MemoryNotesTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = darkBlue
                ) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .padding(horizontal = 24.dp, vertical = 16.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Memory Notes",
                                color = primaryTextColor,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f)
                            )
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

                        if (showDialog.value) {
                            AlertDialog(
                                onDismissRequest = { showDialog.value = false },
                                title = { Text("Confirm Clear", color = primaryTextColor) },
                                text = { Text("Are you sure you want to permanently clear this note?", color = secondaryTextColor) },
                                containerColor = surfaceColor,
                                confirmButton = {
                                    TextButton(
                                        onClick = {
                                            delete()
                                            showDialog.value = false
                                        }
                                    ) {
                                        Text("Yes, Clear", color = destructiveActionColor)
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
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Button(
                                onClick = { save() },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = primaryActionColor,
                                    contentColor = darkBlue
                                ),
                                enabled = unSavedChanges && textState.value.isNotBlank()
                            ) {
                                Text("Save", fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 8.dp))
                            }
                            Button(
                                onClick = { showDialog.value = true },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = destructiveActionColor,
                                    contentColor = Color.White
                                ),
                                enabled = textState.value.isNotEmpty()
                            ) {
                                Text("Clear", fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 8.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}