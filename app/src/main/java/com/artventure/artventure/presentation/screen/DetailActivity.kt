package com.artventure.artventure.presentation.screen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.artventure.artventure.data.model.dto.CollectionDto
import com.artventure.artventure.presentation.screen.SearchFragment.Companion.COLLECTION_CONTENT_KEY
import com.artventure.artventure.presentation.screen.SearchFragment.Companion.SEARCH_BUNDLE_KEY
import com.artventure.artventure.presentation.screen.ui.theme.ArtventureTheme
import com.artventure.artventure.util.extension.customGetSerializable
import timber.log.Timber

class DetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = intent.getBundleExtra(SEARCH_BUNDLE_KEY)
        val data = bundle?.customGetSerializable<CollectionDto>(COLLECTION_CONTENT_KEY)
        Timber.d("$data")

        setContent {
            ArtventureTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ArtventureTheme {
        Greeting("Android")
    }
}