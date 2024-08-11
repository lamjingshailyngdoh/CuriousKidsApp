
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.lyngdoh.curiouskids.R

@Composable
fun HomeScreen(navController: NavController) {
    var showMenu by remember { mutableStateOf(false) }
    var showHowToUse by remember { mutableStateOf(false) }
    var showAboutUs by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF176))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "CuriousKids",
                    style = MaterialTheme.typography.headlineMedium.copy(color = Color(0xFF1976D2)),
                    modifier = Modifier.weight(1f)
                )
                Box {
                    IconButton(
                        onClick = { showMenu = !showMenu },
                        modifier = Modifier
                            .size(50.dp)
                            .background(Color(0xFFFFF176), shape = CircleShape)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_menu),
                            contentDescription = "Menu",
                            tint = Color.Black
                        )
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false },
                        modifier = Modifier.background(Color.White)
                    ) {
                        DropdownMenuItem(
                            text = { Text("How to use") ;Color.Black},
                            onClick = { showHowToUse = true; showMenu = false }
                        )
                        DropdownMenuItem(
                            text = { Text("About Us");Color.Black },
                            onClick = { showAboutUs = true; showMenu = false }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // First row of cards
            Row(modifier = Modifier.fillMaxWidth()) {
                CardItem(
                    navController,
                    "storyTelling",
                    R.drawable.stories,
                    Color(0xFFFFF176),
                    Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(16.dp))
                CardItem(
                    navController,
                    "mathGames",
                    R.drawable.maths,
                    Color(0xFFFFF176),
                    Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Second row of cards
            Row(modifier = Modifier.fillMaxWidth()) {
                CardItem(
                    navController,
                    "spellingBeeGame",
                    R.drawable.spelling,
                    Color(0xFFFFF176),
                    Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(16.dp))
                CardItem(
                    navController,
                    "imageIdentification",
                    R.drawable.picture_hunt,
                    Color(0xFFFFF176),
                    Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Third row of cards
            Row(modifier = Modifier.fillMaxWidth()) {
                CardItem(
                    navController,
                    "chatting",
                    R.drawable.chat,
                    Color(0xFFFFF176),
                    Modifier.weight(1f)
                )
            }
        }
    }

    if (showHowToUse) {
        HowToUseDialog { showHowToUse = false }
    }

    if (showAboutUs) {
        AboutUsDialog { showAboutUs = false }
    }
}

@Composable
fun CardItem(
    navController: NavController,
    route: String,
    imageRes: Int,
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .size(200.dp)
            .clickable { navController.navigate(route) },
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(backgroundColor)
    ) {
        Box {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
fun HowToUseDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "How to use",
                    style = MaterialTheme.typography.headlineSmall.copy(color = Color(0xFF1976D2)),
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onDismiss) {
                    Icon(
                        painter = painterResource(R.drawable.ic_close),
                        contentDescription = "Close",
                        tint = Color.Black
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)  // Limit the height to make it scrollable
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                Text(
                    text = "StoryTelling: Explore interactive stories customized for you. You can also generate your own stories by entering what story you like to read. You can also use ask to read it for you\n\n" +
                            "Math Games: Solve fun math problems and learn new concepts. You can play and learn Addition, Subtraction, Multiplication and Division\n\n" +
                            "Spelling Bee: Test your spelling skills with engaging activities. Press the hear word to listen to the word to be spell and then type it in the input box and then press Check spelling button. You can also press the Definition button to get the spell word definition\n\n" +
                            "Image Identification: Identify objects using the camera or gallery. Click the photo of object you want to identify and the AI will help you identify the object. Also you can choose from gallery.\n\n" +
                            "Chatting: Have a fun conversation with our AI bot. ",
                    fontSize = 14.sp,
                    //color = Color.Black
                )
            }
        },
        modifier = Modifier.padding(16.dp)
    )
}

@Composable
fun AboutUsDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "About Us",
                    style = MaterialTheme.typography.headlineSmall.copy(color = Color(0xFF1976D2)),
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onDismiss) {
                    Icon(
                        painter = painterResource(R.drawable.ic_close),
                        contentDescription = "Close",
                        tint = Color.Black
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                Text(
                    text = "CuriousKids is an educational app designed to make learning fun and interactive for children by using Gemini API. Our aim is to provide a safe and engaging environment where kids can explore different subjects through games, stories, and more.\n\n" +
                            "Developer: Lamjingshai \n\n" +
                        "API use: Google Gemini API",
                    fontSize = 16.sp,
                    //color = Color.Black
                )
            }
        },
        modifier = Modifier.padding(16.dp)
    )
}
