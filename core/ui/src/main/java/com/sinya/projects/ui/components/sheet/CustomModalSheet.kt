package com.sinya.projects.ui.components.sheet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.sinya.projects.ui.theme.LocalAppGradients

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomModalSheet(
    modifier: Modifier = Modifier.verticalScroll(rememberScrollState()),
    onDismiss: () -> Unit,
    content: @Composable () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topEnd = 30.dp, topStart = 30.dp),
        contentColor = MaterialTheme.colorScheme.onPrimary,
        containerColor = Color.Transparent,
        dragHandle = null,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .then(modifier)
                .background(
                    brush = LocalAppGradients.current.basedGradient,
                    shape = RoundedCornerShape(topEnd = 30.dp, topStart = 30.dp),
                )
                .padding(16.dp)
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            BottomSheetDefaults.DragHandle()

            content()
        }
    }
}