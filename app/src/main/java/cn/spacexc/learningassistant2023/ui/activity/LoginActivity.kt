package cn.spacexc.learningassistant2023.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.AccountBox
import androidx.compose.material.icons.outlined.Password
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import cn.leancloud.LCUser
import cn.spacexc.learningassistant2023.R
import cn.spacexc.learningassistant2023.ui.theme.AppTheme
import cn.spacexc.learningassistant2023.ui.theme.gooLiBabaPuhuiSansFamily
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.launch

/* 
LearningAssistant-2023Remake Copyright (C) 2022 XC
This program comes with ABSOLUTELY NO WARRANTY.
This is free software, and you are welcome to redistribute it under certain conditions.
*/

/*
 * Created by XC on 2022/12/29.
 * I'm very cute so please be nice to my code!
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 */

class LoginActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                val snackBarHostState = remember { SnackbarHostState() }
                val scope = rememberCoroutineScope()
                Scaffold(snackbarHost = { SnackbarHost(snackBarHostState) }, topBar = {
                    LargeTopAppBar(
                        navigationIcon = {
                            IconButton(onClick = ::finish) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = stringResource(
                                        id = R.string.back
                                    )
                                )
                            }
                        },
                        title = {
                            Text(
                                text = stringResource(id = R.string.login_button_text),
                                fontFamily = gooLiBabaPuhuiSansFamily
                            )
                        })
                }) {
                    Column(
                        modifier = Modifier
                            .padding(it)
                            .padding(horizontal = 14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        var username by remember {
                            mutableStateOf("")
                        }
                        var password by remember {
                            mutableStateOf("")
                        }
                        var isPasswordVisible by remember {
                            mutableStateOf(false)
                        }
                        var isButtonEnable by remember {
                            mutableStateOf(true)
                        }
                        TextField(
                            value = username,
                            onValueChange = { username = it },
                            label = {
                                Text(
                                    text = stringResource(id = R.string.username)
                                )
                            }, leadingIcon = {
                                Icon(
                                    imageVector = Icons.Outlined.AccountBox,
                                    contentDescription = null
                                )
                            }, modifier = Modifier.fillMaxWidth(), singleLine = true
                        )
                        TextField(
                            value = password,
                            onValueChange = { password = it },
                            label = {
                                Text(
                                    text = stringResource(id = R.string.password)
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Outlined.Password,
                                    contentDescription = null
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            singleLine = true, trailingIcon = {
                                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                    Icon(
                                        imageVector = if (isPasswordVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                                        contentDescription = null
                                    )
                                }
                            }
                        )
                        Button(
                            onClick = {
                                if (username.isNotEmpty() && password.isNotEmpty()) {
                                    isButtonEnable = false
                                    LCUser.logIn(username, password)
                                        .subscribe(object : Observer<LCUser> {
                                            override fun onSubscribe(d: Disposable) {}
                                            override fun onNext(t: LCUser) {
                                                Intent(
                                                    this@LoginActivity,
                                                    MainActivity::class.java
                                                ).apply {
                                                    flags =
                                                        Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                                    startActivity(this)
                                                }
                                                Toast.makeText(
                                                    this@LoginActivity,
                                                    getString(R.string.login_successfully),
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }

                                            override fun onError(e: Throwable) {
                                                scope.launch {
                                                    snackBarHostState.showSnackbar(
                                                        message = getString(R.string.login_failed),
                                                        actionLabel = getString(R.string.confirm),
                                                        duration = SnackbarDuration.Short
                                                    )
                                                    isButtonEnable = true
                                                }
                                            }

                                            override fun onComplete() {}
                                        }
                                        )
                                }
                            },
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            enabled = isButtonEnable
                        ) {
                            Text(text = stringResource(id = R.string.login))
                        }
                    }
                }
            }
        }
    }
}