package com.huskymingle.app.ui.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.huskymingle.app.HuskyMingleApp
import com.huskymingle.app.security.BiometricService
import com.huskymingle.app.ui.audio.AudioScreen
import com.huskymingle.app.ui.auth.*
import com.huskymingle.app.ui.bookmarks.BookmarksScreen
import com.huskymingle.app.ui.circles.CircleDetailScreen
import com.huskymingle.app.ui.circles.CirclesScreen
import com.huskymingle.app.ui.circles.CreateCircleScreen
import com.huskymingle.app.ui.communities.CommunitiesScreen
import com.huskymingle.app.ui.courses.CourseDetailScreen
import com.huskymingle.app.ui.courses.CoursesScreen
import com.huskymingle.app.ui.events.EventsScreen
import com.huskymingle.app.ui.explore.ExploreScreen
import com.huskymingle.app.ui.feed.FeedScreen
import com.huskymingle.app.ui.feed.PostDetailScreen
import com.huskymingle.app.ui.gaming.GamingScreen
import com.huskymingle.app.ui.jobs.JobsScreen
import com.huskymingle.app.ui.live.LiveScreen
import com.huskymingle.app.ui.main.MainShell
import com.huskymingle.app.ui.marketplace.MarketplaceItemScreen
import com.huskymingle.app.ui.marketplace.MarketplaceScreen
import com.huskymingle.app.ui.messages.ChatScreen
import com.huskymingle.app.ui.messages.MessagesScreen
import com.huskymingle.app.ui.notifications.NotificationsScreen
import com.huskymingle.app.ui.polls.PollsScreen
import com.huskymingle.app.ui.profile.ProfileScreen
import com.huskymingle.app.ui.profile.UserListKind
import com.huskymingle.app.ui.profile.UserListScreen
import com.huskymingle.app.ui.profile.UserProfileScreen
import com.huskymingle.app.ui.qa.QaScreen
import com.huskymingle.app.ui.reels.ReelsScreen
import com.huskymingle.app.ui.search.SearchScreen
import com.huskymingle.app.ui.settings.SettingsScreen
import com.huskymingle.app.ui.stories.CreateStoryScreen
import com.huskymingle.app.ui.stories.StoryViewerScreen
import kotlinx.coroutines.delay

