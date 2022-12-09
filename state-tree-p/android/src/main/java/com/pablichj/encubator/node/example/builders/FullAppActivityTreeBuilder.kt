package com.pablichj.encubator.node.example.builders

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import com.pablichj.encubator.node.*
import com.pablichj.encubator.node.drawer.DrawerNode
import com.pablichj.encubator.node.nodes.SplitNavNode
import com.pablichj.encubator.node.navbar.NavBarNode
import com.pablichj.encubator.node.nodes.AppCoordinatorNode
import com.pablichj.encubator.node.nodes.OnboardingNode

object FullAppActivityTreeBuilder {

    private val rootParentNodeContext = NodeContext(null)
    private lateinit var AppCoordinatorNode: Node

    fun build(
        backPressDispatcher: IBackPressDispatcher,
        backPressedCallback: BackPressedCallback
    ): Node {

        rootParentNodeContext.backPressDispatcher = backPressDispatcher
        rootParentNodeContext.backPressedCallbackDelegate = backPressedCallback

        if (FullAppActivityTreeBuilder::AppCoordinatorNode.isInitialized) {
            return AppCoordinatorNode
        }

        return AppCoordinatorNode(rootParentNodeContext).also {
            it.HomeNode = buildDrawerActivityStateTree(it.context)
            AppCoordinatorNode = it
        }
    }

    private fun buildDrawerActivityStateTree(parentContext: NodeContext): Node {

        val DrawerNode = DrawerNode(parentContext)

        val NavBarNode = NavBarNode(DrawerNode.context)
        val PagerNode = PagerNode(DrawerNode.context)

        val SplitNavNode = SplitNavNode(NavBarNode.context).apply {
            TopNode = buildNestedDrawer(context)
            BottomNode = OnboardingNode(NavBarNode.context, "Orders / Current", Icons.Filled.Edit) {}
        }

        val navbarNavItems = mutableListOf(
            NavigatorNodeItem(
                label = "Current",
                icon = Icons.Filled.Home,
                node = OnboardingNode(NavBarNode.context, "Orders / Current", Icons.Filled.Home) {},
                selected = false
            ),
            NavigatorNodeItem(
                label = "Past",
                icon = Icons.Filled.Edit,
                node = OnboardingNode(NavBarNode.context, "Orders / Past", Icons.Filled.Edit) {},
                selected = false
            ),
            NavigatorNodeItem(
                label = "Claim",
                icon = Icons.Filled.Email,
                node = OnboardingNode(NavBarNode.context, "Orders / Claim", Icons.Filled.Email) {},
                selected = false
            ),
            NavigatorNodeItem(
                label = "Nested Node",
                icon = Icons.Filled.Email,
                node = SplitNavNode,
                selected = false
            )
        )

        val pagerNavItems = mutableListOf(
            NavigatorNodeItem(
                label = "Account",
                icon = Icons.Filled.Home,
                node = OnboardingNode(PagerNode.context, "Settings / Account", Icons.Filled.Home) {},
                selected = false
            ),
            NavigatorNodeItem(
                label = "Profile",
                icon = Icons.Filled.Edit,
                node = OnboardingNode(PagerNode.context, "Settings / Profile", Icons.Filled.Edit) {},
                selected = false
            ),
            NavigatorNodeItem(
                label = "About Us",
                icon = Icons.Filled.Email,
                node = OnboardingNode(PagerNode.context, "Settings / About Us", Icons.Filled.Email) {},
                selected = false
            )
        )

        val drawerNavItems = mutableListOf(
            NavigatorNodeItem(
                label = "Home",
                icon = Icons.Filled.Home,
                node = OnboardingNode(DrawerNode.context, "Home", Icons.Filled.Home) {},
                selected = false
            ),
            NavigatorNodeItem(
                label = "Orders",
                icon = Icons.Filled.Edit,
                node = NavBarNode.also { it.setNavItems(navbarNavItems, 0) },
                selected = false
            ),
            NavigatorNodeItem(
                label = "Settings",
                icon = Icons.Filled.Email,
                node = PagerNode.also { it.setNavItems(pagerNavItems, 0) },
                selected = false
            )
        )

        return DrawerNode.apply {
            setNavItems(drawerNavItems, 0)
        }
    }

    private fun buildNestedDrawer(parentContext: NodeContext): DrawerNode {

        val DrawerNode = DrawerNode(parentContext)
        val NavBarNode = NavBarNode(DrawerNode.context)

        val navbarNavItems = mutableListOf(
            NavigatorNodeItem(
                label = "Current",
                icon = Icons.Filled.Home,
                node = OnboardingNode(NavBarNode.context, "Orders / Current", Icons.Filled.Home) {},
                selected = false
            ),
            NavigatorNodeItem(
                label = "Past",
                icon = Icons.Filled.Edit,
                node = OnboardingNode(NavBarNode.context, "Orders / Past", Icons.Filled.Edit) {},
                selected = false
            ),
            NavigatorNodeItem(
                label = "Claim",
                icon = Icons.Filled.Email,
                node = OnboardingNode(NavBarNode.context, "Orders / Claim", Icons.Filled.Email) {},
                selected = false
            )
        )

        val drawerNavItems = mutableListOf(
            NavigatorNodeItem(
                label = "Home Nested",
                icon = Icons.Filled.Home,
                node = OnboardingNode(DrawerNode.context, "Home", Icons.Filled.Home) {},
                selected = false
            ),
            NavigatorNodeItem(
                label = "Orders Nested",
                icon = Icons.Filled.Edit,
                node = NavBarNode.also { it.setNavItems(navbarNavItems, 0) },
                selected = false
            )
        )

        return DrawerNode.also { it.setNavItems(drawerNavItems, 0) }
    }

}