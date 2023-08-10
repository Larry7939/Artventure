package com.artventure.artventure.presentation.screen

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.artventure.artventure.R
import com.artventure.artventure.application.ApplicationClass
import com.artventure.artventure.data.local.FavoriteCollectionDao
import com.artventure.artventure.data.model.dto.CollectionDto
import com.artventure.artventure.presentation.DetailViewModel
import com.artventure.artventure.presentation.screen.FavoriteFragment.Companion.FAVORITE_BUNDLE_KEY
import com.artventure.artventure.presentation.screen.FavoriteFragment.Companion.FAVORITE_INTENT_KEY
import com.artventure.artventure.presentation.screen.SearchFragment.Companion.SEARCH_BUNDLE_KEY
import com.artventure.artventure.presentation.screen.SearchFragment.Companion.SEARCH_INTENT_KEY
import com.artventure.artventure.presentation.screen.ui.theme.ArtventureTheme
import com.artventure.artventure.presentation.screen.ui.theme.ArtventureTypography
import com.artventure.artventure.presentation.screen.ui.theme.G2
import com.artventure.artventure.presentation.screen.ui.theme.G5
import com.artventure.artventure.presentation.screen.ui.theme.G6
import com.artventure.artventure.util.UiState
import com.artventure.artventure.util.extension.customGetSerializable
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.io.Serializable


@AndroidEntryPoint
class DetailActivity : ComponentActivity() {
    private val viewModel: DetailViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var isFavorite = false
        val searchBundle = intent.getBundleExtra(SEARCH_INTENT_KEY)
        val favoriteBundle = intent.getBundleExtra(FAVORITE_INTENT_KEY)
        val bundle:Bundle?
        if(searchBundle!= null) {
             bundle = searchBundle
        }
        else{
            bundle = favoriteBundle
             isFavorite = true
        }
        val bundleKey = if (searchBundle != null) SEARCH_BUNDLE_KEY else FAVORITE_BUNDLE_KEY
        val collectionData = bundle?.customGetSerializable<CollectionDto>(bundleKey)

        setContent {
            addObserver()
            ArtventureTheme {
                if (collectionData != null) {
                    val introData = IntroData(
                        collectionData.titleKor,
                        collectionData.titleEng,
                        collectionData.mainImage
                    )
                    val contentDataRowList = listOf(
                        ContentData(getString(R.string.author), collectionData.artist),
                        ContentData(getString(R.string.mnft_year), "${collectionData.mnfctYear}년"),
                        ContentData(getString(R.string.sector), collectionData.sector),
                        ContentData(getString(R.string.standard), collectionData.standard),
                        ContentData(
                            getString(R.string.collected_year),
                            "${collectionData.artist}년도"
                        ),
                        ContentData(getString(R.string.mtrl_technic), collectionData.matrlTechnic)
                    )
                    DetailScreen(
                        isFavorite,
                        introData,
                        contentDataRowList,
                        onBackClick = { finish() },
                        onConfirmRequest = { viewModel.addFavoriteCollection(collectionData) })
                }
            }
        }
    }

    companion object{
        const val DETAIL_INTENT_KEY = "detail"
    }

    private fun moveToFavorite(){
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("source", DETAIL_INTENT_KEY)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
        finish()
    }
    private fun addObserver(){
        viewModel.dbState.observe(this){state ->
            if (state == UiState.SUCCESS){
                moveToFavorite()
            }
        }
    }
}

