package com.example.mystudyproject1

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AboutAppScreen(
    onBack: () -> Unit,
    stScope: SharedTransitionScope,
    avScope: AnimatedVisibilityScope
) {
    with(stScope){
        Column(
            modifier = Modifier
                .padding(top = 40.dp, start = 30.dp)
                .fillMaxSize()
                .sharedElement(
                    rememberSharedContentState("drawer_item"),
                    avScope
                ),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(16.dp)


        ){
            IconButton(
                onClick = {
                    onBack()
                }
            ) {
                Icon(
                    Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = "back_icon"
                )
            }

            Text(
                text = "Read me text..."
            )
        }
    }

}