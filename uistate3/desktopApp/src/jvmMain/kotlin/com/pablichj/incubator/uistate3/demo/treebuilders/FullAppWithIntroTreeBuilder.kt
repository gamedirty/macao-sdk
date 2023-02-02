package com.pablichj.incubator.uistate3.demo.treebuilders

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import com.pablichj.incubator.uistate3.node.*
import com.pablichj.incubator.uistate3.node.drawer.DrawerComponent
import com.pablichj.incubator.uistate3.node.navbar.NavBarComponent
import com.pablichj.incubator.uistate3.node.split.SplitNavComponent
import example.nodes.AppCoordinatorComponent
import example.nodes.TopBarComponent

object FullAppWithIntroTreeBuilder {

    private lateinit var AppCoordinatorComponent: Component

    fun build(): Component {

        if (FullAppWithIntroTreeBuilder::AppCoordinatorComponent.isInitialized) {
            return AppCoordinatorComponent
        }

        return AppCoordinatorComponent().also {
            it.homeComponent = buildDrawerStateTree(it)
            AppCoordinatorComponent = it
        }
    }

    private fun buildDrawerStateTree(parentComponent: Component): Component {
        val DrawerNode = DrawerComponent()
        val NavBarNode = NavBarComponent()

        val SplitNavNode = SplitNavComponent().apply {
            setTopNode(buildNestedDrawer())
            setBottomNode(
                TopBarComponent("Orders / Current", Icons.Filled.Edit) {}
            )
        }

        val navbarNavItems = mutableListOf(
            NodeItem(
                label = "Current",
                icon = Icons.Filled.Home,
                component = TopBarComponent("Orders / Current", Icons.Filled.Home) {},
                selected = false
            ),
            NodeItem(
                label = "Nested Node",
                icon = Icons.Filled.Email,
                component = SplitNavNode,
                selected = false
            )
        )

        val drawerNavItems = mutableListOf(
            NodeItem(
                label = "Home",
                icon = Icons.Filled.Home,
                component = TopBarComponent("Home", Icons.Filled.Home) {},
                selected = false
            ),
            NodeItem(
                label = "Orders",
                icon = Icons.Filled.Edit,
                component = NavBarNode.also { it.setItems(navbarNavItems, 0) },
                selected = false
            )
        )

        return DrawerNode.apply {
            attachToParent(parentComponent)
            setItems(drawerNavItems, 0)
        }
    }

    private fun buildNestedDrawer(): DrawerComponent {
        val DrawerNode = DrawerComponent()
        val NavBarNode = NavBarComponent()

        val navbarNavItems = mutableListOf(
            NodeItem(
                label = "Current",
                icon = Icons.Filled.Home,
                component = TopBarComponent("Orders / Current", Icons.Filled.Home) {},
                selected = false
            ),
            NodeItem(
                label = "Past",
                icon = Icons.Filled.Edit,
                component = TopBarComponent("Orders / Past", Icons.Filled.Edit) {},
                selected = false
            ),
            NodeItem(
                label = "Claim",
                icon = Icons.Filled.Email,
                component = TopBarComponent("Orders / Claim", Icons.Filled.Email) {},
                selected = false
            )
        )

        val drawerNavItems = mutableListOf(
            NodeItem(
                label = "Home Nested",
                icon = Icons.Filled.Home,
                component = TopBarComponent("Home", Icons.Filled.Home) {},
                selected = false
            ),
            NodeItem(
                label = "Orders Nested",
                icon = Icons.Filled.Edit,
                component = NavBarNode.also { it.setItems(navbarNavItems, 0) },
                selected = false
            )
        )

        return DrawerNode.also { it.setItems(drawerNavItems, 0) }
    }

}