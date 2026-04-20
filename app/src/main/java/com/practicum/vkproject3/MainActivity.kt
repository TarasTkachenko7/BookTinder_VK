package com.practicum.vkproject3

import android.net.Uri // <-- ДОБАВИЛИ ИМПОРТ ДЛЯ КОДИРОВАНИЯ
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.practicum.vkproject3.presentation.discussions.ChatScreen
import com.practicum.vkproject3.presentation.discussions.CreateReviewScreen
import com.practicum.vkproject3.presentation.discussions.DiscussionsScreen
import com.practicum.vkproject3.presentation.discussions.DiscussionsViewModel
import com.practicum.vkproject3.ui.auth.ForgotPasswordScreen
import com.practicum.vkproject3.ui.auth.LoginScreen
import com.practicum.vkproject3.ui.auth.RegistrationScreen
import com.practicum.vkproject3.ui.auth.VerificationScreen
import com.practicum.vkproject3.ui.books.BookDetailsScreen
import com.practicum.vkproject3.ui.books.CatalogScreen
import com.practicum.vkproject3.ui.books.FavoritesScreen
import com.practicum.vkproject3.ui.genres.GenrePickScreen
import com.practicum.vkproject3.ui.home.HomeScreen
import com.practicum.vkproject3.ui.onboarding.OnboardingScreen
import com.practicum.vkproject3.ui.profile.EditProfileScreen
import com.practicum.vkproject3.ui.profile.HistoryScreen
import com.practicum.vkproject3.ui.profile.PlaceholderScreen
import com.practicum.vkproject3.ui.profile.ProfileScreen
import com.practicum.vkproject3.ui.theme.BeigeBackground
import com.practicum.vkproject3.ui.theme.MainBrown
import com.practicum.vkproject3.ui.theme.VkProject3Theme
import org.koin.androidx.compose.koinViewModel

sealed class BottomNavItem(val route: String, val title: String, val selectedIcon: ImageVector, val unselectedIcon: ImageVector) {
    object Books : BottomNavItem("books_screen", "Лента", Icons.Filled.Home, Icons.Outlined.Home)
    object Discussions : BottomNavItem("discussions", "Обсуждения", Icons.Filled.ChatBubble, Icons.Outlined.ChatBubbleOutline)
    object Catalog : BottomNavItem("catalog", "Каталог", Icons.AutoMirrored.Filled.MenuBook, Icons.AutoMirrored.Outlined.MenuBook)
    object Profile : BottomNavItem("profile_screen", "Профиль", Icons.Filled.Person, Icons.Outlined.Person)
}

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val firebase : DatabaseReference = FirebaseDatabase.getInstance().getReference()
        val auth = FirebaseAuth.getInstance()
        setContent {
            VkProject3Theme {
                val rootNavController = rememberNavController()
                val startDestination = if (auth.currentUser != null) "main_app" else "login"

                NavHost(navController = rootNavController, startDestination = startDestination) {
                    composable("login") {
                        LoginScreen(
                            onNavigateToRegistration = {
                                rootNavController.navigate("registration")
                            },
                            onLoginSuccess = {
                                rootNavController.navigate("main_app") {
                                    popUpTo("login") { inclusive = true }
                                }
                            },
                            onNavigateToForgot = {
                                rootNavController.navigate("forgot_password")
                            }
                        )
                    }

                    composable("registration") {
                        RegistrationScreen(
                            onNavigateToVerification = { email ->
                                rootNavController.navigate("registration_verification/$email")
                            },
                            onNavigateToLogin = {
                                rootNavController.popBackStack()
                            }
                        )
                    }

                    composable(
                        route = "registration_verification/{email}",
                        arguments = listOf(
                            navArgument("email") { type = NavType.StringType }
                        )
                    ) { backStackEntry ->
                        val email = backStackEntry.arguments?.getString("email") ?: ""
                        VerificationScreen(
                            email = email,
                            onBack = { rootNavController.popBackStack() },
                            onSuccess = {
                                rootNavController.navigate("onboarding") {
                                    popUpTo("login") { inclusive = true }
                                }
                            }
                        )
                    }

                    composable("onboarding") {
                        OnboardingScreen(
                            onFinishOnboarding = {
                                rootNavController.navigate("genre_pick") {
                                    popUpTo("onboarding") { inclusive = true }
                                }
                            }
                        )
                    }

                    composable("forgot_password") {
                        ForgotPasswordScreen(
                            onBack = { rootNavController.popBackStack() },
                            onSuccessReset = {
                                rootNavController.popBackStack("login", inclusive = false)
                            }
                        )
                    }

                    composable("genre_pick") {
                        GenrePickScreen(
                            onDone = {
                                rootNavController.navigate("main_app") {
                                    popUpTo("genre_pick") { inclusive = true }
                                }
                            }
                        )
                    }

                    composable("main_app") {
                        MainFlowScreen(onLogout = {
                            rootNavController.navigate("login") {
                                popUpTo("main_app") { inclusive = true }
                            }
                        })
                    }
                }
            }
        }
    }
}