data class ContentData(val name: String, val value: String) : Serializable
data class IntroData(val titleKor: String, val titleEng: String, val image: String) : Serializable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    isFavoriteParam:Boolean,
    intro: IntroData,
    content: List<ContentData>,
    onBackClick: () -> Unit,
    onConfirmRequest: () -> Unit
) {

    val introData by rememberSaveable { mutableStateOf(intro) }
    val contentData by rememberSaveable { mutableStateOf(content) }
    val isFavorite = rememberSaveable { mutableStateOf(isFavoriteParam) }
    val isEngTitleVisible = rememberSaveable { mutableStateOf(false) }
    isEngTitleVisible.value = intro.titleKor != intro.titleEng
    val isFavoriteDialogVisible = rememberSaveable { mutableStateOf(false) }


    Surface(
        modifier = Modifier.fillMaxSize(),
        color = G5
    ) {
        Scaffold(
            modifier = Modifier,
            topBar = {
                TopAppBar(
                    introData.titleKor,
                    resId = if (isFavorite.value) {
                        R.drawable.ic_favorite_on
                    } else {
                        R.drawable.ic_favorite_off
                    },
                    onBackClick = onBackClick,
                    onFavoriteClick = {
                        isFavoriteDialogVisible.value = true
                    })
            }) { padding ->
            Content(Modifier.padding(padding), introData, contentData, isEngTitleVisible)
        }

        AddFavoriteDialog(
            visible = isFavoriteDialogVisible.value,
            onDismissRequest = { isFavoriteDialogVisible.value = false },
            onConfirmRequest = {
                isFavorite.value = true
                isFavoriteDialogVisible.value = false
                onConfirmRequest()
            })
    }
}


@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun Content(
    modifier: Modifier,
    introData: IntroData,
    contentData: List<ContentData>,
    isEngTitleVisible: MutableState<Boolean>
) {
    val scrollState = rememberScrollState()

    Surface(
        modifier = modifier
            .padding(top = 20.dp, start = 15.dp, end = 15.dp)
    ) {
        Column(modifier = Modifier.verticalScroll(state = scrollState)) {
            GlideImage(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .align(Alignment.CenterHorizontally),
                model = introData.image,
                contentScale = ContentScale.Crop,
                contentDescription = null
            )
            Text(
                modifier = Modifier.padding(top = 15.dp),
                text = introData.titleKor,
                style = ArtventureTypography.headlineSmall,
            )
            if (isEngTitleVisible.value) {
                Text(
                    text = introData.titleEng,
                    style = ArtventureTypography.headlineSmall,
                )
            }
            Spacer(modifier = Modifier.height(15.dp))
            Column {
                for (data in contentData) {
                    CollectionInfoRow(data = data)
                }
            }
        }
    }
}

@Composable
fun CollectionInfoRow(data: ContentData) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = data.name,
            style = ArtventureTypography.displayLarge,
            color = G2
        )
        Text(
            text = data.value,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.End,
            style = ArtventureTypography.displayLarge,
            color = G2
        )
    }
}

@Composable
fun TopAppBar(titleKor: String, resId: Int, onBackClick: () -> Unit, onFavoriteClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 15.dp, start = 10.dp, end = 15.dp),
        Arrangement.SpaceBetween,
        Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBackClick
        ) {
            Icon(painter = painterResource(id = R.drawable.ic_back), contentDescription = null)
        }
        Text(
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            text = titleKor,
            style = ArtventureTypography.headlineSmall
        )
        IconButton(
            onClick = onFavoriteClick
        ) {
            Icon(
                painter = painterResource(id = resId),
                contentDescription = null
            )
        }
    }
}

@Composable
private fun AddFavoriteDialog(
    visible: Boolean,
    onConfirmRequest: () -> Unit,
    onDismissRequest: () -> Unit,
) {
    if (visible) {
        CustomAlertDialog(onDismissRequest = { onDismissRequest() }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .clip(RoundedCornerShape(12.dp))
                    .background(color = G6)
            ) {
                Text(
                    modifier = Modifier
                        .padding(top = 35.dp)
                        .padding(horizontal = 24.dp)
                        .align(CenterHorizontally),
                    text = stringResource(R.string.favorite_dialog_desc),
                    style = ArtventureTypography.displayLarge
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Text(
                        modifier = Modifier
                            .clickable {
                                onDismissRequest()
                            }
                            .padding(vertical = 30.dp)
                            .weight(1f),
                        textAlign = TextAlign.Center,
                        text = stringResource(R.string.favorite_dismiss),
                        style = ArtventureTypography.displayMedium,
                        color = G2
                    )

                    Text(
                        modifier = Modifier
                            .clickable {
                                onConfirmRequest()
                            }
                            .padding(vertical = 30.dp)
                            .weight(1f),
                        textAlign = TextAlign.Center,
                        text = stringResource(R.string.favorite_confirm),
                        style = ArtventureTypography.displayMedium,
                        color = G2
                    )
                }

            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DetailScreenPreview() {
    ArtventureTheme {
    }
}