package org.creditbook.project.ui.main

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.creditbook.project.data.repository.AuthRepository
import org.creditbook.project.di.AuthEvents
import org.koin.compose.koinInject

@Composable
fun ProfileScreen() {
    val authRepository = koinInject<AuthRepository>()
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Profil") }) }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    scope.launch {
                        authRepository.logout()
                        AuthEvents.emitUnauthorized() // redirige vers LoginScreen via l'observateur racine
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Se déconnecter")
            }
        }
    }
}