package com.pablichj.incubator.uistate3.demo.treebuilders

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import com.pablichj.incubator.uistate3.node.NodeItem
import com.pablichj.incubator.uistate3.node.navbar.NavBarComponent
import com.pablichj.incubator.uistate3.node.setItems
import example.nodes.TopBarComponent

object NavBarTreeBuilder {

    private lateinit var NavBarNode: NavBarComponent

    fun build(): NavBarComponent {

        if (NavBarTreeBuilder::NavBarNode.isInitialized) {
            return NavBarNode
        }

        val NavBarNode = NavBarComponent()

        val navbarNavItems = mutableListOf(
            NodeItem(
                label = "Home",
                icon = Icons.Filled.Home,
                component = TopBarComponent("Home", Icons.Filled.Home) {},
                selected = false
            ),
            NodeItem(
                label = "Orders",
                icon = Icons.Filled.Settings,
                component = TopBarComponent("Orders", Icons.Filled.Settings) {},
                selected = false
            ),
            NodeItem(
                label = "Settings",
                icon = Icons.Filled.Add,
                component = TopBarComponent("Settings", Icons.Filled.Add) {},
                selected = false
            )
        )

        return NavBarNode.also { it.setItems(navbarNavItems, 0) }
    }

}