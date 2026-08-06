package com.example.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.data.db.ClickitDatabase
import com.example.data.model.AppTheme
import com.example.data.preferences.UserPreferencesRepository
import com.example.ui.screens.AboutScreen
import com.example.ui.screens.CameraScreen
import com.example.ui.screens.GalleryScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.PhotoDetailScreen
import com.example.ui.screens.PhotoEditorScreen
import com.example.ui.screens.PresetsScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.ClickitTheme
import kotlinx.coroutines.launch

object Routes {
    const val ONBOARDING = "onboarding"
    const val CAMERA = "camera"
    const val GALLERY = "gallery"
    const val PHOTO_DETAIL = "photo_detail/{photoId}"
    const val PHOTO_EDITOR = "photo_editor/{photoId}"
    const val PRESETS = "presets"
    const val SETTINGS = "settings"
    const val ABOUT = "about"
}

@Composable
fun ClickitNavGraph() {
    val context = LocalContext.current
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    val database = remember { ClickitDatabase.getDatabase(context) }
    val photoDao = remember { database.photoDao() }
    val presetDao = remember { database.presetDao() }
    val userPrefs = remember { UserPreferencesRepository(context) }

    val appThemeStr by userPrefs.appTheme.collectAsStateWithLifecycle(initialValue = "DARK")
    val onboardingCompleted by userPrefs.onboardingCompleted.collectAsStateWithLifecycle(initialValue = false)

    val currentTheme = try {
        AppTheme.valueOf(appThemeStr)
    } catch (e: Exception) {
        AppTheme.DARK
    }

    val startDestination = if (onboardingCompleted) Routes.CAMERA else Routes.ONBOARDING

    ClickitTheme(themeOption = currentTheme) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            gesturesEnabled = drawerState.isOpen,
            drawerContent = {
                ModalDrawerSheet(
                    drawerContainerColor = MaterialTheme.colorScheme.surface,
                    drawerContentColor = MaterialTheme.colorScheme.onSurface
                ) {
                    Column(
                        modifier = Modifier
                            .width(280.dp)
                            .fillMaxHeight()
                            .padding(24.dp)
                    ) {
                        Text("Clickit", color = MaterialTheme.colorScheme.onSurface, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
                        Text("Premium Film Camera", color = MaterialTheme.colorScheme.primary, fontSize = 12.sp, fontWeight = FontWeight.Bold)

                        Spacer(modifier = Modifier.height(32.dp))

                        DrawerItem("Camera", Icons.Default.CameraAlt) {
                            coroutineScope.launch { drawerState.close() }
                            navController.navigate(Routes.CAMERA)
                        }

                        DrawerItem("Gallery", Icons.Default.PhotoLibrary) {
                            coroutineScope.launch { drawerState.close() }
                            navController.navigate(Routes.GALLERY)
                        }

                        DrawerItem("Presets", Icons.Default.Tune) {
                            coroutineScope.launch { drawerState.close() }
                            navController.navigate(Routes.PRESETS)
                        }

                        DrawerItem("Settings", Icons.Default.Settings) {
                            coroutineScope.launch { drawerState.close() }
                            navController.navigate(Routes.SETTINGS)
                        }

                        DrawerItem("About", Icons.Default.Info) {
                            coroutineScope.launch { drawerState.close() }
                            navController.navigate(Routes.ABOUT)
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        Text("Made with ❤️ by Rahul Shah", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
                    }
                }
            }
        ) {
            NavHost(navController = navController, startDestination = startDestination) {
                composable(Routes.ONBOARDING) {
                    OnboardingScreen(
                        onFinishOnboarding = {
                            coroutineScope.launch { userPrefs.setOnboardingCompleted(true) }
                            navController.navigate(Routes.CAMERA) {
                                popUpTo(Routes.ONBOARDING) { inclusive = true }
                            }
                        }
                    )
                }

                composable(Routes.CAMERA) {
                    CameraScreen(
                        photoDao = photoDao,
                        onOpenDrawer = { coroutineScope.launch { drawerState.open() } },
                        onOpenGallery = { navController.navigate(Routes.GALLERY) },
                        onOpenSettings = { navController.navigate(Routes.SETTINGS) }
                    )
                }

                composable(Routes.GALLERY) {
                    GalleryScreen(
                        photoDao = photoDao,
                        onPhotoSelected = { id -> navController.navigate("photo_detail/$id") },
                        onBackToCamera = { navController.popBackStack() }
                    )
                }

                composable(
                    route = Routes.PHOTO_DETAIL,
                    arguments = listOf(navArgument("photoId") { type = NavType.LongType })
                ) { backStackEntry ->
                    val photoId = backStackEntry.arguments?.getLong("photoId") ?: 0L
                    PhotoDetailScreen(
                        photoId = photoId,
                        photoDao = photoDao,
                        onEditPhoto = { id -> navController.navigate("photo_editor/$id") },
                        onBack = { navController.popBackStack() }
                    )
                }

                composable(
                    route = Routes.PHOTO_EDITOR,
                    arguments = listOf(navArgument("photoId") { type = NavType.LongType })
                ) { backStackEntry ->
                    val photoId = backStackEntry.arguments?.getLong("photoId") ?: 0L
                    PhotoEditorScreen(
                        photoId = photoId,
                        photoDao = photoDao,
                        onBack = { navController.popBackStack() }
                    )
                }

                composable(Routes.PRESETS) {
                    PresetsScreen(
                        presetDao = presetDao,
                        onBack = { navController.popBackStack() }
                    )
                }

                composable(Routes.SETTINGS) {
                    SettingsScreen(
                        userPreferencesRepository = userPrefs,
                        onOpenAbout = { navController.navigate(Routes.ABOUT) },
                        onBack = { navController.popBackStack() }
                    )
                }

                composable(Routes.ABOUT) {
                    AboutScreen(
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}

@Composable
private fun DrawerItem(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = label, tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Text(label, color = MaterialTheme.colorScheme.onSurface, fontSize = 15.sp, fontWeight = FontWeight.Medium)
    }
}
