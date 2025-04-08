package com.fit2081.kitahack

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fit2081.kitahack.ui.theme.KitaHackTheme
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.TextField
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.FirebaseException
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GithubAuthProvider
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PlayGamesAuthProvider
import com.google.firebase.auth.actionCodeSettings
import com.google.firebase.auth.auth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
//import com.google.firebase.quickstart.auth.*
import java.util.concurrent.TimeUnit
import kotlin.io.encoding.ExperimentalEncodingApi
import java.util.Base64
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.FunctionDeclaration
import com.google.ai.client.generativeai.type.GenerateContentResponse
import com.google.ai.client.generativeai.type.GenerationConfig
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.ImagePart
import com.google.ai.client.generativeai.type.Schema
import com.google.ai.client.generativeai.type.TextPart
import com.google.ai.client.generativeai.type.defineFunction
import com.google.ai.client.generativeai.type.generationConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.serialization.Serializable
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.util.UUID
import kotlin.time.Duration.Companion.seconds

class MainActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        FirebaseApp.initializeApp(this)
        val auth = FirebaseAuth.getInstance()

        setContent {
            val navController = rememberNavController()

            KitaHackTheme {
                NavHost(navController = navController, startDestination = if (auth.currentUser != null) "home" else "login") {
                    composable("login") {
                        AuthenticationScreen(auth, navController)
                    }
                    composable("home") {
                        HomeScreen(auth, navController)
                    }
                    composable("profile") {
                        Profile(auth, navController)
                    }

                }
            }
        }
    }
}

@Composable
fun AuthenticationScreen(auth: FirebaseAuth, navController: NavController) {
    var phoneNumber by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("") }
    var verificationId by remember { mutableStateOf<String?>(null) }
    var isUserLoggedIn by remember { mutableStateOf(auth.currentUser != null) }
    val context = LocalContext.current

    // FIX: Delay navigation to prevent crash
    LaunchedEffect(auth.currentUser) {
        if (auth.currentUser != null) {
            delay(100) // Small delay to let NavHost initialize
            navController.navigate("home") {
                popUpTo("login") { inclusive = true }
            }
        }
    }
    Scaffold(
        containerColor = Color(0xFFB5CAE9)
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            if (isUserLoggedIn) {
                Text("Login Successful!", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
            } else {

                Image(
                    painter = painterResource(id = R.drawable.logo), // ← your camera icon
                    contentDescription = "logo",
                    modifier = Modifier.size(140.dp).align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Welcome to KitaReport!",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 30.dp),
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Be the eyes of your community! Snap an image of an incident to instantly report to Malaysian authorities.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    label = { Text("Phone Number") },
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        sendOtp(auth, phoneNumber, context, { id -> verificationId = id }) {
                            isUserLoggedIn = true
                            navController.navigate("home") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF003366) // Dark Blue Hex Code
                    )
                ) {
                    Text("Send OTP")
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = otp,
                    onValueChange = { otp = it },
                    label = { Text("Enter OTP") },
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        verificationId?.let {
                            verifyOtp(auth, it, otp, context) {
                                isUserLoggedIn = true
                                navController.navigate("home") {
                                    popUpTo("login") { inclusive = true }
                                }
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF003366) // Dark Blue Hex Code
                    )
                ) {
                    Text("Verify OTP")
                }
            }
        }
    }
}


fun sendOtp(
    auth: FirebaseAuth,
    phoneNumber: String,
    context: Context,
    onVerificationIdReceived: (String) -> Unit,
    onLoginSuccess: () -> Unit // Callback to notify success
) {
    val options = PhoneAuthOptions.newBuilder(auth)
        .setPhoneNumber(phoneNumber)
        .setTimeout(60L, TimeUnit.SECONDS)
        .setActivity(context as Activity)
        .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                auth.signInWithCredential(credential)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(context, "Login Successful", Toast.LENGTH_SHORT).show()
                            onLoginSuccess() // Notify Composable that login is successful
                        } else {
                            Toast.makeText(context, "Login Failed", Toast.LENGTH_SHORT).show()
                        }
                    }
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Toast.makeText(context, "Verification Failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }

            override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                onVerificationIdReceived(verificationId)
                Toast.makeText(context, "OTP Sent", Toast.LENGTH_SHORT).show()
            }
        })
        .build()

    PhoneAuthProvider.verifyPhoneNumber(options)
}

fun verifyOtp(
    auth: FirebaseAuth,
    verificationId: String,
    otp: String,
    context: Context,
    onLoginSuccess: () -> Unit
) {
    val credential = PhoneAuthProvider.getCredential(verificationId, otp)

    auth.signInWithCredential(credential)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(context, "Login Successful", Toast.LENGTH_SHORT).show()
                onLoginSuccess() // Notify Composable that login is successful
            } else {
                Toast.makeText(context, "Invalid OTP", Toast.LENGTH_SHORT).show()
            }
        }
}


