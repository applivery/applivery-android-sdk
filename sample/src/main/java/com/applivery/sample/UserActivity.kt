package com.applivery.sample

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.applivery.android.sdk.Applivery
import com.applivery.android.sdk.domain.model.User
import com.applivery.android.sdk.getInstance
import com.applivery.android.sdk.user.BindUserCallback
import com.applivery.sample.theme.AppliveryTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class UserActivity : ComponentActivity() {

    private val userFlow = MutableStateFlow(emptyUser)

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            val user by userFlow.collectAsState()
            var tags by remember { mutableStateOf("") }
            UserScreen(
                user = user,
                tags = tags,
                onChangeFirstName = { updateUser { copy(firstName = it) } },
                onChangeLastName = { updateUser { copy(lastName = it) } },
                onChangeEmail = { updateUser { copy(email = it) } },
                onChangeTags = { tags = it },
                onBind = { bindUser(user, tags.split(",")) },
                onUnbind = { Applivery.getInstance().unbindUser() },
                onBack = ::finish
            )
        }

        lifecycleScope.launch {
            Applivery.getInstance().getUser().onSuccess { user -> userFlow.update { user } }
        }
    }

    private fun updateUser(user: User.() -> User) {
        userFlow.update { it.user() }
    }

    private fun bindUser(user: User, tags: List<String>) {

        Applivery.getInstance().bindUser(
            user.email.orEmpty(),
            user.firstName.orEmpty(),
            user.lastName.orEmpty(),
            tags,
            object : BindUserCallback {
                override fun onSuccess() {
                    Toast.makeText(
                        this@UserActivity,
                        "User bound",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onError(message: String) {
                    Toast.makeText(
                        this@UserActivity,
                        "Error binding user: $message",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    companion object {

        internal val emptyUser = User(
            id = UUID.randomUUID().toString(),
            email = null,
            firstName = null,
            lastName = null,
            fullName = null,
            type = null,
            createdAt = null
        )

        fun open(context: Context) {
            val intent = Intent(context, UserActivity::class.java)
            context.startActivity(intent)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserScreen(
    user: User?,
    tags: String,
    onChangeFirstName: (String) -> Unit,
    onChangeLastName: (String) -> Unit,
    onChangeEmail: (String) -> Unit,
    onChangeTags: (String) -> Unit,
    onBind: () -> Unit,
    onUnbind: () -> Unit,
    onBack: () -> Unit
) {
    AppliveryTheme {
        Scaffold(
            topBar = {
                val containerColor = MaterialTheme.colorScheme.primaryContainer
                val contentColor = contentColorFor(containerColor)
                TopAppBar(
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Default.ArrowBack,
                                contentDescription = null
                            )
                        }
                    },
                    title = { Text(text = stringResource(id = R.string.app_name)) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = containerColor,
                        titleContentColor = contentColor,
                        actionIconContentColor = contentColor,
                        navigationIconContentColor = contentColor
                    )
                )
            },
            content = { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 24.dp)
                ) {

                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = user?.firstName.orEmpty(),
                        onValueChange = onChangeFirstName,
                        label = { Text(text = stringResource(id = R.string.first_name)) },
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = user?.lastName.orEmpty(),
                        onValueChange = onChangeLastName,
                        label = { Text(text = stringResource(id = R.string.last_name)) },
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = user?.email.orEmpty(),
                        onValueChange = onChangeEmail,
                        label = { Text(text = stringResource(id = R.string.email)) },
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = tags,
                        onValueChange = onChangeTags,
                        label = { Text(text = stringResource(id = R.string.tags)) },
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Button(
                            modifier = Modifier.weight(1f),
                            onClick = onBind
                        ) {
                            Text(text = stringResource(id = R.string.bind_user))
                        }

                        Spacer(modifier = Modifier.width(24.dp))

                        Button(
                            modifier = Modifier.weight(1f),
                            onClick = onUnbind
                        ) {
                            Text(text = stringResource(id = R.string.unbind_user))
                        }
                    }
                }
            }
        )
    }
}

@Preview
@Composable
private fun UserScreenPreview() {
    UserScreen(
        user = UserActivity.emptyUser,
        tags = "",
        onChangeFirstName = { },
        onChangeLastName = { },
        onChangeEmail = { },
        onChangeTags = { },
        onBind = { },
        onUnbind = { },
        onBack = { }
    )
}