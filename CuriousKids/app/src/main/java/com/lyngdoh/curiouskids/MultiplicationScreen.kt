import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.lyngdoh.curiouskids.MathModel
import com.lyngdoh.curiouskids.R
import com.lyngdoh.curiouskids.UiState

@Composable
fun MultiplicationScreen(navController: NavController, mathModel: MathModel = viewModel()) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val sharedPrefs = context.getSharedPreferences("curious_kids_prefs", Context.MODE_PRIVATE)

    // Load the stored score
    var score by remember { mutableStateOf(sharedPrefs.getInt("multiplication_score", 0)) }
    var userAnswer by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var dialogTitle by remember { mutableStateOf("") }
    var dialogMessage by remember { mutableStateOf("") }
    val uiState by mathModel.uiState.collectAsState()

    // Function to fetch a new question
    fun fetchNewQuestion() {
        mathModel.sendPrompt(
            null,
            "Generate a simple multiplication question for kids and provide the correct answer. Response follows the pattern 'Question: ... Answer: ...'. Ensure the answer is not included in the question."
        )
    }

    LaunchedEffect(Unit) {
        fetchNewQuestion()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF176))
            .padding(16.dp)
            .clickable { focusManager.clearFocus() },
        verticalArrangement = Arrangement.spacedBy(1.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { navController.popBackStack() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF64B5F6)),
                modifier = Modifier.padding(8.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_back),
                    contentDescription = "Back",
                    tint = Color.White
                )
                Text(text = "Back", color = Color.White)
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "Score: $score",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                ),
                color = Color(0xFF0D47A1)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Math Fun: Multiplication",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            ),
            color = Color(0xFF0D47A1)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Box(
            modifier = Modifier
                .background(Color.White, shape = MaterialTheme.shapes.medium)
                .padding(16.dp)
        ) {
            when (uiState) {
                is UiState.Loading -> {
                    CircularProgressIndicator()
                }
                is UiState.Success -> {
                    val responseText = (uiState as UiState.Success).outputText

                    // Ensure the response contains a valid question and answer format
                    val questionRegex = "Question: (.*)".toRegex()
                    val answerRegex = "Answer: (\\d+)".toRegex()

                    val questionMatch = questionRegex.find(responseText)
                    val answerMatch = answerRegex.find(responseText)

                    if (questionMatch != null && answerMatch != null) {
                        val question = questionMatch.groupValues[1]
                        val correctAnswer = answerMatch.groupValues[1].trim()

                        if (!question.contains(correctAnswer)) {  // Check if answer is not in question
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = question,
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 24.sp
                                    ),
                                    color = Color.Black
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                OutlinedTextField(
                                    value = userAnswer,
                                    onValueChange = { newValue ->
                                        userAnswer = newValue.filter { it.isDigit() }
                                    },
                                    label = { Text("Enter your answer") },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                                )

                                Button(
                                    onClick = {
                                        if (userAnswer == correctAnswer) {
                                            score++
                                            // Save the updated score
                                            sharedPrefs.edit().putInt("multiplication_score", score).apply()
                                            dialogTitle = "Correct!"
                                            dialogMessage = "Well done! You got it right."
                                        } else {
                                            dialogTitle = "Incorrect"
                                            dialogMessage = "Oops! That's not correct. Please try again."
                                        }
                                        showDialog = true
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF64B5F6)),
                                    modifier = Modifier.padding(top = 8.dp)
                                ) {
                                    Text(text = "Submit", color = Color.White)
                                }
                            }
                        } else {
                            // Answer was included in the question, request a new question
                            fetchNewQuestion()
                        }
                    } else {
                        // Data is incomplete or malformed, request a new question
                        fetchNewQuestion()
                    }
                }
                is UiState.Error -> {
                    // Error occurred, request a new question
                    fetchNewQuestion()
                }

                UiState.Initial -> {
                    // Optionally handle the initial state if necessary
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(text = dialogTitle, fontWeight = FontWeight.Bold) },
            text = { Text(text = dialogMessage) },
            confirmButton = {
                Button(onClick = {
                    showDialog = false
                    userAnswer = ""
                    fetchNewQuestion()  // Fetch a new question on dialog dismissal
                }) {
                    Text(text = "Next Question")
                }
            }
        )
    }
}
