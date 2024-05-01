package com.example.soloproject

import android.content.ContentResolver
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore.getPickImagesMaxLimit
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresExtension
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.exifinterface.media.ExifInterface
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.soloproject.ui.theme.SoloProjectTheme


class MainActivity : ComponentActivity() {
    @RequiresExtension(extension = Build.VERSION_CODES.R, version = 2)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SoloProjectTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    //selectImage()
                    PhotoApp()
                }
            }
        }
    }
}

@RequiresExtension(extension = Build.VERSION_CODES.R, version = 2)
@Composable
fun WelcomeScreen(
    onNextButtonClicked: () -> Unit = {},
    onSelectedImagesChanged: (List<Uri?>) -> Unit,
    modifier: Modifier = Modifier
)
{
    var selectedImages by remember {
        mutableStateOf<List<Uri?>>(emptyList())
    }
    val mediaCount = getPickImagesMaxLimit()
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(mediaCount),
        onResult = {uris -> selectedImages = uris}
    )

    fun launchPhotoPicker()
    {
        photoPickerLauncher.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
    }
    Column(
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    0.0f to Color(android.graphics.Color.parseColor("#FD297B")),
                    0.5f to Color(android.graphics.Color.parseColor("#FF5864")),
                    1.0f to Color(android.graphics.Color.parseColor("#FF655B")),
                    start = Offset(300.0f, 500.0f),
                    end = Offset(1300.0f, 1800.0f)
                )
            )
            .padding(10.dp)
    ) {
        Text(
            text = "Welcome to",
            textAlign = TextAlign.Start
        )
        Text(
            text ="PhotoTinder",
            fontSize = 56.sp,
            fontWeight = FontWeight.Bold,
            style = TextStyle(color = MaterialTheme.colorScheme.secondary),
            textAlign = TextAlign.Center,
            modifier = Modifier
        )
        Spacer(modifier = Modifier.height(100.dp))
        OutlinedButton(
            onClick = {
                launchPhotoPicker()
            },
            modifier = modifier.align(Alignment.CenterHorizontally)
        )
        {
            Text("Select Image(s)")
        }
        OutlinedButton(
            onClick = {
                onSelectedImagesChanged(selectedImages)
                onNextButtonClicked()
            },
            enabled = selectedImages.isNotEmpty(),
            modifier = modifier.align(Alignment.CenterHorizontally)
        )
        {
            Text("Let's Start Swiping")
        }
    }
}

@Composable
fun EndScreen(
    onNextButtonClicked: () -> Unit = {},
    SwipeCount: Int,
)
{
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    0.0f to Color(android.graphics.Color.parseColor("#FD297B")),
                    0.5f to Color(android.graphics.Color.parseColor("#FF5864")),
                    1.0f to Color(android.graphics.Color.parseColor("#FF655B")),
                    start = Offset(300.0f, 500.0f),
                    end = Offset(1300.0f, 1800.0f)
                )
            )
    )
    {
        Text(text = "You Have Swiped $SwipeCount Images")
        Spacer(modifier = Modifier.height(10.dp))
        Text(text = "Click to Restart:")
        Spacer(modifier = Modifier.height(50.dp))
        LargeFloatingActionButton(onClick = { onNextButtonClicked() }) {
            Icon(Icons.Filled.Refresh, "Floating action button.")
        }
    }
}

