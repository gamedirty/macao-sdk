package com.pablichj.templato.component.core.backpress

import androidx.compose.runtime.*
import com.pablichj.templato.component.core.Component
import com.pablichj.templato.component.core.ComponentLifecycleState

/**
 * This [Composable] can be used with a [LocalBackPressedDispatcher] to intercept a back press.
 *
 * @param onBackPressed (Event) What to do when back is intercepted
 *
 */
@Composable
fun BackPressHandler(
    component: Component,
    enabled: Boolean = true,
    onBackPressed: () -> Unit
) {
    // Safely update the current `onBack` lambda when a new one is provided
    val currentOnBackPressed by rememberUpdatedState(onBackPressed)

    // Remember in Composition a back callback that calls the `onBackPressed` lambda
    val backPressCallback = remember(component) {
        ForwardBackPressCallback {
            currentOnBackPressed()
        }
        /* uncomment to test: https://github.com/JetBrains/compose-jb/issues/2615
        object : BackPressedCallback() {
            override fun onBackPressed() {
                currentOnBackPressed()
            }
        }*/
    }

    val backPressDispatcher = LocalBackPressedDispatcher.current
    val componentLifecycleState by component.lifecycleStateFlow.collectAsState(
        ComponentLifecycleState.Created
    )

    when (componentLifecycleState) {
        ComponentLifecycleState.Created -> {
            println("${component.clazz}::Lifecycle Flow = Created, Ignoring")
            // Ignore
        }

        ComponentLifecycleState.Started -> {
            println("${component.clazz}::Lifecycle Flow = Started, BackPressHandler Subscribing")
            backPressDispatcher.subscribe(backPressCallback)
        }

        ComponentLifecycleState.Stopped -> {
            println("${component.clazz}::Lifecycle Flow = Stopped, BackPressHandler Unsubscribing")
            backPressDispatcher.unsubscribe(backPressCallback)
        }

        ComponentLifecycleState.Destroyed -> {
            println("${component.clazz}::Lifecycle Flow = Destroyed, Ignoring")
            // Ignore it did unsubscribe in Stopped already.
        }
    }

    // Whenever there's a new dispatcher set up the callback
    /*DisposableEffect(backPressDispatcher) {
        println("Subscribing to backPressDispatcher, class = className here")
        backPressDispatcher.subscribe(backCallback)
        // When the effect leaves the Composition, or there's a new dispatcher, remove the callback
        onDispose {
            println("BackPressHandler::onDispose Unsubscribing from backPressDispatcher, class = className here")
            backPressDispatcher.unsubscribe(backCallback)
        }
    }*/
}

/**
 * This [CompositionLocal] is used to provide an [IBackPressDispatcher]:
 *
 * ```
 * val backPressedDispatcher = AndroidBackPressedDispatcher
 *
 * CompositionLocalProvider(
 *     LocalBackPressedDispatcher provides backPressedDispatcher
 * ) { }
 * ```
 *
 * and setting up the callbacks with [BackPressHandler].
 */
val LocalBackPressedDispatcher =
    staticCompositionLocalOf<IBackPressDispatcher> { DefaultBackPressDispatcher() }
