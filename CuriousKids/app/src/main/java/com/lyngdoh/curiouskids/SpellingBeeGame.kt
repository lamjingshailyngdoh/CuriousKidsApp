package com.lyngdoh.curiouskids

import android.content.Context
import android.content.SharedPreferences
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun SpellingBeeGame(navController: NavController, wordModel: WordModel = viewModel()) {
    val context = LocalContext.current
    val sharedPreferences: SharedPreferences = context.getSharedPreferences("SpellingBeePrefs", Context.MODE_PRIVATE)
    var inputWord by rememberSaveable { mutableStateOf("") }
    var feedbackMessage by rememberSaveable { mutableStateOf("") }
    var definition by rememberSaveable { mutableStateOf("") }
    var score by rememberSaveable { mutableStateOf(sharedPreferences.getInt("score", 0)) }
    var tts: TextToSpeech? by remember { mutableStateOf(null) }
    val uiState by wordModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        tts = TextToSpeech(context) {
            if (it == TextToSpeech.SUCCESS) {
                tts?.language = Locale.US
            }
        }
        wordModel.sendPrompt(null, "For kids, generate a word for a spelling bee without definition")
    }

    DisposableEffect(Unit) {
        onDispose {
            tts?.shutdown()
        }
    }

    fun saveScore(newScore: Int) {
        sharedPreferences.edit().putInt("score", newScore).apply()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF9C4))
            .padding(16.dp)
            .clickable { focusManager.clearFocus() }, // Dismiss keyboard on touch outside
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { navController.popBackStack() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF64B5F6)) // Light blue
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_back),
                    contentDescription = "Back",
                    tint = Color.White
                )
                Text(
                    text = "Back",
                    color = Color.White,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            Text(
                text = "Score: $score",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color(0xFF0D47A1)
                ),
                modifier = Modifier.padding(end = 16.dp),
                textAlign = TextAlign.End
            )
        }

        Text(
            text = "Spelling Bee",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = Color(0xFF0D47A1) // Dark blue color
            ),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (uiState is UiState.Loading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        } else {
            OutlinedTextField(
                value = inputWord,
                onValueChange = { inputWord = it },
                label = { Text("Spell the word") },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { focusManager.clearFocus() }
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentSize(Alignment.Center)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = {
                    if (uiState is UiState.Success) {
                        val currentWord = (uiState as UiState.Success).outputText.trim().replace("**", "")
                        if (inputWord.equals(currentWord, ignoreCase = true)) {
                            feedbackMessage = "Correct!"
                            score++
                            saveScore(score)
                            tts?.speak("Correct!", TextToSpeech.QUEUE_FLUSH, null, null)
                            inputWord = ""
                            coroutineScope.launch {
                                wordModel.sendPrompt(null, "For kids, generate a new word for a spelling bee without definition")
                            }
                        } else {
                            feedbackMessage = "Incorrect. Try again!"
                            tts?.speak("Incorrect. Try again.", TextToSpeech.QUEUE_FLUSH, null, null)
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFAB91)), // Light orange
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = 8.dp)
            ) {
                Text(
                    text = "Check Spelling",
                    color = Color.White,
                    modifier = Modifier.padding(8.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Button(
                    onClick = {
                        if (tts != null && uiState is UiState.Success) {
                            val wordToSpell = (uiState as UiState.Success).outputText.trim().replace("**", "") // Clean word
                            Log.d("SpellingBeeGame", "Generated word: $wordToSpell")
                            tts?.speak(wordToSpell, TextToSpeech.QUEUE_FLUSH, null, null)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF81C784)), // Light green
                    shape = MaterialTheme.shapes.large,
                    elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = 8.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_speaker),
                        contentDescription = "Hear Word",
                        tint = Color.White
                    )
                    Text(
                        text = "Hear Word",
                        color = Color.White,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }

                Button(
                    onClick = {
                        if (tts != null && uiState is UiState.Success) {
                            val currentWord = (uiState as UiState.Success).outputText.trim().replace("**", "")
                            coroutineScope.launch {
                                definition = wordModel.getDefinition(currentWord)
                                tts?.speak(definition, TextToSpeech.QUEUE_FLUSH, null, null)
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFAB91)), // Light orange
                    shape = MaterialTheme.shapes.large,
                    elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = 8.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_definition),
                        contentDescription = "Definition",
                        tint = Color.White
                    )
                    Text(
                        text = "Definition",
                        color = Color.White,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = feedbackMessage,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = if (feedbackMessage == "Correct!") Color(0xFF4CAF50) else Color(0xFFF44336)
                ),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    }
}
