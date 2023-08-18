package com.pablichj.templato.component.core.drawer

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.pablichj.templato.component.core.Component
import com.pablichj.templato.component.core.ComponentLifecycleState
import com.pablichj.templato.component.core.ComponentWithBackStack
import com.pablichj.templato.component.core.EmptyStackMessage
import com.pablichj.templato.component.core.NavItem
import com.pablichj.templato.component.core.NavigationComponent
import com.pablichj.templato.component.core.NavigationComponentDefaultLifecycleHandler
import com.pablichj.templato.component.core.getChildForNextUriFragment
import com.pablichj.templato.component.core.getNavItemFromComponent
import com.pablichj.templato.component.core.onDeepLinkNavigateTo
import com.pablichj.templato.component.core.processBackstackEvent
import com.pablichj.templato.component.core.processBackstackTransition
import com.pablichj.templato.component.core.deeplink.DeepLinkResult
import com.pablichj.templato.component.core.stack.AddAllPushStrategy
import com.pablichj.templato.component.core.stack.PushStrategy
import com.pablichj.templato.component.core.toNavItemDeco
import com.pablichj.templato.component.platform.DispatchersProxy
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DrawerComponent<T : DrawerStatePresenter>(
    val drawerStatePresenter: T,
    config: Config = DefaultConfig,
    private val lifecycleHandler: NavigationComponent.LifecycleHandler = NavigationComponentDefaultLifecycleHandler(),
    dispatchers: DispatchersProxy = DispatchersProxy.DefaultDispatchers,
    private var content: @Composable DrawerComponent<T>.(
        modifier: Modifier,
        childComponent: Component
    ) -> Unit
) : Component(), NavigationComponent, DrawerNavigationProvider {
    override val backStack = createBackStack(config.pushStrategy)
    override var navItems: MutableList<NavItem> = mutableListOf()
    override var selectedIndex: Int = 0
    override var childComponents: MutableList<Component> = mutableListOf()
    override var activeComponent: MutableState<Component?> = mutableStateOf(null)
    private val coroutineScope = CoroutineScope(dispatchers.main)

    init {
        coroutineScope.launch {
            drawerStatePresenter.navItemClickFlow.collect { navItemClick ->
                backStack.push(navItemClick.component)
            }
        }
        backStack.eventListener = { event ->
            val stackTransition = processBackstackEvent(event)
            processBackstackTransition(stackTransition)
        }
    }

    // region: ComponentLifecycle

    override fun onStart() {
        lifecycleHandler.onStart(this)
    }

    override fun onStop() {
        lifecycleHandler.onStop(this)
    }

    override fun onDestroy() {
        lifecycleHandler.onDestroy(this)
    }

    override fun handleBackPressed() {
        println("${instanceId()}::handleBackPressed, backStack.size = ${backStack.size()}")
        if (backStack.size() > 1) {
            backStack.pop()
        } else {
            // We delegate the back event when the stack has 1 element and not 0. The reason is, if
            // we pop all the way to zero the stack empty view will be show for a fraction of
            // milliseconds and this creates an undesirable effect.
            delegateBackPressedToParent()
        }
    }

    // endregion

    // region: IDrawerComponent

    override fun open() {
        println("${instanceId()}::open")
        drawerStatePresenter.setDrawerState(DrawerValue.Open)
    }

    override fun close() {
        println("${instanceId()}::close")
        drawerStatePresenter.setDrawerState(DrawerValue.Closed)
    }

    // endregion

    // region: NavigatorItems

    override fun getComponent(): Component {
        return this
    }

    override fun onSelectNavItem(selectedIndex: Int, navItems: MutableList<NavItem>) {
        val navItemDecoNewList = navItems.map { it.toNavItemDeco() }
        drawerStatePresenter.setNavItemsDeco(navItemDecoNewList)
        drawerStatePresenter.selectNavItemDeco(navItemDecoNewList[selectedIndex])
        if (getComponent().lifecycleState == ComponentLifecycleState.Started) {
            backStack.push(childComponents[selectedIndex])
        }
    }

    override fun updateSelectedNavItem(newTop: Component) {
        getNavItemFromComponent(newTop).let {
            println("${instanceId()}::updateSelectedNavItem(), selectedIndex = $it")
            drawerStatePresenter.selectNavItemDeco(it.toNavItemDeco())
            selectedIndex = childComponents.indexOf(newTop)
        }
    }

    override fun onDestroyChildComponent(component: Component) {
        if (component.lifecycleState == ComponentLifecycleState.Started) {
            component.dispatchStop()
            component.dispatchDestroy()
        } else {
            component.dispatchDestroy()
        }
    }

    // endregion

    // region: DeepLink

    override fun onDeepLinkNavigateTo(matchingComponent: Component): DeepLinkResult {
        return (this as ComponentWithBackStack).onDeepLinkNavigateTo(matchingComponent)
    }

    override fun getChildForNextUriFragment(nextUriFragment: String): Component? {
        return (this as ComponentWithBackStack).getChildForNextUriFragment(nextUriFragment)
    }

    // endregion

    // region Drawer rendering

    @Composable
    override fun Content(modifier: Modifier) {
        println(
            """${instanceId()}.Composing() stack.size = ${backStack.size()}
                |lifecycleState = ${lifecycleState}
            """
        )
        CompositionLocalProvider(
            LocalDrawerNavigationProvider provides this
        ) {
            val activeComponentCopy = activeComponent.value
            if (activeComponentCopy != null) {
                content(modifier, activeComponentCopy)
            } else {
                Text(
                    modifier = Modifier.fillMaxSize(),
                    text = "${instanceId()} $EmptyStackMessage",
                    textAlign = TextAlign.Center
                )
            }
        }
    }

    // endregion

    class Config(
        val pushStrategy: PushStrategy<Component>,
        val drawerHeaderStyle: DrawerHeaderStyle,
        val drawerBodyStyle: DrawerBodyStyle
    )

    companion object {
        val DefaultConfig = Config(
            pushStrategy = AddAllPushStrategy(),
            drawerHeaderStyle = DrawerHeaderStyle(),
            drawerBodyStyle = DrawerBodyStyle()
        )

        fun createDefaultDrawerStatePresenter(
            dispatcher: CoroutineDispatcher = Dispatchers.Main,
            drawerHeaderStyle: DrawerHeaderStyle = DrawerHeaderStyle()
        ): DrawerStatePresenterDefault {
            return DrawerStatePresenterDefault(
                dispatcher,
                DrawerHeaderDefaultState(
                    title = "Header Title",
                    description = "This is the default text. Provide your own text for your App",
                    imageUri = "",
                    style = drawerHeaderStyle
                )
            )
        }

        val DefaultDrawerComponentView: @Composable DrawerComponent<DrawerStatePresenterDefault>.(
            modifier: Modifier,
            childComponent: Component
        ) -> Unit = { modifier, childComponent ->
            NavigationDrawer(
                modifier = modifier,
                statePresenter = drawerStatePresenter
            ) {
                childComponent.Content(Modifier)
            }
        }
    }

}