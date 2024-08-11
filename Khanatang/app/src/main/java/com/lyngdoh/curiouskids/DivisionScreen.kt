import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.lyngdoh.khanatang.MathModel
import com.lyngdoh.khanatang.UiState

@Composable
fun DivisionScreen(navController: NavController, mathModel: MathModel = viewModel()) {
    var score by remember { mutableStateOf(0) }
    var userAnswer by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var dialogTitle by remember { mutableStateOf("") }
    var dialogMessage by remember { mutableStateOf("") }
    val uiState by mathModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        mathModel.sendPrompt(null, "Generate a simple division question for kids and provide the correct answer.response follows the pattern \"Question: ... Answer: ...\"")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF176))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { navController.popBackStack() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF64B5F6)),
                modifier = Modifier.padding(8.dp)
            ) {
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

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Math Fun: Division",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 26.sp
            ),
            color = Color(0xFF0D47A1)
        )

        Spacer(modifier = Modifier.height(16.dp))

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
                                        dialogTitle = "Correct!"
                                        dialogMessage = "Well done! You got it right."
                                    } else {
                                        dialogTitle = "Incorrect"
                                        dialogMessage = "Oops! That's not correct. Please try again."
                                    }
                                    showDialog = true
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF64B5F6)),
                                modifier = Modifier
                                    .padding(top = 8.dp)
                            ) {
                                Text(text = "Submit", color = Color.White)
                            }
                        }
                    } else {
                        // Handle case where data is incomplete or malformed
                        Text(
                            text = "Error: Question data is incomplete or malformed.",
                            color = Color.Red
                        )
                    }
                }
                is UiState.Error -> {
                    Text(
                        text = "Error: ${(uiState as UiState.Error).errorMessage}",
                        color = Color.Red
                    )
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
                    mathModel.sendPrompt(null, "Generate another simple division question for kids and provide the correct answer.response follows the pattern \"Question: ... Answer: ...\"")
                }) {
                    Text(text = "Next Question")
                }
            }
        )
    }
}
