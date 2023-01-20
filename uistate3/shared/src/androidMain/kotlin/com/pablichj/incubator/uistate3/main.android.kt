package com.pablichj.incubator.uistate3

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.pablichj.incubator.uistate3.ComposeApp
import com.pablichj.incubator.uistate3.node.AndroidBackPressDispatcher
import com.pablichj.incubator.uistate3.node.LocalBackPressedDispatcher

@Composable
fun AndroidComposeApp(componentActivity: ComponentActivity) {
    CompositionLocalProvider(
        LocalBackPressedDispatcher provides AndroidBackPressDispatcher(componentActivity),
    ) {
        ComposeApp(
            onExit = { componentActivity.finishAffinity() }
        )
    }
}