@Composable
fun HomeScreen(auth: FirebaseAuth, navController: NavController) {
    KitaHackTheme {
        Scaffold(
            containerColor = Color(0xFFB5CAE9),
            bottomBar = { MyBottomBar(navController) }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {


                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    CameraCaptureScreen(navController)

                }

                Spacer(modifier = Modifier.height(16.dp))

                // Logout Button
                Button(
                    onClick = {
                        auth.signOut()
                        navController.navigate("login") {
                            popUpTo("home") { inclusive = true }
                        }
                    },
                    modifier = Modifier.padding(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF003366) // Dark Blue Hex Code
                    )
                ) {
                    Text("Logout")
                }
            }
        }
    }
}

@Composable
fun CameraCaptureScreen(navController: NavController) {
    var capturedImage by remember { mutableStateOf<Bitmap?>(null) }
    var uploadUrl by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            capturedImage = bitmap
            // You could upload the bitmap here or convert it to a file
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Take a photo of a crime.", fontSize = 30.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center,)
        Spacer(modifier = Modifier.height(10.dp))

        Box(
            Modifier
                .align(Alignment.CenterHorizontally)
                .clickable { cameraLauncher.launch() }
                .size(100.dp) // Adjust size as needed
                .background(Color.White, shape = CircleShape) // Dark blue background with circle shape
                .padding(12.dp) // Optional padding for the icon

        ) {
            Image(
                painter = painterResource(id = R.drawable.cam), // ← your camera icon
                contentDescription = "Upload picture",
                modifier = Modifier.size(60.dp).align(Alignment.Center)
            )
        }

        Spacer(modifier = Modifier.height(5.dp))

        capturedImage?.let { bitmap ->
            Text("Captured Image:", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Captured Image",
                modifier = Modifier.size(200.dp)
            )

            SubmitComplaintButton(bitmap, navController)
        }
    }
}

@Composable
fun SubmitComplaintButton(
    bitmap: Bitmap,
    navController: NavController
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(false) }

    if (isLoading) {
        // Show loading spinner while processing
        CircularProgressIndicator(modifier = Modifier)
    } else {
        Button(
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF003366) // Dark Blue Hex Code
            ),
            onClick = {
                isLoading = true
                coroutineScope.launch {
                    try {
                        val result = withContext(Dispatchers.IO) {
                            val response = sendBitmapToGemini(bitmap)
                            val url = (uploadImageToSupabase(context, bitmap))
                            updateSupabaseWithJsonAndUrl(response.text.toString(), "crime", url)
                        }
                        Log.d("Return value",result)
                        if (result.equals("Successful upload.")) {
                            Toast.makeText(context, "Your report was successful.", Toast.LENGTH_SHORT).show()
                        }
                        else {
                            Toast.makeText(context, "Unsuccessful report. Image might be blurry or has defects. Please try again.", Toast.LENGTH_SHORT).show()
                        }
                        navController.navigate("home")
                    } catch (e: Exception) {
                        Toast.makeText(
                            context,
                            "Error: ${e.localizedMessage ?: "Something went wrong."}",
                            Toast.LENGTH_LONG
                        ).show()
                        e.printStackTrace()
                    } finally {
                        isLoading = false
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp),
            shape = RoundedCornerShape(12.dp),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 6.dp
            )
        ) {
            Text(
                text = "🚨 Submit Report",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}



suspend fun sendBitmapToGemini(bitmap: Bitmap): GenerateContentResponse {
    val model = GenerativeModel(
        modelName = "gemini-2.0-flash",
        apiKey = "AIzaSyBvumppxNq-juIFHbWrX6QZUr1ZHeyZ5DA"
    )
    val response = model.generateContent(
        content {
            generationConfig {responseMimeType = "application/json"}
            image(bitmap)  // Now we send the byte array
            text("Analyze the image uploaded by user." +
                    "Then, please identify the crime or public wrongdoing committed by people in the image" +
                    "then provide a detailed description about the crime or public wrongdoing" +
                    "including the type of violations it involve and the penalties that may be impose" +
                    "by referring to the Malaysian laws." +
                    "If the image is very blur and is not related to crime or public wrongdoing," +
                    "please inform the user to upload again and explain why." +
                    "Structure your response to be concise and precise." + "Find the authority's contact in the form of a phone number, email or website."+
                    "Only output a json file with the following columns: crime_name, authority, authority_contact if crime is detected, or output" +
                    "the json file with \"sorry\" in all columns. Example Output:\n" +
                    "{\n" +
                    "  \"crime_name\": \"Theft\",\n" +
                    "  \"authority\": \"Royal Malaysian Police (PDRM)\",\n" +
                    "  \"authority_contact\": \"999\"\n" +
                    "}")
        }
    )

    print(response.text)
    return response
}



fun bitmapToByteArrayAndFile(context: Context, bitmap: Bitmap): Pair<ByteArray, File> {
    val outputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream) // Compress as JPEG with 90% quality
    val byteArray = outputStream.toByteArray()

    // Save as a temporary file
    val file = File.createTempFile("captured_image", ".jpg", context.cacheDir)
    FileOutputStream(file).use { fileOut ->
        fileOut.write(byteArray)
    }

    return Pair(byteArray, file)
}