@Composable
fun MainFlowScreen(onLogout: () -> Unit) {
    val navController = rememberNavController()
    val discussionsViewModel: DiscussionsViewModel = koinViewModel()

    val items = listOf(
        BottomNavItem.Books,
        BottomNavItem.Discussions,
        BottomNavItem.Catalog,
        BottomNavItem.Profile
    )

    Scaffold(
        containerColor = BeigeBackground,
        bottomBar = {
            Surface(
                shadowElevation = 0.dp,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                color = Color.White
            ) {
                NavigationBar(
                    modifier = Modifier.fillMaxWidth(),
                    containerColor = Color.White,
                    tonalElevation = 0.dp,
                    windowInsets = WindowInsets(0.dp, 0.dp, 0.dp, 0.dp)
                ) {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination

                    items.forEach { screen ->
                        val currentRoute = currentDestination?.route
                        val isSelected = when (screen) {
                            BottomNavItem.Books -> currentRoute == BottomNavItem.Books.route
                            BottomNavItem.Discussions -> currentRoute == BottomNavItem.Discussions.route
                            BottomNavItem.Catalog -> currentRoute == BottomNavItem.Catalog.route || currentRoute == "favorites_screen" || currentRoute?.startsWith("book_details") == true
                            BottomNavItem.Profile -> currentRoute == BottomNavItem.Profile.route || currentRoute == "edit_profile" || currentRoute == "history_screen" || currentRoute == "settings_screen" || currentRoute == "subscription_screen" || currentRoute == "profile_favorite_books"
                        }

                        NavigationBarItem(
                            icon = { Icon(if (isSelected) screen.selectedIcon else screen.unselectedIcon, null, Modifier.size(26.dp)) },
                            selected = isSelected,
                            alwaysShowLabel = false,
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MainBrown,
                                unselectedIconColor = Color.Gray.copy(alpha = 0.6f),
                                selectedTextColor = MainBrown,
                                unselectedTextColor = Color.Gray.copy(alpha = 0.6f),
                                indicatorColor = MainBrown.copy(alpha = 0.1f)
                            ),
                            onClick = {
                                if (isSelected) {
                                    navController.popBackStack(screen.route, inclusive = false)
                                } else {
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Books.route,
            modifier = Modifier
        ) {
            composable(BottomNavItem.Books.route) {
                HomeScreen(
                    onFavoritesClick = {
                        navController.navigate("favorites_screen")
                    }
                )
            }

            composable(BottomNavItem.Catalog.route) {
                CatalogScreen(
                    onNavigateToFavorites = {
                        navController.navigate("favorites_screen")
                    },
                    onBookClick = { bookId ->
                        navController.navigate("book_details/${Uri.encode(bookId)}")
                    },
                    onNavigateToGenre = { genre ->
                        navController.navigate("genre_details/$genre")
                    }
                )
            }

            composable(
                route = "genre_details/{genre}",
                arguments = listOf(navArgument("genre") { type = NavType.StringType })
            ) { backStackEntry ->
                val genre = backStackEntry.arguments?.getString("genre") ?: ""
                com.practicum.vkproject3.ui.books.GenreDetailsScreen(
                    genre = genre,
                    onBack = { navController.popBackStack() },
                    onBookClick = { bookId -> navController.navigate("book_details/${Uri.encode(bookId)}") }
                )
            }

            composable(BottomNavItem.Profile.route) {
                ProfileScreen(
                    onNavigateToEdit = { navController.navigate("edit_profile") },
                    onNavigateToHistory = { navController.navigate("history_screen") },
                    onNavigateToSettings = { navController.navigate("settings_screen") },
                    onNavigateToSubscription = { navController.navigate("subscription_screen") },
                    onNavigateToFavoriteBooks = { navController.navigate("profile_favorite_books") },
                    onLogout = onLogout
                )
            }

            composable("edit_profile") {
                EditProfileScreen(
                    onBack = { navController.popBackStack() },
                    onLogout = onLogout
                )
            }

            composable("history_screen") {
                HistoryScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            composable("settings_screen") {
                PlaceholderScreen(title = "Настройки", onBack = { navController.popBackStack() })
            }

            composable("subscription_screen") {
                PlaceholderScreen(title = "Подписка", onBack = { navController.popBackStack() })
            }

            composable("favorites_screen") {
                FavoritesScreen(
                    onBack = { navController.popBackStack() },
                    // ИСПРАВЛЕНИЕ: Кодируем bookId
                    onBookClick = { bookId ->
                        navController.navigate("book_details/${Uri.encode(bookId)}")
                    }
                )
            }

            composable("profile_favorite_books") {
                PlaceholderScreen(
                    title = "Любимые книги",
                    onBack = { navController.popBackStack() }
                )
            }

            composable(BottomNavItem.Discussions.route) {
                DiscussionsScreen(
                    navController = navController,
                    viewModel = discussionsViewModel,
                    onNavigateToFavorites = {
                        navController.navigate("favorites_screen")
                    },
                    onAddReviewClick = {
                        discussionsViewModel.resetCreateReviewState()
                        navController.navigate("create_review")
                    }
                )
            }

            composable(
                route = "book_details/{bookId}",
                arguments = listOf(
                    navArgument("bookId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val rawBookId = backStackEntry.arguments?.getString("bookId") ?: ""
                val bookId = Uri.decode(rawBookId)

                BookDetailsScreen(
                    bookId = bookId,
                    onBack = { navController.popBackStack() },
                    onAddReviewClick = { selectedBook ->
                        discussionsViewModel.setSelectedBookForReview(
                            id = selectedBook.id,
                            title = selectedBook.title,
                            author = selectedBook.author,
                            coverUrl = selectedBook.imageUrl,
                            rating = if (selectedBook.rating > 0) {
                                selectedBook.rating.toFloat()
                            } else {
                                4.0f
                            }
                        )
                        navController.navigate("create_review/${Uri.encode(selectedBook.id)}")
                    }
                )
            }

            composable("create_review") {
                CreateReviewScreen(
                    bookId = null,
                    viewModel = discussionsViewModel,
                    onBackClick = { navController.popBackStack() },
                    onPublishSuccess = { navController.popBackStack() }
                )
            }

            composable(
                route = "create_review/{bookId}",
                arguments = listOf(
                    navArgument("bookId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val rawBookId = backStackEntry.arguments?.getString("bookId") ?: ""
                val bookId = Uri.decode(rawBookId)

                CreateReviewScreen(
                    bookId = bookId,
                    viewModel = discussionsViewModel,
                    onBackClick = { navController.popBackStack() },
                    onPublishSuccess = { navController.popBackStack() }
                )
            }

            composable(
                route = "discussion_chat/{discussionId}",
                arguments = listOf(navArgument("discussionId") { type = NavType.IntType })
            ) { backStackEntry ->
                val discussionId = backStackEntry.arguments?.getInt("discussionId") ?: 0

                ChatScreen(
                    discussionId = discussionId,
                    viewModel = discussionsViewModel,
                    onBackClick = { navController.popBackStack() },
                    onAddReviewClick = {
                        discussionsViewModel.resetCreateReviewState()
                        navController.navigate("create_review")
                    }
                )
            }
        }
    }
}