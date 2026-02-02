package com.guerramath.safetyapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.guerramath.safetyapp.auth.data.preferences.AuthPreferences
import com.guerramath.safetyapp.data.api.RetrofitInstance
import com.guerramath.safetyapp.data.api.SafetyApiService
import com.guerramath.safetyapp.data.local.SafetyDatabase
import com.guerramath.safetyapp.data.repository.SafetyRepository
import com.guerramath.safetyapp.presentation.custom.CreateChecklistScreen
import com.guerramath.safetyapp.presentation.custom.CustomChecklistViewModel
import com.guerramath.safetyapp.presentation.custom.CustomChecklistViewModelFactory
import com.guerramath.safetyapp.presentation.custom.MyChecklistsScreen
import com.guerramath.safetyapp.presentation.evaluation.ChecklistScreen
import com.guerramath.safetyapp.presentation.evaluation.ChecklistViewModel
import com.guerramath.safetyapp.presentation.evaluation.ChecklistViewModelFactory
import com.guerramath.safetyapp.presentation.history.HistoryScreenEnhanced
import com.guerramath.safetyapp.presentation.history.HistoryViewModel
import com.guerramath.safetyapp.presentation.history.HistoryViewModelFactory
import com.guerramath.safetyapp.presentation.navigation.DrawerContent
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToLogin: () -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Preferências de autenticação
    val authPreferences = remember { AuthPreferences(context) }
    val isLoggedIn by authPreferences.isLoggedIn.collectAsState(initial = false)
    val userName by authPreferences.userName.collectAsState(initial = null)
    val userEmail by authPreferences.userEmail.collectAsState(initial = null)

    // Database e dependências
    val database = remember { SafetyDatabase.getDatabase(context) }
    val safetyDao = remember { database.safetyDao() }
    val customChecklistDao = remember { database.customChecklistDao() }
    val apiService = remember { RetrofitInstance.retrofit.create(SafetyApiService::class.java) }
    val repository = remember { SafetyRepository(apiService, safetyDao) }

    // ViewModels
    val checklistViewModel: ChecklistViewModel = viewModel(
        factory = ChecklistViewModelFactory(repository)
    )
    val historyViewModel: HistoryViewModel = viewModel(
        factory = HistoryViewModelFactory(repository)
    )
    val customChecklistViewModel: CustomChecklistViewModel = viewModel(
        factory = CustomChecklistViewModelFactory(customChecklistDao)
    )

    // Navegação
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route ?: "checklist"

    // Estado do Drawer
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = Color.Transparent,
                modifier = Modifier.width(300.dp)
            ) {
                DrawerContent(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo("checklist") { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onCloseDrawer = {
                        scope.launch { drawerState.close() }
                    },
                    isLoggedIn = isLoggedIn,
                    userName = userName,
                    userEmail = userEmail,
                    onLoginClick = {
                        onNavigateToLogin()
                    },
                    onLogout = {
                        scope.launch {
                            authPreferences.clearAuthData()
                            onLogout()
                        }
                    }
                )
            }
        }
    ) {
        NavHost(
            navController = navController,
            startDestination = "checklist"
        ) {
            // Dashboard SMS (ChecklistScreen)
            composable("checklist") {
                ChecklistScreen(
                    viewModel = checklistViewModel,
                    onOpenDrawer = {
                        scope.launch { drawerState.open() }
                    },
                    onNavigateToHistory = {
                        navController.navigate("history")
                    }
                )
            }

            // Meus Checklists
            composable("my_checklists") {
                MyChecklistsScreen(
                    viewModel = customChecklistViewModel,
                    onNavigateBack = {
                        scope.launch { drawerState.open() }
                    },
                    onNavigateToCreate = {
                        navController.navigate("create_checklist")
                    },
                    onNavigateToEdit = { checklistId ->
                        customChecklistViewModel.loadChecklist(checklistId)
                        navController.navigate("create_checklist")
                    }
                )
            }

            // Criar/Editar Checklist
            composable("create_checklist") {
                CreateChecklistScreen(
                    viewModel = customChecklistViewModel,
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onNavigateToList = {
                        navController.navigate("my_checklists") {
                            popUpTo("my_checklists") { inclusive = true }
                        }
                    }
                )
            }

            // Histórico
            composable("history") {
                HistoryScreenEnhanced(
                    viewModel = historyViewModel,
                    onNavigateBack = {
                        scope.launch { drawerState.open() }
                    }
                )
            }

            // Configurações
            composable("settings") {
                SettingsScreen(
                    onNavigateBack = {
                        scope.launch { drawerState.open() }
                    },
                    onLogout = {
                        scope.launch {
                            authPreferences.clearAuthData()
                            onLogout()
                        }
                    }
                )
            }
        }
    }
}
