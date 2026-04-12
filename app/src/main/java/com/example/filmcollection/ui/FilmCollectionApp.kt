package com.example.filmcollection.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.filmcollection.R
import com.example.filmcollection.model.Film
import com.example.filmcollection.model.FilmDraft
import com.example.filmcollection.viewmodel.FilmCollectionViewModel
import java.util.Calendar

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun FilmCollectionApp(
    viewModel: FilmCollectionViewModel,
) {
    val films by viewModel.films.collectAsState()

    var editorMode: EditorMode? by remember { mutableStateOf(null) }
    var filmToDelete: Film? by remember { mutableStateOf(null) }

    Scaffold(
        topBar = { TopAppBar(title = { Text(text = stringResource(R.string.top_bar_title)) }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { editorMode = EditorMode.Add }) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = stringResource(R.string.fab_add_film),
                )
            }
        },
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
                contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(
                    items = films,
                    key = { it.id },
                ) { film ->
                    FilmCard(
                        film = film,
                        onEdit = { editorMode = EditorMode.Edit(film) },
                        onDelete = { filmToDelete = film },
                    )
                }
            }
        }
    }

    when (val mode = editorMode) {
        EditorMode.Add -> {
            FilmEditorDialog(
                dialogTitle = stringResource(R.string.dialog_add_film_title),
                initial = FilmDraft(),
                onDismiss = { editorMode = null },
                onSave = { title, year, genre, country, director ->
                    viewModel.addFilm(
                        title = title,
                        year = year,
                        genre = genre,
                        country = country,
                        director = director,
                    )
                    editorMode = null
                },
            )
        }

        is EditorMode.Edit -> {
            val film = mode.film
            FilmEditorDialog(
                dialogTitle = stringResource(R.string.dialog_edit_film_title),
                initial = FilmDraft(
                    title = film.title,
                    year = film.year.toString(),
                    genre = film.genre,
                    country = film.country,
                    director = film.director,
                ),
                onDismiss = { editorMode = null },
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
                    editorMode = null
                },
            )
        }

        null -> Unit
    }

    filmToDelete?.let { film ->
        AlertDialog(
            onDismissRequest = { filmToDelete = null },
            title = { Text(text = stringResource(R.string.dialog_delete_film_title)) },
            text = {
                Text(
                    text = stringResource(
                        R.string.dialog_delete_film_message,
                        film.title,
                    ),
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteFilm(film.id)
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

private sealed interface EditorMode {
    data object Add : EditorMode
    data class Edit(val film: Film) : EditorMode
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun FilmCard(
    film: Film,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onEdit,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = stringResource(
                        R.string.film_title_with_year,
                        film.title,
                        film.year,
                    ),
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = stringResource(
                        R.string.film_subtitle,
                        film.genre,
                        film.country,
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = stringResource(R.string.film_director, film.director),
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(onClick = onEdit) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = stringResource(R.string.action_edit),
                )
            }
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = stringResource(R.string.action_delete),
                )
            }
        }
    }
}

@Composable
private fun FilmEditorDialog(
    dialogTitle: String,
    initial: FilmDraft,
    onDismiss: () -> Unit,
    onSave: (
        title: String,
        year: Int,
        genre: String,
        country: String,
        director: String,
    ) -> Unit,
) {
    var draft by remember(initial) { mutableStateOf(initial) }

    val currentYear = remember {
        Calendar.getInstance().get(Calendar.YEAR)
    }
    val parsedYear = draft.year.trim().toIntOrNull()
    val isTitleValid = draft.title.trim().isNotEmpty()
    val isYearValid = parsedYear != null && parsedYear in 1888..(currentYear + 1)
    val isGenreValid = draft.genre.trim().isNotEmpty()
    val isCountryValid = draft.country.trim().isNotEmpty()
    val isDirectorValid = draft.director.trim().isNotEmpty()
    val canSave = isTitleValid && isYearValid && isGenreValid && isCountryValid && isDirectorValid

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = dialogTitle) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                OutlinedTextField(
                    value = draft.title,
                    onValueChange = { draft = draft.copy(title = it) },
                    label = { Text(text = stringResource(R.string.label_title)) },
                    singleLine = true,
                    isError = !isTitleValid,
                    supportingText = {
                        if (!isTitleValid) Text(text = stringResource(R.string.error_title_required))
                    },
                    modifier = Modifier.fillMaxWidth(),
                )

                OutlinedTextField(
                    value = draft.year,
                    onValueChange = { draft = draft.copy(year = it) },
                    label = { Text(text = stringResource(R.string.label_year)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = !isYearValid,
                    supportingText = {
                        if (!isYearValid) {
                            Text(
                                text = stringResource(
                                    R.string.error_year_invalid,
                                    1888,
                                    currentYear + 1,
                                ),
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                )

                OutlinedTextField(
                    value = draft.genre,
                    onValueChange = { draft = draft.copy(genre = it) },
                    label = { Text(text = stringResource(R.string.label_genre)) },
                    singleLine = true,
                    isError = !isGenreValid,
                    supportingText = {
                        if (!isGenreValid) Text(text = stringResource(R.string.error_genre_required))
                    },
                    modifier = Modifier.fillMaxWidth(),
                )

                OutlinedTextField(
                    value = draft.country,
                    onValueChange = { draft = draft.copy(country = it) },
                    label = { Text(text = stringResource(R.string.label_country)) },
                    singleLine = true,
                    isError = !isCountryValid,
                    supportingText = {
                        if (!isCountryValid) Text(text = stringResource(R.string.error_country_required))
                    },
                    modifier = Modifier.fillMaxWidth(),
                )

                OutlinedTextField(
                    value = draft.director,
                    onValueChange = { draft = draft.copy(director = it) },
                    label = { Text(text = stringResource(R.string.label_director)) },
                    singleLine = true,
                    isError = !isDirectorValid,
                    supportingText = {
                        if (!isDirectorValid) Text(text = stringResource(R.string.error_director_required))
                    },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val year = parsedYear ?: return@TextButton
                    onSave(
                        draft.title.trim(),
                        year,
                        draft.genre.trim(),
                        draft.country.trim(),
                        draft.director.trim(),
                    )
                },
                enabled = canSave,
            ) {
                Text(text = stringResource(R.string.action_save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.action_cancel))
            }
        },
    )
}
