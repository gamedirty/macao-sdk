package com.pablichj.encubator.node.drawer

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.pablichj.encubator.node.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
class DrawerNode(
    parentContext: NodeContext,
    // TODO: Ask for the Header Info to render the Drawer header
) : BackStackNode<Node>(parentContext), INavigationProvider, NavigatorNode {

    private val nodeCoroutineScope = CoroutineScope(Dispatchers.Main)// TODO: Use DispatchersBin
    private var activeNode: Node? = null
    private var startingIndex = 0
    private var selectedIndex = 0
    private var navItems: MutableList<NavigatorNodeItem> = mutableListOf()
    private var childNodes: MutableList<Node> = mutableListOf()
    private val navDrawerState = NavigationDrawerState(emptyList())

    init {
        nodeCoroutineScope.launch {
            navDrawerState.navItemClickFlow.collect { navItemClick ->
                pushNode(navItemClick.node)
            }
        }

        context.setNavigationProvider(this)
    }

    override fun start() {
        super.start()
        val childNodesCopy = childNodes
        if (activeNode == null && childNodesCopy.isNotEmpty()) {
            pushNode(childNodesCopy[startingIndex]) // push() will call node.start() on success
        } else {
            activeNode?.start()
        }
    }

    override fun stop() {
        super.stop()
        activeNode?.stop()
    }

    override fun onStackTopChanged(node: Node) {
        activeNode = node

        getNavItemFromNode(node)?.let {
            navDrawerState.selectNavItem(it)
            selectedIndex = childNodes.indexOf(node)
        }

        println("DrawerNode::onStackTopChanged = ${getNavItemFromNode(node)}")
    }

    private fun getNavItemFromNode(node: Node): NavigatorNodeItem? {
        return navDrawerState.navItems.firstOrNull { it.node == node }
    }

    // region: INavigationProvider

    override fun open() {
        println("DrawerNode::open")
        navDrawerState.setDrawerState(DrawerValue.Open)
    }

    override fun close() {
        println("DrawerNode::close")
        navDrawerState.setDrawerState(DrawerValue.Closed)
    }

    // endregion

    // region: NavigatorNode TODO: This code is repeated pretty much in all NavigatorNode classes
    // Take it to a common place

    override fun getNode(): Node {
        return this
    }

    override fun getSelectedNavItemIndex(): Int {
        return selectedIndex
    }

    override fun setNavItems(
        navItems: MutableList<NavigatorNodeItem>,
        startingIndex: Int
    ) {
        this.startingIndex = startingIndex
        this.selectedIndex = startingIndex
        this.navItems = navItems

        this.childNodes = navItems.map { navItem ->
            navItem.node.context.updateParent(context)
            navItem.node
        }.toMutableList()

        navDrawerState.navItems = navItems
        navDrawerState.selectNavItem(navItems[startingIndex])

        if (context.lifecycleState == LifecycleState.Started) {
            pushNode(childNodes[startingIndex])
        }
    }

    override fun getNavItems(): MutableList<NavigatorNodeItem> {
        return this.navItems
    }

    override fun addNavItem(navItem: NavigatorNodeItem, index: Int) {
        navItems.add(index, navItem)
        childNodes.add(index, navItem.node)
        // The call to toMutableList() should return a new stack variable that triggers
        // recomposition in navDrawerState.
        navDrawerState.navItems = navItems.toMutableList()
    }

    override fun removeNavItem(index: Int) {
        navItems.removeAt(index)
        childNodes.removeAt(index)
        // The call to toMutableList() should return a new stack variable that triggers
        // recomposition in navDrawerState.
        navDrawerState.navItems = navItems.toMutableList()
    }

    override fun clearNavItems() {
        navItems.clear()
        childNodes.clear()
        stack.clear()
    }

    // endregion

    @Composable
    override fun Content(modifier: Modifier) {
        println(
            """DrawerNode.Content stack.size = ${stack.size}
                |lifecycleState = ${context.lifecycleState}
            """.trimMargin()
        )

        NavigationDrawer(
            modifier = modifier,
            navDrawerState = navDrawerState
        ) {
            Box {
                if (screenUpdateCounter >= 0 && stack.size > 0) {
                    stack.peek().Content(Modifier)
                } else {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.Center),
                        text = "Empty Stack, Please add some children",
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }

}