package com.example.filmcollection.ui.filmlist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.filmcollection.R
import com.example.filmcollection.model.FilmDraft
import java.util.Calendar

@Composable
internal fun FilmEditorDialog(
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
    var showErrors by remember { mutableStateOf(false) }

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
                    isError = showErrors && !isTitleValid,
                    supportingText = {
                        if (showErrors && !isTitleValid) {
                            Text(text = stringResource(R.string.error_title_required))
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                )

                OutlinedTextField(
                    value = draft.year,
                    onValueChange = { draft = draft.copy(year = it) },
                    label = { Text(text = stringResource(R.string.label_year)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = showErrors && !isYearValid,
                    supportingText = {
                        if (showErrors && !isYearValid) {
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
                    isError = showErrors && !isGenreValid,
                    supportingText = {
                        if (showErrors && !isGenreValid) {
                            Text(text = stringResource(R.string.error_genre_required))
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                )

                OutlinedTextField(
                    value = draft.country,
                    onValueChange = { draft = draft.copy(country = it) },
                    label = { Text(text = stringResource(R.string.label_country)) },
                    singleLine = true,
                    isError = showErrors && !isCountryValid,
                    supportingText = {
                        if (showErrors && !isCountryValid) {
                            Text(text = stringResource(R.string.error_country_required))
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                )

                OutlinedTextField(
                    value = draft.director,
                    onValueChange = { draft = draft.copy(director = it) },
                    label = { Text(text = stringResource(R.string.label_director)) },
                    singleLine = true,
                    isError = showErrors && !isDirectorValid,
                    supportingText = {
                        if (showErrors && !isDirectorValid) {
                            Text(text = stringResource(R.string.error_director_required))
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    showErrors = true
                    if (!canSave) return@TextButton
                    onSave(
                        draft.title.trim(),
                        parsedYear!!,
                        draft.genre.trim(),
                        draft.country.trim(),
                        draft.director.trim(),
                    )
                },
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
