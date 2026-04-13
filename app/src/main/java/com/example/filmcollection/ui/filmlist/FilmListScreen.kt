package com.example.filmcollection.ui.filmlist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.filmcollection.R
import com.example.filmcollection.model.Film
import com.example.filmcollection.model.FilmDraft
import com.example.filmcollection.viewmodel.FilmCollectionViewModel

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun FilmListScreen(
    viewModel: FilmCollectionViewModel,
    snackbarHostState: SnackbarHostState,
    onFilmClick: (Long) -> Unit,
) {
    val films by viewModel.films.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var filmToDelete: Film? by remember { mutableStateOf(null) }

    val snackbarAdded = stringResource(R.string.snackbar_film_added)
    val snackbarDeleted = stringResource(R.string.snackbar_film_deleted)

    Scaffold(
        topBar = { TopAppBar(title = { Text(text = stringResource(R.string.top_bar_title)) }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = stringResource(R.string.fab_add_film),
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        if (films.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(R.string.empty_collection_title),
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = stringResource(R.string.empty_collection_description),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(
                    items = films,
                    key = { it.id },
                ) { film ->
                    FilmCard(
                        film = film,
                        onClick = { onFilmClick(film.id) },
                        onDelete = { filmToDelete = film },
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        FilmEditorDialog(
            dialogTitle = stringResource(R.string.dialog_add_film_title),
            initial = FilmDraft(),
            onDismiss = { showAddDialog = false },
            onSave = { title, year, genre, country, director ->
                viewModel.addFilm(
                    title = title,
                    year = year,
                    genre = genre,
                    country = country,
                    director = director,
                )
                viewModel.showSnackbar(snackbarAdded)
                showAddDialog = false
            },
        )
    }

    filmToDelete?.let { film ->
        AlertDialog(
            onDismissRequest = { filmToDelete = null },
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
                        filmToDelete = null
                    },
                ) {
                    Text(text = stringResource(R.string.action_delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { filmToDelete = null }) {
                    Text(text = stringResource(R.string.action_cancel))
                }
            },
        )
    }
}
