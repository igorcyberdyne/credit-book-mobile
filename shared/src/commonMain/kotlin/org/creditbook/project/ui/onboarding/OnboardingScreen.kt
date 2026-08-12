package org.creditbook.project.ui.onboarding

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.benasher44.uuid.uuid4
import org.creditbook.project.ui.main.MainScreen
import org.koin.compose.viewmodel.koinViewModel

object OnboardingScreen : Screen {
    private val screenKey = uuid4().toString()

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = koinViewModel<OnboardingViewModel>(key = screenKey)
        val state by viewModel.state.collectAsState()

        LaunchedEffect(state.isCompleted) {
            if (state.isCompleted) {
                navigator.replaceAll(MainScreen)
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(if (state.step == OnboardingStep.SHOP) "Votre commerce" else "Votre compte") },
                    navigationIcon = {
                        IconButton(onClick = {
                            if (state.step == OnboardingStep.USER) {
                                viewModel.goBackToShopStep()
                            } else {
                                navigator.pop()
                            }
                        }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
                        }
                    }
                )
            }
        ) { padding ->
            when (state.step) {
                OnboardingStep.SHOP -> OnboardingShopStepContent(
                    state = state,
                    modifier = androidx.compose.ui.Modifier.padding(padding),
                    onShopNameChange = viewModel::onShopNameChange,
                    onAddressChange = viewModel::onAddressChange,
                    onPostalCodeChange = viewModel::onPostalCodeChange,
                    onCityChange = viewModel::onCityChange,
                    onCountryChange = viewModel::onCountryChange,
                    onShopPhoneChange = viewModel::onShopPhoneChange,
                    onCurrencyChange = viewModel::onCurrencyChange,
                    onNext = viewModel::goToUserStep
                )

                OnboardingStep.USER -> OnboardingUserStepContent(
                    state = state,
                    modifier = androidx.compose.ui.Modifier.padding(padding),
                    onFirstnameChange = viewModel::onFirstnameChange,
                    onLastnameChange = viewModel::onLastnameChange,
                    onEmailChange = viewModel::onEmailChange,
                    onPhoneChange = viewModel::onPhoneChange,
                    onPasswordChange = viewModel::onPasswordChange,
                    onPasswordConfirmChange = viewModel::onPasswordConfirmChange,
                    onSubmit = viewModel::submit
                )
            }
        }
    }
}