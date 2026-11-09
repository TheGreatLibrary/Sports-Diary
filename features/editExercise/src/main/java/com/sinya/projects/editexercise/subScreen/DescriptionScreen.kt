package com.sinya.projects.editexercise.subScreen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil.compose.AsyncImage
import com.sinya.projects.editexercise.R
import com.sinya.projects.ui.components.EmptyMediaPlaceholder
import com.sinya.projects.ui.components.textField.CustomTextField
import com.sinya.projects.ui.components.textField.ErrorTextField

@Composable
internal fun DescriptionScreen(
    modifier: Modifier,
    name: String,
    isNameError: Boolean,
    namePlaceholder: String,
    onNameChange: (String) -> Unit,
    description: String,
    descriptionPlaceholder: String,
    onDescriptionChange: (String) -> Unit,
    imageUri: String?,
    onImageSelected: (Uri) -> Unit
) {
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let { onImageSelected(it) }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 20.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ErrorTextField(
            value = name,
            onValueChange = onNameChange,
            placeholder = namePlaceholder,
            modifier = Modifier
                .heightIn(max = 40.dp)
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainerLowest,
                    shape = MaterialTheme.shapes.extraLarge
                ),
            leadingIcon = {
                Icon(
                    painterResource(com.sinya.projects.ui.R.drawable.train_muscul),
                    contentDescription = "menu",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(18.dp)
                )
            },
            contentColor = MaterialTheme.colorScheme.onSurface,
            isError = isNameError,
            errorMessage = stringResource(com.sinya.projects.ui.R.string.exercise_name_is_empty),
            shape = MaterialTheme.shapes.extraLarge,
        )

        MediaFile(
            imageUri = imageUri,
            addImage = {
                photoPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            }
        )

        CustomTextField(
            value = description,
            onValueChange = onDescriptionChange,
            placeholder = descriptionPlaceholder,
            maxLines = 15,
            minLines = 5,
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainerLowest,
                    shape = MaterialTheme.shapes.small
                ),
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
internal fun MediaFile(
    imageUri: String?,
    addImage: () -> Unit
) {
    if (imageUri.isNullOrEmpty()) {
        EmptyMediaPlaceholder(
            modifier = Modifier.clickable { addImage() },
            stringResource(R.string.no_photo)
        )
        return
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 100.dp)
            .padding(bottom = 10.dp)
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            .clickable { addImage() }
            .padding(1.dp),
        contentAlignment = Alignment.Center
    ) {


        when (imageUri) {
            null -> Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    painter = painterResource(com.sinya.projects.ui.R.drawable.ic_plus),
                    contentDescription = "Add photo",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(30.dp)
                )
                Text(
                    text = stringResource(R.string.add_photo),
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            else -> AsyncImage(
                model = imageUri.toUri(),
                contentDescription = "Selected image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(MaterialTheme.shapes.small)
            )
        }
    }
}