suspend fun uploadImageToSupabase(context: Context, bitmap: Bitmap): String {
    val supabase = createSupabaseClient(
        supabaseUrl = "https://znxlinqrmqmjczhoejia.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InpueGxpbnFybXFtamN6aG9lamlhIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDM0MTY1MjIsImV4cCI6MjA1ODk5MjUyMn0.NM6mdiPBtUdcKN7ZqxtybiOPBD1t11gJqjmeWawmyQ4"
    ) {
        install(Postgrest)
        install(Storage) {
            transferTimeout = 90.seconds
        }
    }

    return withContext(Dispatchers.IO) { // Switch to IO dispatcher for file/network operations
        val (byteArray, file) = bitmapToByteArrayAndFile(context, bitmap)
        val fileName = "images/${UUID.randomUUID()}.jpg"

        supabase.storage
            .from("submission")
            .upload(
                path = fileName,
                data = byteArray,
            )

        val url = supabase.storage.from("submission").publicUrl(fileName)
        file.delete()
        url  // The last expression in the withContext block is what's returned
    }
}

fun cleanJsonString(jsonString: String): String {
    val startIndex = jsonString.indexOf('{')
    val endIndex = jsonString.lastIndexOf('}')

    return jsonString.substring(startIndex, endIndex + 1)
}

@Serializable
data class CrimeData(
    val crime_name: String,
    val authority: String,
    val authority_contact: String,
)
suspend fun updateSupabaseWithJsonAndUrl(
    jsonString: String,
    tableName: String,
    imageUrl: String //  Do you really need this if URL is in JSON?
): String {
    val supabase = createSupabaseClient(
        supabaseUrl = "https://znxlinqrmqmjczhoejia.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InpueGxpbnFybXFtamN6aG9lamlhIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDM0MTY1MjIsImV4cCI6MjA1ODk5MjUyMn0.NM6mdiPBtUdcKN7ZqxtybiOPBD1t11gJqjmeWawmyQ4"
    ) {
        install(Postgrest)
        install(Storage) {
            transferTimeout = 90.seconds
        }
    }

    val result = withContext(Dispatchers.IO) {
        try {
            val cleanedJson = cleanJsonString(jsonString)
            val crimeData = Json.decodeFromString<CrimeData>(cleanedJson)
            //  No need for mutableMapOf here.  Use the crimeData object directly

            if (crimeData.crime_name.equals("sorry") || crimeData.authority.equals("sorry") || crimeData.authority_contact.equals("sorry"))
            {
                return@withContext "Please try again."
            }

            val rowData = mapOf(  //  Use a mapOf for immutability
                "crime_name" to crimeData.crime_name,
                "authority" to crimeData.authority,
                "authority_contact" to crimeData.authority_contact,
                "url" to imageUrl //  Use URL from JSON or parameter
            )

            supabase.from(tableName).insert(rowData)
            return@withContext "Successful upload."

        } catch (e: Exception) {
            Log.e("JSON Processing", "Error processing JSON: ${e.message}")
            return@withContext "Please try again."
        }
    }
    return result
}



@Composable
fun Profile(auth: FirebaseAuth, navController: NavController) {
    KitaHackTheme {
        Scaffold(
            containerColor = Color(0xFFB5CAE9),
            bottomBar = { MyBottomBar(navController) }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(16.dp), // Extra padding for aesthetics
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Title
                Text("About Us", fontSize = 30.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center,)
                Spacer(modifier = Modifier.height(20.dp))

                // Image (replace with your image resource or use a URL with Coil)
                Image(
                    painter = painterResource(id = R.drawable.logo), // Replace with your image
                    contentDescription = "Profile picture",
                    modifier = Modifier
                        .size(140.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Some lines of text
                Text(
                    text = "Hello! We are from team Sarawak Buaya Riders!",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 24.dp),
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "We are a group of students who aims to streamline the whistleblowing system in Malaysia.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )



                Spacer(modifier = Modifier.height(150.dp))


                Text(
                    text = "Developed by ✨",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
                Spacer(modifier = Modifier.height(20.dp))


                Text(
                    text = "Ong Shang Kheat",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )

                Text(
                    text = "Chuah Pei Wen",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )

                Text(
                    text = "Ho Hong En",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )

                Text(
                    text = "David Ding Sing Yew",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
            }
        }
    }

}





@Composable
fun MyBottomBar(navController: NavController) {
    BottomAppBar(
        modifier = Modifier.height(60.dp),
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        content = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly, // 👈 even spacing
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.navigate("profile") }) {
                    Icon(Icons.Filled.Info, contentDescription = "Profile")
                }

                IconButton(onClick = { navController.navigate("home") }) {
                    Icon(Icons.Filled.Home, contentDescription = "Home")
                }

                IconButton(onClick = {
                    FirebaseAuth.getInstance().signOut()
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                }) {
                    Icon(Icons.Filled.ExitToApp, contentDescription = "Logout")
                }
            }
        }
    )
}







