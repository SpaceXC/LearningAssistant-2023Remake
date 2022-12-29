package cn.spacexc.learningassistant2023.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import cn.leancloud.LCUser
import cn.spacexc.learningassistant2023.R
import cn.spacexc.learningassistant2023.VERSION_NAME
import cn.spacexc.learningassistant2023.entity.toProblemEntity
import cn.spacexc.learningassistant2023.ui.component.ProblemCard
import cn.spacexc.learningassistant2023.ui.theme.AppTheme
import cn.spacexc.learningassistant2023.ui.theme.gooLiBabaPuhuiSansFamily
import cn.spacexc.learningassistant2023.viewmodel.ProblemListViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    sealed class Screen(
        val route: String,
        @StringRes val destinationString: Int,
        val icon: ImageVector
    ) {
        object List : Screen(
            route = "list",
            destinationString = R.string.problem_list,
            icon = Icons.Default.List
        )

        object Folder : Screen(
            route = "folder",
            destinationString = R.string.folder,
            icon = Icons.Default.Folder
        )

        object Profile : Screen(
            route = "profile",
            destinationString = R.string.profile,
            icon = Icons.Default.AccountCircle
        )
    }

    private val navItems = listOf(Screen.List, Screen.Folder, Screen.Profile)

    private val viewModel: ProblemListViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            AppTheme {
                Scaffold(
                    bottomBar = {
                        NavigationBar {
                            val navBackEntry by navController.currentBackStackEntryAsState()
                            val currentDestination = navBackEntry?.destination
                            navItems.forEach { screen ->
                                NavigationBarItem(
                                    icon = {
                                        Icon(
                                            imageVector = screen.icon,
                                            contentDescription = stringResource(
                                                id = screen.destinationString
                                            )
                                        )
                                    },
                                    label = {
                                        Text(text = stringResource(id = screen.destinationString))
                                    },
                                    selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                                    onClick = {
                                        navController.navigate(screen.route) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                )
                            }
                        }
                    },
                    modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Screen.List.route,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(Screen.List.route) { ProblemListPage() }
                        composable(Screen.Folder.route) { FolderPage() }
                        composable(Screen.Profile.route) { ProfilePage() }
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ProblemListPage() {
        var menuExpanded by remember { mutableStateOf(false) }
        var deleteAllDialogShowing by remember {
            mutableStateOf(false)
        }
        val problemList by viewModel.problemList
        val snackBarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()
        var isListDescended by remember {
            mutableStateOf(true)
        }
        if (deleteAllDialogShowing) {
            AlertDialog(onDismissRequest = { deleteAllDialogShowing = false }, confirmButton = {
                TextButton(onClick = { deleteAllDialogShowing = false }) {
                    Text(text = stringResource(id = R.string.confirm))
                }
            }, dismissButton = {
                TextButton(onClick = { deleteAllDialogShowing = false }) {
                    Text(text = stringResource(id = R.string.cancel))
                }
            }, title = {
                Text(
                    text = stringResource(id = R.string.delete_confrimation),
                    fontFamily = gooLiBabaPuhuiSansFamily
                )
            }, icon = {
                Icon(imageVector = Icons.Outlined.Delete, contentDescription = null)
            }, text = {
                Text(
                    text = stringResource(id = R.string.delete_confrimation_description),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            })
        }
        Scaffold(
            snackbarHost = { SnackbarHost(snackBarHostState) },
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            stringResource(id = R.string.problem_list),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontFamily = gooLiBabaPuhuiSansFamily
                        )
                    },
                    actions = {
                        Box(
                            modifier = Modifier
                                .wrapContentSize(Alignment.TopStart)
                        ) {
                            IconButton(onClick = { menuExpanded = true }) {
                                Icon(Icons.Default.MoreVert, contentDescription = "More")
                            }
                            DropdownMenu(
                                expanded = menuExpanded,
                                onDismissRequest = { menuExpanded = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text(stringResource(id = R.string.delete_all)) },
                                    onClick = {
                                        deleteAllDialogShowing = true
                                        menuExpanded = false
                                    },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Outlined.Delete,
                                            contentDescription = null
                                        )
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text(stringResource(id = R.string.sync)) },
                                    onClick = {
                                        menuExpanded = false
                                        viewModel.getProblems {
                                            scope.launch {
                                                snackBarHostState.showSnackbar(
                                                    message = getString(
                                                        if (it) R.string.sync_complete else R.string.sync_failed
                                                    ),
                                                    actionLabel = getString(R.string.confirm),
                                                    duration = SnackbarDuration.Short
                                                )
                                            }
                                        }
                                    },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Outlined.Sync,
                                            contentDescription = null
                                        )
                                    }
                                )
                                Divider()
                                DropdownMenuItem(
                                    text = { Text(stringResource(id = R.string.change_order)) },
                                    onClick = {
                                        isListDescended = !isListDescended
                                    },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Outlined.Sort,
                                            contentDescription = null
                                        )
                                    },
                                    trailingIcon = {
                                        Text(
                                            if (isListDescended) stringResource(id = R.string.descend) else stringResource(
                                                id = R.string.ascend
                                            ), textAlign = TextAlign.Center
                                        )
                                    }
                                )

                            }
                        }
                    }
                )
            }, floatingActionButton = {
                FloatingActionButton(onClick = { }) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Problem"
                    )
                }
            }
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(
                    vertical = 8.dp,
                    horizontal = 14.dp
                )
            ) {
                (if (isListDescended) problemList.asReversed() else problemList).forEach {
                    val problem = it.toProblemEntity()
                    item {
                        ProblemCard(
                            problemTitle = problem.problemSource,
                            subject = problem.subject,
                            updateTime = problem.updateTimeString,
                            rate = problem.problemRate.toFloat()
                        )
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun FolderPage() {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            stringResource(id = R.string.folder),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontFamily = gooLiBabaPuhuiSansFamily
                        )
                    }
                )
            }, floatingActionButton = {
                FloatingActionButton(onClick = { }) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(id = R.string.create_folder)
                    )
                }
            }
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {

            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ProfilePage() {
        var isLogoutDialogShowing by remember {
            mutableStateOf(false)
        }
        val snackBarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()
        if (isLogoutDialogShowing) {
            AlertDialog(onDismissRequest = { isLogoutDialogShowing = false }, title = {
                Text(
                    text = stringResource(
                        id = R.string.logout
                    ), fontFamily = gooLiBabaPuhuiSansFamily
                )
            }, text = {
                Text(
                    text = stringResource(id = R.string.logout_confirmation),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }, icon = {
                Icon(imageVector = Icons.Filled.Logout, contentDescription = null)
            }, confirmButton = {
                TextButton(onClick = {
                    isLogoutDialogShowing = false
                    LCUser.logOut()
                    Intent(this, LoginActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(this)
                    }
                    scope.launch {
                        snackBarHostState.showSnackbar(
                            message = getString(R.string.logout_successfully),
                            actionLabel = getString(R.string.confirm),
                            duration = SnackbarDuration.Short
                        )
                    }
                }) {
                    Text(text = stringResource(id = R.string.confirm))
                }
            }, dismissButton = {
                TextButton(onClick = { isLogoutDialogShowing = false }) {
                    Text(text = stringResource(id = R.string.cancel))
                }
            })
        }
        Scaffold(
            snackbarHost = { SnackbarHost(snackBarHostState) },
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            stringResource(id = R.string.profile),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontFamily = gooLiBabaPuhuiSansFamily
                        )
                    },
                    actions = {
                        LCUser.currentUser()?.let {
                            IconButton(onClick = {
                                isLogoutDialogShowing = true
                            }) {
                                Icon(
                                    imageVector = Icons.Filled.Logout,
                                    contentDescription = stringResource(id = R.string.logout)
                                )
                            }
                        }
                    },
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it), verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (LCUser.currentUser() == null) {
                    Button(onClick = {
                        startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                    }, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                        Text(text = stringResource(id = R.string.login_button_text))
                    }
                }

                LCUser.currentUser()?.let {
                    ListItem(leadingContent = {
                        Icon(imageVector = Icons.Outlined.AccountCircle, contentDescription = null)
                    }, headlineText = {
                        Text(
                            text = stringResource(id = R.string.current_user_text),
                            fontSize = 18.sp
                        )
                    }, supportingText = {
                        Text(text = it.username)
                    }, trailingContent = {
                        Icon(imageVector = Icons.Default.ArrowForwardIos, contentDescription = null)
                    }, modifier = Modifier.clickable { })
                }

                ListItem(leadingContent = {
                    Icon(imageVector = Icons.Outlined.Info, contentDescription = null)
                }, headlineText = {
                    Text(text = stringResource(id = R.string.version_text), fontSize = 18.sp)
                }, supportingText = {
                    Text(text = VERSION_NAME)
                })
            }
        }
    }
}