package daily.dayo.presentation.screen.post

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun PostScreen(postId: String) {
    // TODO PostScreen
    Scaffold(
        content = {
            Column(Modifier.fillMaxSize()) {
                Text(text = postId)
            }
        }
    )
}