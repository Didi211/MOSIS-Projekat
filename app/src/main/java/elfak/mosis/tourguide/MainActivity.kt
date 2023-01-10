package elfak.mosis.tourguide

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import elfak.mosis.tourguide.ui.navigation.Navigation
import elfak.mosis.tourguide.ui.theme.TourGuideTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LOCKED
        setContent {
            TourGuideTheme {
//                val db = FirebaseFirestore.getInstance()
//
//                val users: MutableMap<String, Any> = HashMap()
//                users["username"] = "dimitrije"
//                users["password"] = "password"

                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
//                    db.collection("users")
//                        .add(users)
//                        .addOnSuccessListener {
//                            Log.i("FIREBASE", "$it")
//                        }
                    Navigation()
                }
            }
        }
    }
}


