package uk.ac.tees.mad.carcare.ui.screens.signup

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.HowToReg
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import uk.ac.tees.mad.carcare.R
import uk.ac.tees.mad.carcare.model.dataclass.firebase.AuthResult

@Composable
fun SignUpScreen(
    modifier: Modifier = Modifier,
    popUp: () -> Unit,
    viewmodel: SignUpScreenViewModel = koinViewModel<SignUpScreenViewModel>()
) {
    val email by viewmodel.email.collectAsStateWithLifecycle()
    val password by viewmodel.password.collectAsStateWithLifecycle()
    val isPasswordVisible by viewmodel.isPasswordVisible.collectAsStateWithLifecycle()
    val isSignUpMode by viewmodel.isSignUpMode.collectAsStateWithLifecycle()
    val signUpResult by viewmodel.signUpResult.collectAsStateWithLifecycle()

    val focusManager = LocalFocusManager.current
    val focusRequesterEmail = remember { FocusRequester() }
    val focusRequesterPassword = remember { FocusRequester() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (!isSignUpMode) {
            when (val result = signUpResult) {
                is AuthResult.Loading -> {
                    AlertDialog(onDismissRequest = {
                        viewmodel.switchSignUpMode()
                    }, icon = {
                        Icon(
                            Icons.Default.CloudUpload,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }, title = {
                        Text(
                            text = "Signing Up",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }, text = {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .size(48.dp)
                                    .align(Alignment.CenterHorizontally)
                            )
                        }
                    }, confirmButton = { })
                }

                is AuthResult.Success -> {
                    // Handle successful sign-up
                    AlertDialog(icon = {
                        Icon(
                            Icons.Default.CloudDone,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }, title = {
                        Text(
                            text = "Sign Up Successful",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }, text = {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "You have successfully signed up.",
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }, confirmButton = {
                        TextButton(onClick = {
                            popUp()
                        }) {
                            Text(
                                text = "Go to Log In Screen", fontWeight = FontWeight.Bold
                            )
                        }
                    }, onDismissRequest = {
                        popUp()
                    })

                }

                is AuthResult.Error -> {
                    // Handle sign-up error
                    AlertDialog(icon = {
                        Icon(
                            Icons.Default.Error,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                    }, title = {
                        Text(
                            text = "Error",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }, text = {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(result.exception.message.toString())
                        }
                    }, confirmButton = {
                        TextButton(onClick = {
                            viewmodel.switchSignUpMode()
                        }) {
                            Text(text = "Retry?", fontWeight = FontWeight.Bold)
                        }
                    }, onDismissRequest = {
                        viewmodel.switchSignUpMode()
                    })
                }
            }
        }

        // App Logo and Name
        Image(
            painter = painterResource(id = R.drawable.carcareapp_logo),
            contentDescription = "App Logo",
            modifier = modifier.size(80.dp),
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground)
        )
        Text(
            text = stringResource(id = R.string.app_name),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            modifier = modifier.padding(top = 8.dp),
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = modifier.height(24.dp))

        Text(
            text = "Sign Up",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = modifier.height(24.dp))

        // Email TextField
        OutlinedTextField(
            value = email,
            modifier = modifier
                .fillMaxWidth(0.9f)
                .focusRequester(focusRequesterEmail),
            onValueChange = {
                viewmodel.updateEmail(it)
            },
            label = {
                Text(
                    text = "Email"
                )
            },
            leadingIcon = {
                Icon(
                    Icons.Filled.Email,
                    contentDescription = "Email",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email, imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(onNext = {
                focusRequesterPassword.requestFocus()
            }),
            shape = MaterialTheme.shapes.extraLarge,
            singleLine = true
        )

        Spacer(modifier = modifier.height(16.dp))

        // Password TextField
        OutlinedTextField(
            value = password,
            onValueChange = {
                viewmodel.updatePassword(it)
            },
            label = {
                Text(
                    text = "Password"
                )
            },
            leadingIcon = {
                Icon(
                    Icons.Filled.Lock,
                    contentDescription = "Password",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            },
            modifier = modifier
                .fillMaxWidth(0.9f)
                .focusRequester(focusRequesterPassword),
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password, imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = {
                focusManager.clearFocus()
            }),
            shape = MaterialTheme.shapes.extraLarge,
            singleLine = true,
            trailingIcon = {
                IconButton(onClick = {
                    viewmodel.togglePasswordVisibility()
                }) {
                    Icon(
                        imageVector = if (isPasswordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                        contentDescription = "Toggle Password Visibility",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            })

        Spacer(modifier = modifier.height(24.dp))

        // SignUp Button
        Button(
            enabled = email.isNotBlank() && password.isNotBlank(), onClick = {
                viewmodel.signUp(email, password)
                viewmodel.switchSignUpMode()
            }, modifier = modifier.fillMaxWidth(0.8f), shape = MaterialTheme.shapes.extraLarge
        ) {
            Icon(
                Icons.Default.HowToReg,
                contentDescription = null,
                modifier = modifier.size(24.dp),
            )
            Spacer(modifier = modifier.width(4.dp))
            Text(
                "Sign Up", style = MaterialTheme.typography.titleMedium
            )
        }

        Spacer(modifier = modifier.height(24.dp))

        Row(
            modifier = modifier.fillMaxWidth(0.9f),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Already have an account?", textAlign = TextAlign.Center
            )
            TextButton(
                onClick = {
                    popUp()
                }) {
                Icon(
                    Icons.AutoMirrored.Filled.Login,
                    contentDescription = null,
                    modifier = modifier.size(24.dp),
                )
                Spacer(modifier = modifier.width(4.dp))
                Text(
                    text = "Log In",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

}