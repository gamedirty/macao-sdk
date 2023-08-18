package com.pablichj.templato.component.demo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pablichj.templato.component.core.Component
import com.pablichj.templato.component.core.consumeBackPressEvent
import com.pablichj.templato.component.core.deeplink.DeepLinkMsg
import com.pablichj.templato.component.core.deeplink.LocalRootComponentProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SimpleRequestComponent(
    val screenName: String,
    val bgColor: Color
) : Component() {

    private var result by mutableStateOf("")
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    override fun onStart() {
        println("${instanceId()}::onStart()")
    }

    override fun onStop() {
        println("${instanceId()}::onStop()")
    }

    private fun subscribeToSimpleResponseComponent(component: Component?) {
        if (component == null) {
            println("SimpleResponseComponent not found in component tree")
            return
        }
        val responseComponent = component as? SimpleResponseComponent
        if (responseComponent == null) {
            println("Cast to SimpleResponseComponent failed")
            return
        }

        coroutineScope.launch {
            println("Pablo launch response subscription")
            responseComponent.resultFlow.collect {
                println("Pablo received response: $it")
                result = it
            }
        }
    }

    @Composable
    override fun Content(modifier: Modifier) {
        println("${instanceId()}::Composing()")
        consumeBackPressEvent()

        val rootComponent = LocalRootComponentProvider.current

        Column(
            modifier = modifier.fillMaxSize()
                .background(bgColor)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                modifier = Modifier.padding(vertical = 40.dp),
                onClick = {
                    rootComponent?.navigateToDeepLink(
                        DeepLinkMsg(
                            path = listOf("_navigator_adaptive", "*", "Settings", "Page 3"),
                            resultListener = { result, component ->
                                println("$screenName deeplink result: $result")
                                subscribeToSimpleResponseComponent(component)
                            }
                        )
                    )
                }
            ) {
                Text(text = "Go To Settings/Page 3")
            }
            Spacer(modifier.padding(24.dp))
            if (result.isNotEmpty()) {
                Text(
                    text = "Response: ${result}",
                    fontSize = 20.sp
                )
            }
        }
    }

}