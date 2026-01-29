package com.guerramath.safetyapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.guerramath.safetyapp.data.api.RetrofitInstance
import com.guerramath.safetyapp.data.local.SafetyDatabase
import com.guerramath.safetyapp.data.preferences.PreferencesManager
import com.guerramath.safetyapp.data.repository.SafetyRepository
import com.guerramath.safetyapp.presentation.evaluation.ChecklistScreen
import com.guerramath.safetyapp.presentation.evaluation.ChecklistViewModel
import com.guerramath.safetyapp.presentation.evaluation.ChecklistViewModelFactory
import com.guerramath.safetyapp.presentation.history.HistoryScreen
import com.guerramath.safetyapp.presentation.history.HistoryViewModel
import com.guerramath.safetyapp.presentation.history.HistoryViewModelFactory
import com.guerramath.safetyapp.presentation.onboarding.OnboardingScreen
import com.guerramath.safetyapp.ui.theme.SafetyAppTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var checklistViewModel: ChecklistViewModel
    private lateinit var historyViewModel: HistoryViewModel
    private lateinit var preferencesManager: PreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Setup Preferences
        preferencesManager = PreferencesManager(applicationContext)

        // Setup Repository
        val database = SafetyDatabase.getDatabase(applicationContext)
        val repository = SafetyRepository(
            api = RetrofitInstance.api,
            dao = database.safetyDao()
        )

        // Setup ViewModels
        checklistViewModel = ViewModelProvider(
            this,
            ChecklistViewModelFactory(repository)
        )[ChecklistViewModel::class.java]

        historyViewModel = ViewModelProvider(
            this,
            HistoryViewModelFactory(repository)
        )[HistoryViewModel::class.java]

        setContent {
            SafetyAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SafetyAppNavigation(
                        checklistViewModel = checklistViewModel,
                        historyViewModel = historyViewModel,
                        preferencesManager = preferencesManager
                    )
                }
            }
        }
    }
}

@Composable
fun SafetyAppNavigation(
    checklistViewModel: ChecklistViewModel,
    historyViewModel: HistoryViewModel,
    preferencesManager: PreferencesManager
) {
    val navController = rememberNavController()
    val isOnboardingCompleted by preferencesManager.isOnboardingCompleted.collectAsState(initial = null)

    // Determine start destination
    val startDestination = when (isOnboardingCompleted) {
        true -> "checklist"
        false -> "onboarding"
        null -> "checklist" // Fallback
    }

    if (isOnboardingCompleted != null) {
        NavHost(
            navController = navController,
            startDestination = startDestination
        ) {
            composable("onboarding") {
                OnboardingScreen(
                    onFinish = {
                        kotlinx.coroutines.MainScope().launch {
                            preferencesManager.setOnboardingCompleted(true)
                            navController.navigate("checklist") {
                                popUpTo("onboarding") { inclusive = true }
                            }
                        }
                    }
                )
            }

            composable("checklist") {
                ChecklistScreen(
                    viewModel = checklistViewModel,
                    onNavigateToHistory = {
                        navController.navigate("history")
                    }
                )
            }

            composable("history") {
                HistoryScreen(
                    viewModel = historyViewModel,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}