@RequiresExtension(extension = Build.VERSION_CODES.R, version = 2)
@Composable
fun PhotoApp(
    navController: NavHostController = rememberNavController()
)
{
    var selectedImages by remember {
        mutableStateOf<List<Uri?>>(emptyList())
    }
    NavHost(navController = navController, startDestination = PhotoScreen.Start.name, modifier = Modifier) {
        composable(route = PhotoScreen.Start.name) {
            WelcomeScreen(
                onSelectedImagesChanged = { newSelectedImages ->
                    selectedImages = newSelectedImages
                },
                onNextButtonClicked = {
                    navController.navigate(PhotoScreen.ImageSelect.name)
                },
                modifier = Modifier
            )
        }
        composable(route = PhotoScreen.ImageSelect.name) {
            ImageLayoutView(
                selectedImages = selectedImages,
                onNextButtonClicked = {
                    navController.navigate(PhotoScreen.End.name)
                },
            )
        }
        composable(route = PhotoScreen.End.name)
        {
            EndScreen(
                onNextButtonClicked = {
                    navController.navigate(PhotoScreen.Start.name)
                },
                SwipeCount = selectedImages.size
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageLayoutView(
    selectedImages: List<Uri?>,
    onNextButtonClicked: () -> Unit = {},
) {
    var phase by remember {
        mutableStateOf(0)
    }
    Scaffold(
        topBar = {
            TopAppBar(
                colors = topAppBarColors(
                    containerColor = Color(android.graphics.Color.parseColor("#FD297B"))
                ),
                title = {
                    Text(
                        text = " PhotoTinder",
                        fontSize = 20.sp
                    )
                }
            )
        }
    )
    {innerpadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .background(
                    brush = Brush.linearGradient(
                        0.0f to Color(android.graphics.Color.parseColor("#FD297B")),
                        0.5f to Color(android.graphics.Color.parseColor("#FF5864")),
                        1.0f to Color(android.graphics.Color.parseColor("#FF655B")),
                        start = Offset(300.0f, 500.0f),
                        end = Offset(1300.0f, 1800.0f)
                    )
                )
                .padding(innerpadding)
                .fillMaxSize()
        )
        {
            val uri = selectedImages[phase]
            uri?.let { imageUrl ->
                AsyncImage(
                    model = uri,
                    contentDescription = null,
                    modifier = Modifier
                        .height(400.dp)
                        .width(350.dp)
                        .clip(
                            shape = RoundedCornerShape(
                                topStart = 16.dp,
                                topEnd = 16.dp,

                                )
                        ),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .width(350.dp)
                        .height(150.dp)
                        .clip(
                            shape = RoundedCornerShape(
                                bottomStart = 16.dp,
                                bottomEnd = 16.dp
                            )
                        )
                        .background(Color.Black)
                )
                {
                    val context = LocalContext.current
                    val contentResolver: ContentResolver = context.contentResolver
                    val exifInterface = contentResolver.openInputStream(uri)?.let { ExifInterface(it) }
                    val dateTime = exifInterface?.getAttribute(ExifInterface.TAG_DATETIME)
                    val latitude = exifInterface?.getAttribute(ExifInterface.TAG_GPS_LATITUDE)
                    val longitude = exifInterface?.getAttribute(ExifInterface.TAG_GPS_LONGITUDE)
                    val gpsAreaInformation = exifInterface?.getAttribute(ExifInterface.TAG_GPS_AREA_INFORMATION)
                    
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("Date|Time: $dateTime")
                        Text(text = "Lat|Long: $latitude, $longitude")
                        Text(text = "Area Information: $gpsAreaInformation")
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 10.dp)
            ) {
                FloatingActionButton(
                    onClick = {
                        if(phase == selectedImages.size - 1)
                        {
                            onNextButtonClicked()
                        }
                        else
                        {
                            phase += 1
                        }
                    },
                    containerColor = Color(android.graphics.Color.parseColor("#FD297B"))
                ) {
                    Icon(Icons.Filled.Clear, "Floating action button.")
                }
                FloatingActionButton(
                    onClick = {
                        if(phase == selectedImages.size - 1)
                        {
                            onNextButtonClicked()
                        }
                        else
                        {
                            phase += 1
                        }
                    },
                    containerColor = Color(android.graphics.Color.parseColor("#FD297B"))
                ) {
                    Icon(Icons.Filled.Check, "Floating action button.")
                }
            }
        }
    }
}

enum class PhotoScreen
{
    Start,
    ImageSelect,
    End,
}
