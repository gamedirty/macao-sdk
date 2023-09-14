package com.macaosoftware.component.stack

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.macaosoftware.component.core.Component

object StackComponentDefaults {

    val DefaultStackComponentView: @Composable StackComponent<StackStatePresenterDefault>.(
        modifier: Modifier,
        activeChildComponent: Component
    ) -> Unit = { modifier, activeChildComponent ->
        Box {
            PredictiveBackstackView(
                modifier = modifier,
                predictiveComponent = activeChildComponent,
                backStack = backStack,
                lastBackstackEvent = lastBackstackEvent,
                onComponentSwipedOut = {}
            )
        }
    }
}