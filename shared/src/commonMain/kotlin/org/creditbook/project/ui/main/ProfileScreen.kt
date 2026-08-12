package org.creditbook.project.ui.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.creditbook.project.data.repository.AuthRepository
import org.creditbook.project.di.AuthEvents
import org.creditbook.project.model.CurrentCurrency
import org.koin.compose.koinInject

@Composable
fun ProfileScreen() {
    val authRepository = koinInject<AuthRepository>()
    val scope = rememberCoroutineScope()
    val session = remember { authRepository.getCachedSession() }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Votre profil") }) }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
        ) {
            session.let {
                Text("Nom complet : " + it.user.displayName)
                Text("Email : " + it.user.email)
                Text("Role : " + it.user.roleName)
                Text("Phone : " + it.user.phone?.isEmpty().let { "---" })

                Spacer(modifier = Modifier.height(12.dp))

                Text("Enseigne : " + it.shop.name)
                Text("Phone : " + it.shop.phone?.isEmpty().let { "---" })
                Text("Adresse : " + it.shop.displayAddress.isEmpty().let { "---" })
                Text("Pays : " + it.shop.country?.isEmpty().let { "---" })
                Text("Devise : " + CurrentCurrency.value.symbol)
            }

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