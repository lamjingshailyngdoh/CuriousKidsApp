
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.navigation.NavController
import com.lyngdoh.khanatang.R

@Composable
fun HomeScreen(navController: NavController) {
    var showMenu by remember { mutableStateOf(false) }

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
                            text = { Text("Parent Login") },
                            onClick = { navController.navigate("parentLogin"); showMenu = false }
                        )
                        DropdownMenuItem(
                            text = { Text("About Us") },
                            onClick = { navController.navigate("aboutUs"); showMenu = false }
                        )
                        DropdownMenuItem(
                            text = { Text("Settings") },
                            onClick = { navController.navigate("settings"); showMenu = false }
                        )
                        DropdownMenuItem(
                            text = { Text("Sound") },
                            onClick = { /* Handle sound toggle */ showMenu = false }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

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

            Spacer(modifier = Modifier.height(16.dp))

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
        }
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
