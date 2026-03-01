package com.example.g_bankforemployees.feature.users_list.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.g_bankforemployees.R
import com.example.g_bankforemployees.common.presentation.component.ErrorState
import com.example.g_bankforemployees.common.presentation.component.ListItemCard
import com.example.g_bankforemployees.common.presentation.component.LoadingState
import com.example.g_bankforemployees.common.presentation.component.TariffCard
import com.example.g_bankforemployees.common.presentation.theme.BankTheme
import com.example.g_bankforemployees.common.navigation.AppNavigator
import com.example.g_bankforemployees.common.navigation.NavigatorHolder
import com.example.g_bankforemployees.feature.credit_rate.domain.model.CreditRate
import com.example.g_bankforemployees.feature.users_list.domain.model.User
import org.koin.androidx.compose.koinViewModel

@Composable
fun UsersListScreen(viewModel: UsersListScreenViewModel) {
    val screenState by viewModel.state.collectAsStateWithLifecycle()
    val creditRates by viewModel.creditRates.collectAsStateWithLifecycle()
    val tariffsError by viewModel.tariffsError.collectAsStateWithLifecycle()
    var selectedSectionIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        (NavigatorHolder.navigator as? AppNavigator)?.apply {
            setOnReturnFromUserCreate { viewModel.loadUsers() }
            setOnReturnFromCreditRateCreate { viewModel.loadTariffs() }
        }
    }
    LaunchedEffect(selectedSectionIndex) {
        if (selectedSectionIndex == 1) viewModel.loadTariffs()
    }

    UsersListScreenContent(
        screenState = screenState,
        creditRates = creditRates,
        tariffsError = tariffsError,
        selectedSectionIndex = selectedSectionIndex,
        onSelectedSectionIndexChange = { selectedSectionIndex = it },
        viewModel = viewModel,
    )
}

@Composable
private fun UsersListScreenContent(
    screenState: UsersListScreenState,
    creditRates: List<CreditRate>,
    tariffsError: String?,
    selectedSectionIndex: Int,
    onSelectedSectionIndexChange: (Int) -> Unit,
    viewModel: UsersListScreenViewModel,
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        bottomBar = {
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
            ) {
                Button(
                    onClick = { onSelectedSectionIndexChange(0) },
                    modifier = Modifier.weight(1f).padding(horizontal = 4.dp),
                ) {
                    Icon(Icons.Filled.Person, contentDescription = null)
                    Text(
                        text = stringResource(R.string.tab_users),
                        style = MaterialTheme.typography.displayLarge.copy(fontSize = 14.sp, lineHeight = 18.sp),
                    )
                }
                Button(
                    onClick = { onSelectedSectionIndexChange(1) },
                    modifier = Modifier.weight(1f).padding(horizontal = 4.dp),
                ) {
                    Icon(Icons.Filled.Percent, contentDescription = null)
                    Text(
                        text = stringResource(R.string.tab_tariffs),
                        style = MaterialTheme.typography.displayLarge.copy(fontSize = 14.sp, lineHeight = 18.sp),
                    )
                }
            }
        },
        floatingActionButton = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                FloatingActionButton(
                    onClick = { if (selectedSectionIndex == 0) viewModel.onCreateUserClick() else viewModel.onCreateRateClick() },
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                ) {
                    Icon(Icons.Filled.Add, contentDescription = null)
                }
                FloatingActionButton(
                    onClick = viewModel::onLogoutClick,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                ) {
                    Icon(Icons.Filled.ExitToApp, contentDescription = null)
                }
            }
        },
        floatingActionButtonPosition = FabPosition.End,
    ) { paddingValues ->
        when (val state = screenState) {
            is UsersListScreenState.Default -> {
                when (selectedSectionIndex) {
                    0 -> DefaultState(
                        clients = state.clients,
                        employees = state.employees,
                        modifier = Modifier.padding(paddingValues),
                        onUserClick = viewModel::onUserClick,
                        onBanUser = viewModel::banUser,
                        onUnbanUser = viewModel::unbanUser,
                    )
                    1 -> if (tariffsError != null) {
                        ErrorState(
                            title = stringResource(R.string.error),
                            description = tariffsError ?: "",
                            onRetry = {
                                viewModel.clearTariffsError()
                                viewModel.loadTariffs()
                            },
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.padding(paddingValues).fillMaxSize().padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            items(creditRates) { rate ->
                                TariffCard(
                                    name = rate.name,
                                    percent = "${rate.percent}%",
                                    writeOffPeriod = rate.writeOffPeriod,
                                )
                            }
                        }
                    }
                    else -> {}
                }
            }
            is UsersListScreenState.Error -> ErrorState(
                title = stringResource(R.string.error),
                description = state.message,
                onRetry = viewModel::loadUsers,
            )
            UsersListScreenState.Loading -> LoadingState()
            UsersListScreenState.Unauthorized -> {
                LaunchedEffect(Unit) { viewModel.onLogoutClick() }
            }
        }
    }
}

@Composable
private fun DefaultState(
    clients: List<User>,
    employees: List<User>,
    modifier: Modifier,
    onUserClick: (User) -> Unit,
    onBanUser: (User) -> Unit,
    onUnbanUser: (User) -> Unit,
) {
    val tabTitles = listOf(stringResource(R.string.tab_clients), stringResource(R.string.tab_employees))
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val users = if (selectedTabIndex == 0) clients else employees

    Column(modifier = modifier) {
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(text = title, style = MaterialTheme.typography.titleMedium) },
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(users) { user ->
                ListItemCard(onClick = { onUserClick(user) }) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = user.name ?: stringResource(R.string.name_empty),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                            user.phone?.let { phone ->
                                Text(
                                    text = phone,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                            if (user.ban) {
                                Text(
                                    text = stringResource(R.string.banned),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.error,
                                )
                            }
                        }
                        if (user.isBannable) {
                            Button(
                                onClick = { if (user.ban) onUnbanUser(user) else onBanUser(user) },
                            ) {
                                Text(
                                    text = if (user.ban) stringResource(R.string.unban) else stringResource(R.string.ban),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun UsersListScreenPreview() = BankTheme {
    UsersListScreen(viewModel = koinViewModel())
}
