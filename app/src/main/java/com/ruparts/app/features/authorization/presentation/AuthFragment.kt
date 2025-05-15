package com.ruparts.app.features.authorization.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.fragment.findNavController
import com.ruparts.app.R
import com.ruparts.app.features.authorization.presentation.model.AuthUiAction
import com.ruparts.app.features.authorization.presentation.model.AuthUiEffect
import com.ruparts.app.features.authorization.presentation.model.AuthUiState
import com.ruparts.app.ui.theme.RupartsTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AuthFragment : Fragment() {

    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                RupartsTheme {
                    val state = viewModel.uiState.collectAsStateWithLifecycle().value
                    val snackbarHostState = remember { SnackbarHostState() }
                    LaunchedEffect(Unit) {
                        var snackBarJob: Job? = null
                        viewModel.uiEffect.collect { effect ->
                            when (effect) {
                                is AuthUiEffect.NavigateToMenu -> {
                                    findNavController().navigate(R.id.action_authFragment_to_menuFragment)
                                }

                                is AuthUiEffect.ShowError -> {
                                    snackBarJob?.cancel()
                                    snackBarJob = launch {
                                        snackbarHostState.showSnackbar(
                                            message = getString(R.string.auth_error_message),
                                        )
                                    }
                                }
                            }
                        }
                    }
                    AuthScreen(
                        snackbarHostState = snackbarHostState,
                        state = state,
                        onDigitPressed = { digit ->
                            viewModel.handleAction(AuthUiAction.DigitPressed(digit))
                        },
                        onClearPressed = { viewModel.handleAction(AuthUiAction.ClearPin) },
                        onDeletePressed = { viewModel.handleAction(AuthUiAction.DeleteLastDigit) }
                    )
                }
            }
        }
    }
}

@Composable
private fun AuthScreen(
    snackbarHostState: SnackbarHostState,
    state: AuthUiState,
    onDigitPressed: (Int) -> Unit,
    onClearPressed: () -> Unit,
    onDeletePressed: () -> Unit
) {
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
    ) { contentPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.padding(top = 48.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.auth_required_title),
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(bottom = 32.dp)
                    )
                    PinCodeDisplay(pinCode = state.pinCode)
                }
                NumericKeyboard(
                    onDigitPressed = onDigitPressed,
                    onClearPressed = onClearPressed,
                    onDeletePressed = onDeletePressed,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
            }
        }
    }
}

@Composable
private fun PinCodeDisplay(pinCode: String) {
    Row(
        modifier = Modifier.padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        repeat(6) { index ->
            PinDigitBox(
                isFilled = index < pinCode.length,
                digit = if (index < pinCode.length) pinCode[index] else null,
            )
        }
    }
}

@Composable
private fun PinDigitBox(isFilled: Boolean, digit: Char?) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(8.dp)
            )
            .background(
                color = if (isFilled) MaterialTheme.colorScheme.primaryContainer
                else MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(8.dp)
            ), contentAlignment = Alignment.Center
    ) {
        if (isFilled && digit != null) {
            Text(
                text = digit.toString(),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
private fun NumericKeyboard(
    onDigitPressed: (Int) -> Unit,
    onClearPressed: () -> Unit,
    onDeletePressed: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        KeyboardRow(digits = listOf(1, 2, 3), onDigitPressed = onDigitPressed)
        KeyboardRow(digits = listOf(4, 5, 6), onDigitPressed = onDigitPressed)
        KeyboardRow(digits = listOf(7, 8, 9), onDigitPressed = onDigitPressed)
        Row(
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            KeyboardButton(
                onClick = onClearPressed,
                content = {
                    Text(
                        text = "C",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            )
            KeyboardButton(
                onClick = { onDigitPressed(0) },
                content = {
                    Text(
                        text = "0",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            )
            KeyboardButton(
                onClick = onDeletePressed,
                content = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Delete"
                    )
                }
            )
        }
    }
}

@Composable
private fun KeyboardRow(digits: List<Int>, onDigitPressed: (Int) -> Unit) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        digits.forEach { digit ->
            KeyboardButton(
                onClick = { onDigitPressed(digit) },
                content = {
                    Text(
                        text = digit.toString(),
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            )
        }
    }
}

@Composable
private fun KeyboardButton(
    onClick: () -> Unit,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(64.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f), CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

@Preview(showBackground = true)
@Composable
private fun AuthScreenPreview() {
    RupartsTheme {
        AuthScreen(
            state = AuthUiState(pinCode = "123"),
            onDigitPressed = {},
            onClearPressed = {},
            onDeletePressed = {},
            snackbarHostState = SnackbarHostState()
        )
    }
}
