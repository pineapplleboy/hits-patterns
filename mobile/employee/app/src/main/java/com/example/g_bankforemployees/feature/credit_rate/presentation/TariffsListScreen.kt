package com.example.g_bankforemployees.feature.credit_rate.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.g_bankforemployees.R
import com.example.g_bankforemployees.common.navigation.AppNavigator
import com.example.g_bankforemployees.common.navigation.NavigatorHolder
import com.example.g_bankforemployees.common.presentation.component.BankTopBar
import com.example.g_bankforemployees.common.presentation.component.ErrorState
import com.example.g_bankforemployees.common.presentation.component.LoadingState
import com.example.g_bankforemployees.common.presentation.component.TariffCard

@Composable
fun TariffsListScreen(viewModel: TariffsListViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        (NavigatorHolder.navigator as? AppNavigator)?.setOnReturnFromCreditRateCreate {
            viewModel.load()
        }
    }

    TariffsListScreenContent(
        state = state,
        onBackClick = viewModel::onBackClick,
        onRetry = viewModel::load,
        onCreateRateClick = viewModel::onCreateRateClick,
    )
}

@Composable
private fun TariffsListScreenContent(
    state: TariffsListScreenState,
    onBackClick: () -> Unit,
    onRetry: () -> Unit,
    onCreateRateClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            BankTopBar(
                title = stringResource(R.string.tab_tariffs),
                onBackClick = onBackClick,
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateRateClick,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
            ) {
                Icon(Icons.Filled.Add, contentDescription = null)
            }
        },
        floatingActionButtonPosition = FabPosition.End,
    ) { paddingValues ->
        when (state) {
            TariffsListScreenState.Loading -> LoadingState()
            is TariffsListScreenState.Error -> ErrorState(
                title = stringResource(R.string.error),
                description = state.message,
                onRetry = onRetry,
            )
            is TariffsListScreenState.Default -> {
                if (state.creditRates.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.Center,
                    ) {}
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        items(state.creditRates) { rate ->
                            TariffCard(
                                name = rate.name,
                                percent = "${rate.percent}%",
                                writeOffPeriod = rate.writeOffPeriod,
                            )
                        }
                    }
                }
            }
        }
    }
}
