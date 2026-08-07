package org.creditbook.project.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator
import org.creditbook.project.data.local.SessionDatabase
import org.creditbook.project.data.repository.AuthRepository
import org.creditbook.project.data.repository.TransactionRepository
import org.creditbook.project.di.AuthEvents
import org.creditbook.project.sync.ConnectivityObserver
import org.creditbook.project.ui.auth.LoginScreen
import org.creditbook.project.ui.common.error.ErrorDialog
import org.creditbook.project.ui.main.MainScreen
import org.koin.compose.koinInject

@Composable
fun AppEntryPoint() {
    val authRepository = koinInject<AuthRepository>()
    val connectivityObserver = koinInject<ConnectivityObserver>()
    val transactionRepository = koinInject<TransactionRepository>()
    val sessionDatabase = koinInject<SessionDatabase>()

    var isCheckingAuth by remember { mutableStateOf(true) }
    var isLoggedIn by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isLoggedIn = authRepository.isLoggedIn()
        isCheckingAuth = false
    }

    // Démarre l'observation réseau dès le lancement de l'app, en continu
    LaunchedEffect(Unit) {
        connectivityObserver.observe().collect { isOnline ->
            if (isOnline && sessionDatabase.hasSession()) {
                transactionRepository.syncPendingTransactions()
            }
        }
    }

    if (isCheckingAuth) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        val startScreen = if (isLoggedIn) MainScreen else LoginScreen

        Navigator(startScreen) { navigator ->
            // Empêche Voyager de restaurer une pile de navigation d'une session précédente
            // (ex. la fiche du dernier client consulté) au profit de l'écran de départ voulu.
            LaunchedEffect(Unit) {
                if (navigator.lastItem != startScreen) {
                    navigator.replaceAll(startScreen)
                }
            }

            LaunchedEffect(Unit) {
                AuthEvents.onUnauthorized.collect {
                    navigator.replaceAll(LoginScreen)
                }
            }

            CurrentScreen()

            ErrorDialog() // affiché par-dessus le Navigator, visible depuis n'importe quel écran
        }
    }
}