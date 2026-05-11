package com.example.filmcollection.ui.filmdetail

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.filmcollection.R
import com.example.filmcollection.model.Film
import com.example.filmcollection.model.FilmDraft
import com.example.filmcollection.model.WatchStatus
import com.example.filmcollection.ui.filmlist.FilmEditorDialog
import com.example.filmcollection.viewmodel.FilmCollectionViewModel

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun FilmDetailScreen(
    filmId: String,
    viewModel: FilmCollectionViewModel,
    snackbarHostState: SnackbarHostState,
    onBack: () -> Unit,
) {
    val films by viewModel.films.collectAsState()
    val film = films.firstOrNull { it.id == filmId }

    if (film == null) {
        LaunchedEffect(Unit) { onBack() }
        return
    }

    var showEditor by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val snackbarUpdated = stringResource(R.string.snackbar_film_updated)
    val snackbarDeleted = stringResource(R.string.snackbar_film_deleted)
    val snackbarRating = stringResource(R.string.snackbar_rating_saved)
    val snackbarStatus = stringResource(R.string.snackbar_status_updated)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = film.title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.action_back),
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showEditor = true }) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = stringResource(R.string.action_edit),
                        )
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = stringResource(R.string.action_delete),
                        )
                    }
                },
            )
        },
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
            ) {
                FilmImage(imageResId = film.imageResId)

                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    FilmInfoSection(film = film)

                    RatingSection(
                        rating = film.rating,
                        onRatingChanged = { newRating ->
                            viewModel.updateFilm(film.copy(rating = newRating))
                            viewModel.showSnackbar(snackbarRating)
                        },
                    )

                    WatchStatusSection(
                        currentStatus = film.watchStatus,
                        onStatusChanged = { newStatus ->
                            viewModel.updateFilm(film.copy(watchStatus = newStatus))
                            viewModel.showSnackbar(snackbarStatus)
                        },
                    )
                }
            }

            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.align(Alignment.TopCenter),
            )
        }
    }

    if (showEditor) {
        FilmEditorDialog(
            dialogTitle = stringResource(R.string.dialog_edit_film_title),
            initial = FilmDraft(
                title = film.title,
                year = film.year.toString(),
                genre = film.genre,
                country = film.country,
                director = film.director,
            ),
            onDismiss = { showEditor = false },
            onSave = { title, year, genre, country, director ->
                viewModel.updateFilm(
                    film.copy(
                        title = title,
                        year = year,
                        genre = genre,
                        country = country,
                        director = director,
                    ),
                )
                viewModel.showSnackbar(snackbarUpdated)
                showEditor = false
            },
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(text = stringResource(R.string.dialog_delete_film_title)) },
            text = {
                Text(
                    text = stringResource(R.string.dialog_delete_film_message, film.title),
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteFilm(film.id)
                        viewModel.showSnackbar(snackbarDeleted)
                        showDeleteDialog = false
                    },
                ) {
                    Text(text = stringResource(R.string.action_delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(text = stringResource(R.string.action_cancel))
                }
            },
        )
    }
}

@Composable
private fun FilmImage(imageResId: Int?) {
    Image(
        painter = painterResource(imageResId ?: R.drawable.film_placeholder),
        contentDescription = stringResource(R.string.detail_film_image),
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(2f / 3f),
        contentScale = ContentScale.Crop,
    )
}

@Composable
private fun FilmInfoSection(film: Film) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = stringResource(R.string.film_title_with_year, film.title, film.year),
            style = MaterialTheme.typography.headlineSmall,
        )
        Text(
            text = stringResource(R.string.film_subtitle, film.genre, film.country),
            style = MaterialTheme.typography.bodyLarge,
        )
        Text(
            text = stringResource(R.string.film_director, film.director),
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
private fun RatingSection(
    rating: Int?,
    onRatingChanged: (Int?) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = stringResource(R.string.detail_rating_label),
            style = MaterialTheme.typography.titleMedium,
        )
        StarRatingBar(
            rating = rating ?: 0,
            onRatingChanged = { newRating ->
                onRatingChanged(if (newRating == rating) null else newRating)
            },
        )
    }
}

@Composable
private fun StarRatingBar(
    rating: Int,
    onRatingChanged: (Int) -> Unit,
    maxStars: Int = 5,
) {
    Row {
        for (i in 1..maxStars) {
            val filled = i <= rating
            IconButton(onClick = { onRatingChanged(i) }) {
                Icon(
                    imageVector = if (filled) Icons.Filled.Star else Icons.Filled.StarBorder,
                    contentDescription = stringResource(
                        if (filled) R.string.star_filled else R.string.star_empty,
                        i,
                        maxStars,
                    ),
                    tint = if (filled) Color(0xFFFFB300) else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(32.dp),
                )
            }
        }
    }
}

@Composable
@OptIn(ExperimentalLayoutApi::class)
private fun WatchStatusSection(
    currentStatus: WatchStatus,
    onStatusChanged: (WatchStatus) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = stringResource(R.string.detail_watch_status_label),
            style = MaterialTheme.typography.titleMedium,
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            WatchStatus.entries.forEach { status ->
                FilterChip(
                    selected = status == currentStatus,
                    onClick = { onStatusChanged(status) },
                    label = { Text(text = status.toDisplayString()) },
                    leadingIcon = if (status == currentStatus) {
                        {
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = null,
                                modifier = Modifier.size(FilterChipDefaults.IconSize),
                            )
                        }
                    } else {
                        null
                    },
                )
            }
        }
    }
}

@Composable
private fun WatchStatus.toDisplayString(): String = when (this) {
    WatchStatus.NOT_WATCHED -> stringResource(R.string.status_not_watched)
    WatchStatus.IN_PROGRESS -> stringResource(R.string.status_in_progress)
    WatchStatus.WATCHED -> stringResource(R.string.status_watched)
}