private const val SPLASH_MIN_HOLD_MS = 1_400L

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object BiometricLock : Screen("biometric_lock")
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
    object CreateStory : Screen("create_story")
    object StoryViewer : Screen("story_viewer/{storyId}") {
        fun createRoute(storyId: String) = "story_viewer/$storyId"
    }
    object PostDetail : Screen("post_detail/{postId}") {
        fun createRoute(postId: String) = "post_detail/$postId"
    }
    object UserProfile : Screen("user_profile/{username}") {
        fun createRoute(username: String) = "user_profile/$username"
    }
    object FollowerList : Screen("user_list/{username}/{kind}") {
        fun createRoute(username: String, kind: String) = "user_list/$username/$kind"
    }
    object Chat : Screen("chat/{conversationId}") {
        fun createRoute(conversationId: String) = "chat/$conversationId"
    }
    object MarketplaceItem : Screen("marketplace/{itemId}") {
        fun createRoute(itemId: String) = "marketplace/$itemId"
    }
    object Circles : Screen("circles")
    object CircleDetail : Screen("circle/{circleId}") {
        fun createRoute(circleId: String) = "circle/$circleId"
    }
    object CreateCircle : Screen("circle_create")
    object CourseDetail : Screen("course/{courseCode}") {
        fun createRoute(courseCode: String) = "course/$courseCode"
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val authState by authViewModel.authState.collectAsState()

    val context = LocalContext.current
    val prefs = remember { (context.applicationContext as HuskyMingleApp).userPreferences }
    val biometricEnabled by prefs.biometricEnabled.collectAsState(initial = false)

    var splashHoldElapsed by remember { mutableStateOf(false) }
    var biometricUnlocked by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(SPLASH_MIN_HOLD_MS)
        splashHoldElapsed = true
    }

    LaunchedEffect(authState, splashHoldElapsed, biometricUnlocked, biometricEnabled) {
        if (!splashHoldElapsed) return@LaunchedEffect
        when (authState) {
            AuthState.Loading -> Unit
            AuthState.LoggedOut -> {
                biometricUnlocked = false
                navController.navigate(Screen.Login.route) {
                    popUpTo(0) { inclusive = true }
                }
            }
            is AuthState.NeedsOnboarding -> navController.navigate(Screen.Onboarding.route) {
                popUpTo(0) { inclusive = true }
            }
            is AuthState.LoggedIn -> {
                val needsBiometric =
                    biometricEnabled && BiometricService.isAvailable(context) && !biometricUnlocked
                val target = if (needsBiometric) Screen.BiometricLock.route else Screen.Feed.route
                navController.navigate(target) {
                    popUpTo(0) { inclusive = true }
                }
            }
            is AuthState.Error -> navController.navigate(Screen.Login.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen()
        }
        composable(Screen.BiometricLock.route) {
            BiometricLockScreen(
                onSuccess = {
                    biometricUnlocked = true
                    navController.navigate(Screen.Feed.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onCancel = { authViewModel.logout() },
            )
        }
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
                FeedScreen(
                    onMenuOpen = onMenuOpen,
                    onCreateStory = { navController.navigate(Screen.CreateStory.route) },
                    onOpenStory = { id -> navController.navigate(Screen.StoryViewer.createRoute(id)) },
                    onOpenPost = { id -> navController.navigate(Screen.PostDetail.createRoute(id)) },
                    onOpenAuthor = { username -> navController.navigate(Screen.UserProfile.createRoute(username)) },
                )
            }
        }
        composable(Screen.CreateStory.route) {
            CreateStoryScreen(onClose = { navController.popBackStack() }, authViewModel = authViewModel)
        }
        composable(
            route = Screen.StoryViewer.route,
            arguments = listOf(navArgument("storyId") { type = NavType.StringType }),
        ) { backStackEntry ->
            val storyId = backStackEntry.arguments?.getString("storyId") ?: ""
            StoryViewerScreen(
                initialStoryId = storyId,
                onClose = { navController.popBackStack() },
            )
        }
        composable(
            route = Screen.PostDetail.route,
            arguments = listOf(navArgument("postId") { type = NavType.StringType }),
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("postId") ?: ""
            PostDetailScreen(
                postId = id,
                onBack = { navController.popBackStack() },
                onOpenAuthor = { username -> navController.navigate(Screen.UserProfile.createRoute(username)) },
            )
        }
        composable(
            route = Screen.UserProfile.route,
            arguments = listOf(navArgument("username") { type = NavType.StringType }),
        ) { backStackEntry ->
            val username = backStackEntry.arguments?.getString("username") ?: ""
            UserProfileScreen(
                username = username,
                onBack = { navController.popBackStack() },
                onOpenFollowers = { u -> navController.navigate(Screen.FollowerList.createRoute(u, "followers")) },
                onOpenFollowing = { u -> navController.navigate(Screen.FollowerList.createRoute(u, "following")) },
            )
        }
        composable(
            route = Screen.FollowerList.route,
            arguments = listOf(
                navArgument("username") { type = NavType.StringType },
                navArgument("kind") { type = NavType.StringType },
            ),
        ) { backStackEntry ->
            val username = backStackEntry.arguments?.getString("username") ?: ""
            val kindRaw = backStackEntry.arguments?.getString("kind") ?: "followers"
            val kind = if (kindRaw == "following") UserListKind.FOLLOWING else UserListKind.FOLLOWERS
            UserListScreen(
                username = username,
                kind = kind,
                onBack = { navController.popBackStack() },
                onOpenUser = { u -> navController.navigate(Screen.UserProfile.createRoute(u)) },
            )
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
                MessagesScreen(
                    onMenuOpen = onMenuOpen,
                    onOpenConversation = { id -> navController.navigate(Screen.Chat.createRoute(id)) },
                )
            }
        }
        composable(
            route = Screen.Chat.route,
            arguments = listOf(navArgument("conversationId") { type = NavType.StringType }),
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("conversationId") ?: ""
            ChatScreen(
                conversationId = id,
                onBack = { navController.popBackStack() },
                onOpenAuthor = { u -> navController.navigate(Screen.UserProfile.createRoute(u)) },
            )
        }
        composable(Screen.Communities.route) {
            MainShell(navController = navController, authViewModel = authViewModel) { onMenuOpen ->
                CommunitiesScreen(onMenuOpen = onMenuOpen)
            }
        }
        composable(Screen.Marketplace.route) {
            MainShell(navController = navController, authViewModel = authViewModel) { onMenuOpen ->
                MarketplaceScreen(
                    onMenuOpen = onMenuOpen,
                    onOpenItem = { id -> navController.navigate(Screen.MarketplaceItem.createRoute(id)) },
                )
            }
        }
        composable(
            route = Screen.MarketplaceItem.route,
            arguments = listOf(navArgument("itemId") { type = NavType.StringType }),
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("itemId") ?: ""
            MarketplaceItemScreen(
                itemId = id,
                onBack = { navController.popBackStack() },
                onOpenSeller = { u -> navController.navigate(Screen.UserProfile.createRoute(u)) },
            )
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
                ProfileScreen(
                    authViewModel = authViewModel,
                    onMenuOpen = onMenuOpen,
                    onOpenCircles = { navController.navigate(Screen.Circles.route) },
                    onOpenFollowers = { u -> navController.navigate(Screen.FollowerList.createRoute(u, "followers")) },
                    onOpenFollowing = { u -> navController.navigate(Screen.FollowerList.createRoute(u, "following")) },
                )
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
                CoursesScreen(
                    onMenuOpen = onMenuOpen,
                    onOpenCourse = { code -> navController.navigate(Screen.CourseDetail.createRoute(code)) },
                )
            }
        }
        composable(
            route = Screen.CourseDetail.route,
            arguments = listOf(navArgument("courseCode") { type = NavType.StringType }),
        ) { backStackEntry ->
            val code = backStackEntry.arguments?.getString("courseCode") ?: ""
            CourseDetailScreen(courseCode = code, onBack = { navController.popBackStack() })
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
        composable(Screen.Circles.route) {
            CirclesScreen(
                onBack = { navController.popBackStack() },
                onOpenCircle = { id -> navController.navigate(Screen.CircleDetail.createRoute(id)) },
                onCreateCircle = { navController.navigate(Screen.CreateCircle.route) },
            )
        }
        composable(Screen.CreateCircle.route) {
            CreateCircleScreen(onClose = { navController.popBackStack() })
        }
        composable(
            route = Screen.CircleDetail.route,
            arguments = listOf(navArgument("circleId") { type = NavType.StringType }),
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("circleId") ?: ""
            CircleDetailScreen(circleId = id, onBack = { navController.popBackStack() })
        }
    }
}
