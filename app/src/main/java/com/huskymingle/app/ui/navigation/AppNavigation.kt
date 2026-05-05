package com.huskymingle.app.ui.navigation

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.huskymingle.app.ui.audio.AudioScreen
import com.huskymingle.app.ui.auth.*
import com.huskymingle.app.ui.bookmarks.BookmarksScreen
import com.huskymingle.app.ui.communities.CommunitiesScreen
import com.huskymingle.app.ui.courses.CoursesScreen
import com.huskymingle.app.ui.events.EventsScreen
import com.huskymingle.app.ui.explore.ExploreScreen
import com.huskymingle.app.ui.feed.FeedScreen
import com.huskymingle.app.ui.gaming.GamingScreen
import com.huskymingle.app.ui.jobs.JobsScreen
import com.huskymingle.app.ui.live.LiveScreen
import com.huskymingle.app.ui.main.MainShell
import com.huskymingle.app.ui.marketplace.MarketplaceScreen
import com.huskymingle.app.ui.messages.MessagesScreen
import com.huskymingle.app.ui.notifications.NotificationsScreen
import com.huskymingle.app.ui.polls.PollsScreen
import com.huskymingle.app.ui.profile.ProfileScreen
import com.huskymingle.app.ui.qa.QaScreen
import com.huskymingle.app.ui.reels.ReelsScreen
import com.huskymingle.app.ui.search.SearchScreen
import com.huskymingle.app.ui.settings.SettingsScreen

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object VerifyEmail : Screen("verify_email/{email}") {
        fun createRoute(email: String) = "verify_email/$email"
    }
    object Onboarding : Screen("onboarding")
    object Feed : Screen("feed")
    object Explore : Screen("explore")
    object Events : Screen("events")
    object Jobs : Screen("jobs")
    object Messages : Screen("messages")
    object Communities : Screen("communities")
    object Marketplace : Screen("marketplace")
    object Notifications : Screen("notifications")
    object Search : Screen("search")
    object Profile : Screen("profile")
    object Settings : Screen("settings")
    object Gaming : Screen("gaming")
    object Polls : Screen("polls")
    object Qa : Screen("qa")
    object Courses : Screen("courses")
    object Audio : Screen("audio")
    object Reels : Screen("reels")
    object Live : Screen("live")
    object Bookmarks : Screen("bookmarks")
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val authState by authViewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Loading -> Unit
            is AuthState.LoggedOut -> navController.navigate(Screen.Login.route) {
                popUpTo(0) { inclusive = true }
            }
            is AuthState.NeedsOnboarding -> navController.navigate(Screen.Onboarding.route) {
                popUpTo(0) { inclusive = true }
            }
            is AuthState.LoggedIn -> navController.navigate(Screen.Feed.route) {
                popUpTo(0) { inclusive = true }
            }
            is AuthState.Error -> navController.navigate(Screen.Login.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                viewModel = authViewModel,
                onNavigateToRegister = { navController.navigate(Screen.Register.route) }
            )
        }
        composable(Screen.Register.route) {
            RegisterScreen(
                viewModel = authViewModel,
                onNavigateToLogin = { navController.popBackStack() },
                onNavigateToVerify = { email ->
                    navController.navigate(Screen.VerifyEmail.createRoute(email))
                }
            )
        }
        composable(
            route = Screen.VerifyEmail.route,
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            VerifyEmailScreen(
                email = email,
                viewModel = authViewModel,
                onVerified = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Onboarding.route) {
            OnboardingScreen(viewModel = authViewModel)
        }
        composable(Screen.Feed.route) {
            MainShell(navController = navController, authViewModel = authViewModel) { onMenuOpen ->
                FeedScreen(onMenuOpen = onMenuOpen)
            }
        }
        composable(Screen.Explore.route) {
            MainShell(navController = navController, authViewModel = authViewModel) { onMenuOpen ->
                ExploreScreen(onMenuOpen = onMenuOpen)
            }
        }
        composable(Screen.Events.route) {
            MainShell(navController = navController, authViewModel = authViewModel) { onMenuOpen ->
                EventsScreen(onMenuOpen = onMenuOpen)
            }
        }
        composable(Screen.Jobs.route) {
            MainShell(navController = navController, authViewModel = authViewModel) { onMenuOpen ->
                JobsScreen(onMenuOpen = onMenuOpen)
            }
        }
        composable(Screen.Messages.route) {
            MainShell(navController = navController, authViewModel = authViewModel) { onMenuOpen ->
                MessagesScreen(onMenuOpen = onMenuOpen)
            }
        }
        composable(Screen.Communities.route) {
            MainShell(navController = navController, authViewModel = authViewModel) { onMenuOpen ->
                CommunitiesScreen(onMenuOpen = onMenuOpen)
            }
        }
        composable(Screen.Marketplace.route) {
            MainShell(navController = navController, authViewModel = authViewModel) { onMenuOpen ->
                MarketplaceScreen(onMenuOpen = onMenuOpen)
            }
        }
        composable(Screen.Notifications.route) {
            MainShell(navController = navController, authViewModel = authViewModel) { onMenuOpen ->
                NotificationsScreen(onMenuOpen = onMenuOpen)
            }
        }
        composable(Screen.Search.route) {
            MainShell(navController = navController, authViewModel = authViewModel) { onMenuOpen ->
                SearchScreen(onMenuOpen = onMenuOpen)
            }
        }
        composable(Screen.Profile.route) {
            MainShell(navController = navController, authViewModel = authViewModel) { onMenuOpen ->
                ProfileScreen(authViewModel = authViewModel, onMenuOpen = onMenuOpen)
            }
        }
        composable(Screen.Settings.route) {
            MainShell(navController = navController, authViewModel = authViewModel) { onMenuOpen ->
                SettingsScreen(authViewModel = authViewModel, onMenuOpen = onMenuOpen)
            }
        }
        composable(Screen.Gaming.route) {
            MainShell(navController = navController, authViewModel = authViewModel) { onMenuOpen ->
                GamingScreen(onMenuOpen = onMenuOpen)
            }
        }
        composable(Screen.Polls.route) {
            MainShell(navController = navController, authViewModel = authViewModel) { onMenuOpen ->
                PollsScreen(onMenuOpen = onMenuOpen)
            }
        }
        composable(Screen.Qa.route) {
            MainShell(navController = navController, authViewModel = authViewModel) { onMenuOpen ->
                QaScreen(onMenuOpen = onMenuOpen)
            }
        }
        composable(Screen.Courses.route) {
            MainShell(navController = navController, authViewModel = authViewModel) { onMenuOpen ->
                CoursesScreen(onMenuOpen = onMenuOpen)
            }
        }
        composable(Screen.Audio.route) {
            MainShell(navController = navController, authViewModel = authViewModel) { onMenuOpen ->
                AudioScreen(onMenuOpen = onMenuOpen)
            }
        }
        composable(Screen.Reels.route) {
            MainShell(navController = navController, authViewModel = authViewModel) { onMenuOpen ->
                ReelsScreen(onMenuOpen = onMenuOpen)
            }
        }
        composable(Screen.Live.route) {
            MainShell(navController = navController, authViewModel = authViewModel) { onMenuOpen ->
                LiveScreen(onMenuOpen = onMenuOpen)
            }
        }
        composable(Screen.Bookmarks.route) {
            MainShell(navController = navController, authViewModel = authViewModel) { onMenuOpen ->
                BookmarksScreen(onMenuOpen = onMenuOpen)
            }
        }
    }
}
