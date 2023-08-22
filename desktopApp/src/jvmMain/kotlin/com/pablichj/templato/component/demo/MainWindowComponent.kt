package com.pablichj.templato.component.demo

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.MenuBar
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import com.pablichj.templato.component.core.Component
import com.pablichj.templato.component.core.DesktopComponentRender
import com.pablichj.templato.component.core.deeplink.DeepLinkMsg
import com.pablichj.templato.component.core.deeplink.DefaultDeepLinkManager
import com.pablichj.templato.component.core.drawer.DrawerComponent
import com.pablichj.templato.component.core.navbar.NavBarComponent
import com.pablichj.templato.component.core.panel.PanelComponent
import com.pablichj.templato.component.core.topbar.TopBarComponent
import com.pablichj.templato.component.demo.treebuilders.AdaptableSizeTreeBuilder
import com.pablichj.templato.component.platform.CoroutineDispatchers
import com.pablichj.templato.component.platform.DesktopBridge

class MainWindowComponent(
    val onOpenDeepLinkClick: () -> Unit,
    val onRootNodeSelection: (WindowSample) -> Unit,
    val onExitClick: () -> Unit
) : Component() {
    private val windowState = WindowState(size = DpSize(1000.dp, 900.dp))
    private var adaptableSizeComponent: Component
    private val desktopBridge = DesktopBridge()

    init {
        val subtreeNavItems = AdaptableSizeTreeBuilder.getOrCreateDetachedNavItems()
        adaptableSizeComponent = AdaptableSizeTreeBuilder.build().also {
            it.setNavItems(subtreeNavItems, 0)
            it.setCompactContainer(
                DrawerComponent(
                    drawerStatePresenter = DrawerComponent.createDefaultDrawerStatePresenter(),
                    content = DrawerComponent.DefaultDrawerComponentView
                )
            )
            //it.setCompactContainer(PagerComponent())
            it.setMediumContainer(
                NavBarComponent(
                    navBarStatePresenter = NavBarComponent.createDefaultNavBarStatePresenter(),
                    content = NavBarComponent.DefaultNavBarComponentView
                )
            )
            it.setExpandedContainer(
                PanelComponent(
                    panelStatePresenter = PanelComponent.createDefaultPanelStatePresenter(),
                    content = PanelComponent.DefaultPanelComponentView
                )
            )
        }
    }

    // region: DeepLink

    fun handleDeepLink(destinations: List<String>) {
        val deepLinkMsg = DeepLinkMsg(
            path = destinations,
            resultListener = { dlResult, component ->
                println("MainWindowNode::deepLinkResult = $dlResult")
            }
        )
        DefaultDeepLinkManager().navigateToDeepLink(adaptableSizeComponent, deepLinkMsg)
    }

    // endregion

    @Composable
    override fun Content(modifier: Modifier) {
        Window(
            state = windowState,
            onCloseRequest = { onExitClick() }
        ) {
            MenuBar {
                Menu("Actions") {
                    Item(
                        "Deep Link",
                        onClick = {
                            onOpenDeepLinkClick()
                        }
                    )
                    Item(
                        "Exit",
                        onClick = {
                            onExitClick()
                        }
                    )
                }
                Menu("Samples") {
                    Item(
                        "Slide Drawer",
                        onClick = {
                            onRootNodeSelection(WindowSample.Drawer)
                        }
                    )
                    Item(
                        "Nav Bottom Bar",
                        onClick = {
                            onRootNodeSelection(WindowSample.Navbar)
                        }
                    )
                    Item(
                        "Left Panel",
                        onClick = {
                            onRootNodeSelection(WindowSample.Panel)
                        }
                    )
                    Item(
                        "Full App Sample",
                        onClick = {
                            onRootNodeSelection(WindowSample.FullApp)
                        }
                    )
                }
            }
            DesktopComponentRender(
                rootComponent = adaptableSizeComponent,
                windowState = windowState,
                onBackPress = onExitClick,
                desktopBridge = desktopBridge
            )
        }
    }

}

@Preview
@Composable
fun MainWindowComponentPreview() {

    /*
    val simpleComponent = SimpleComponent(
        "Simple",
        Color.Yellow,
    ) {}
    simpleComponent.Content(Modifier)
    */

    val topbarComponent = CustomTopBarComponent(
        "CustomTopBarComponent"
    ) {}
    topbarComponent.dispatchStart()
    topbarComponent.Content(Modifier)
}
