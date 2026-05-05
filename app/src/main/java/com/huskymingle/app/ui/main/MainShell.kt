package com.huskymingle.app.ui.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.huskymingle.app.ui.auth.AuthViewModel
import com.huskymingle.app.ui.navigation.Screen
import com.huskymingle.app.ui.theme.HuskyRed
import kotlinx.coroutines.launch

data class BottomNavItem(
    val label: String,
    val route: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

data class DrawerItem(
    val label: String,
    val route: String,
    val icon: ImageVector,
    val section: String
)

val bottomNavItems = listOf(
    BottomNavItem("Feed", Screen.Feed.route, Icons.Filled.Home, Icons.Outlined.Home),
    BottomNavItem("Explore", Screen.Explore.route, Icons.Filled.Explore, Icons.Outlined.Explore),
    BottomNavItem("Messages", Screen.Messages.route, Icons.Filled.Message, Icons.Outlined.Message),
    BottomNavItem("Market", Screen.Marketplace.route, Icons.Filled.Store, Icons.Outlined.Store),
    BottomNavItem("Profile", Screen.Profile.route, Icons.Filled.Person, Icons.Outlined.Person)
)

val drawerItems = listOf(
    DrawerItem("Feed", Screen.Feed.route, Icons.Default.Home, "Main"),
    DrawerItem("Explore", Screen.Explore.route, Icons.Default.Explore, "Main"),
    DrawerItem("Events", Screen.Events.route, Icons.Default.Event, "Campus"),
    DrawerItem("Jobs & Internships", Screen.Jobs.route, Icons.Default.Work, "Campus"),
    DrawerItem("Communities", Screen.Communities.route, Icons.Default.Group, "Campus"),
    DrawerItem("Messages", Screen.Messages.route, Icons.Default.Message, "Social"),
    DrawerItem("Notifications", Screen.Notifications.route, Icons.Default.Notifications, "Social"),
    DrawerItem("Search", Screen.Search.route, Icons.Default.Search, "Social"),
    DrawerItem("Marketplace", Screen.Marketplace.route, Icons.Default.Store, "Social"),
    DrawerItem("Courses", Screen.Courses.route, Icons.Default.School, "Academic"),
    DrawerItem("Q & A", Screen.Qa.route, Icons.Default.QuestionAnswer, "Academic"),
    DrawerItem("Polls", Screen.Polls.route, Icons.Default.Poll, "Academic"),
    DrawerItem("Gaming", Screen.Gaming.route, Icons.Default.SportsEsports, "Entertainment"),
    DrawerItem("Audio Rooms", Screen.Audio.route, Icons.Default.Mic, "Entertainment"),
    DrawerItem("Reels", Screen.Reels.route, Icons.Default.VideoLibrary, "Entertainment"),
    DrawerItem("Live", Screen.Live.route, Icons.Default.LiveTv, "Entertainment"),
    DrawerItem("Bookmarks", Screen.Bookmarks.route, Icons.Default.Bookmark, "Other"),
    DrawerItem("Profile", Screen.Profile.route, Icons.Default.Person, "Other"),
    DrawerItem("Settings", Screen.Settings.route, Icons.Default.Settings, "Other")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainShell(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    content: @Composable (onMenuOpen: () -> Unit) -> Unit
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val sections = drawerItems.map { it.section }.distinct()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState())
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                    ) {
                        Column {
                            Text(
                                "HuskyMingle",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = HuskyRed
                            )
                            Text(
                                "Northeastern University",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    HorizontalDivider()

                    sections.forEach { section ->
                        val sectionItems = drawerItems.filter { it.section == section }
                        Text(
                            section,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = HuskyRed,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                        sectionItems.forEach { item ->
                            NavigationDrawerItem(
                                label = { Text(item.label) },
                                icon = { Icon(item.icon, contentDescription = null) },
                                selected = currentRoute == item.route,
                                onClick = {
                                    scope.launch { drawerState.close() }
                                    navController.navigate(item.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                colors = NavigationDrawerItemDefaults.colors(
                                    selectedContainerColor = HuskyRed.copy(alpha = 0.12f),
                                    selectedIconColor = HuskyRed,
                                    selectedTextColor = HuskyRed
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    ) {
        Scaffold(
            bottomBar = {
                NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                    bottomNavItems.forEach { item ->
                        val selected = currentRoute == item.route
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    if (selected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.label
                                )
                            },
                            label = { Text(item.label, fontSize = 10.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = HuskyRed,
                                selectedTextColor = HuskyRed,
                                indicatorColor = HuskyRed.copy(alpha = 0.12f)
                            )
                        )
                    }
                }
            }
        ) { padding ->
            Box(modifier = Modifier.padding(padding)) {
                content { scope.launch { drawerState.open() } }
            }
        }
    }
}
