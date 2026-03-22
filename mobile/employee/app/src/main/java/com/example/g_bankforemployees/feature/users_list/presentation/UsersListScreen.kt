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
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Settings
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.g_bankforemployees.R
import com.example.g_bankforemployees.common.presentation.component.ErrorState
import com.example.g_bankforemployees.common.presentation.component.CommonTabRow
import com.example.g_bankforemployees.common.presentation.component.ListItemCard
import com.example.g_bankforemployees.common.presentation.component.LoadingState
import com.example.g_bankforemployees.common.presentation.theme.BankTheme
import com.example.g_bankforemployees.common.navigation.AppNavigator
import com.example.g_bankforemployees.common.navigation.NavigatorHolder
import com.example.g_bankforemployees.feature.users_list.domain.model.User
import org.koin.androidx.compose.koinViewModel

@Composable
fun UsersListScreen(viewModel: UsersListScreenViewModel) {
    val screenState by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        (NavigatorHolder.navigator as? AppNavigator)?.apply {
            setOnReturnFromUserCreate { viewModel.loadUsers() }
        }
    }

    UsersListScreenContent(
        screenState = screenState,
        onSelectedUsersTabIndexChange = viewModel::onSelectedUsersTabIndexChange,
        onTariffsClick = viewModel::onTariffsClick,
        onSettingsClick = viewModel::onSettingsClick,
        onUserClick = viewModel::onUserClick,
        onBanUser = viewModel::banUser,
        onUnbanUser = viewModel::unbanUser,
        onCreateUserClick = viewModel::onCreateUserClick,
        onCreateEmployeeClick = viewModel::onCreateEmployeeClick,
        onLogoutClick = viewModel::onLogoutClick,
        onRetry = viewModel::loadUsers,
    )
}

@Composable
private fun UsersListScreenContent(
    screenState: UsersListScreenState,
    onSelectedUsersTabIndexChange: (Int) -> Unit,
    onTariffsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onUserClick: (User) -> Unit,
    onBanUser: (User) -> Unit,
    onUnbanUser: (User) -> Unit,
    onCreateUserClick: () -> Unit,
    onCreateEmployeeClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onRetry: () -> Unit,
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        bottomBar = {
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
            ) {
                Button(
                    onClick = { onSelectedUsersTabIndexChange(0) },
                    modifier = Modifier.weight(1f).padding(horizontal = 4.dp),
                ) {
                    Icon(Icons.Filled.Person, contentDescription = null)
                    Text(
                        text = stringResource(R.string.tab_users),
                        style = MaterialTheme.typography.displayLarge.copy(fontSize = 10.sp, lineHeight = 14.sp),
                    )
                }
                Button(
                    onClick = onTariffsClick,
                    modifier = Modifier.weight(1f).padding(horizontal = 4.dp),
                ) {
                    Icon(Icons.Filled.Percent, contentDescription = null)
                    Text(
                        text = stringResource(R.string.tab_tariffs),
                        style = MaterialTheme.typography.displayLarge.copy(fontSize = 10.sp, lineHeight = 14.sp),
                    )
                }
                Button(
                    onClick = onSettingsClick,
                    modifier = Modifier.weight(1f).padding(horizontal = 4.dp),
                ) {
                    Icon(Icons.Filled.Settings, contentDescription = null)
                    Text(
                        text = stringResource(R.string.settings),
                        style = MaterialTheme.typography.displayLarge.copy(fontSize = 10.sp, lineHeight = 14.sp),
                    )
                }
            }
        },
        floatingActionButton = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                FloatingActionButton(
                    onClick = onCreateUserClick,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                ) {
                    Icon(Icons.Filled.Add, contentDescription = null)
                }
                FloatingActionButton(
                    onClick = onCreateEmployeeClick,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                ) {
                    Icon(Icons.Filled.PersonAdd, contentDescription = null)
                }
                FloatingActionButton(
                    onClick = onLogoutClick,
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
                DefaultState(
                    clients = state.clients,
                    employees = state.employees,
                    selectedTabIndex = state.selectedUsersTabIndex,
                    onSelectedTabIndexChange = onSelectedUsersTabIndexChange,
                    modifier = Modifier.padding(paddingValues),
                    onUserClick = onUserClick,
                    onBanUser = onBanUser,
                    onUnbanUser = onUnbanUser,
                )
            }
            is UsersListScreenState.Error -> ErrorState(
                title = stringResource(R.string.error),
                description = state.message,
                onRetry = onRetry,
            )
            UsersListScreenState.Loading -> LoadingState()
        }
    }
}

@Composable
private fun DefaultState(
    clients: List<User>,
    employees: List<User>,
    selectedTabIndex: Int,
    onSelectedTabIndexChange: (Int) -> Unit,
    modifier: Modifier,
    onUserClick: (User) -> Unit,
    onBanUser: (User) -> Unit,
    onUnbanUser: (User) -> Unit,
) {
    val tabTitles = listOf(stringResource(R.string.tab_clients), stringResource(R.string.tab_employees))
    val users = if (selectedTabIndex == 0) clients else employees

    Column(modifier = modifier) {
        CommonTabRow(
            titles = tabTitles,
            selectedTabIndex = selectedTabIndex,
            onSelectedTabIndexChange = onSelectedTabIndexChange,
        )

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
